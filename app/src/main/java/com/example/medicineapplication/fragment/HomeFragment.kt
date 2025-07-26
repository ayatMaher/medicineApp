package com.example.medicineapplication.fragment

import android.annotation.SuppressLint
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.medicineapplication.AddingPrescriptionActivity
import com.example.medicineapplication.LogInActivity
import com.example.medicineapplication.MedicineDetailsActivity
import com.example.medicineapplication.NotificationsActivity
import com.example.medicineapplication.PharmacyDetailsActivity
import com.example.medicineapplication.R
import com.example.medicineapplication.adapter.CategoryAdapter
import com.example.medicineapplication.adapter.MedicineAdapter
import com.example.medicineapplication.adapter.PharmacyAdapter
import com.example.medicineapplication.api.ApiClient
import com.example.medicineapplication.api.ApiService
import com.example.medicineapplication.databinding.FragmentHomeBinding
import com.example.medicineapplication.model.Category
import com.example.medicineapplication.model.FavoriteMedicineRequest
import com.example.medicineapplication.model.FavoriteMedicineResponse
import com.example.medicineapplication.model.FavoritePharmacyRequest
import com.example.medicineapplication.model.FavoritePharmacyResponse
import com.example.medicineapplication.model.FavoriteTreatmentResponse
import com.example.medicineapplication.model.GeneralResponse
import com.example.medicineapplication.model.MedicinesWithCategoryResponse
import com.example.medicineapplication.model.Pharmacy
import com.example.medicineapplication.model.PharmacyResponse
import com.example.medicineapplication.model.Treatment
import com.example.medicineapplication.model.UserResponse
import com.example.medicineapplication.model.ViewCategoriesResponse
import com.google.firebase.messaging.FirebaseMessaging
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("DEPRECATION")
class HomeFragment : Fragment(), CategoryAdapter.ItemClickListener,
    PharmacyAdapter.ItemClickListener, MedicineAdapter.ItemClickListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var categoryAdapter: CategoryAdapter
    private var items: ArrayList<Category> = ArrayList()

    private lateinit var pharmacyHomeAdapter: PharmacyAdapter
    private var pharmacyItems: ArrayList<Pharmacy> = ArrayList()

    private var token: String = " "
    private var userId: Int = -1


    private lateinit var medicineAdapter: MedicineAdapter
    private var medicineItems: ArrayList<Treatment> = ArrayList()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val sharedPref =
            requireActivity().getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        token = sharedPref.getString("ACCESS_TOKEN", "") ?: ""
        userId = sharedPref.getInt("USER_ID", -1)


        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val fcmToken = task.result
                Log.d("FCM_TOKEN", "Token: $fcmToken")
                Log.d("hhhhhhhhhhhhhhhhh", "Token: $fcmToken")
                sendFcmTokenToServer(fcmToken)

            } else {
                Log.e("FCM_TOKEN", "فشل في جلب التوكن", task.exception)
                Log.d("hhhhhhhhhhhhhhhhh", "Token:")
            }
        }



        // تغيير لون status bar
        val window = requireActivity().window
        window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.light_green)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
        // add user name
        loadUserName()
        // search icon
        binding.searchIcon.setOnClickListener {
            val bundle = Bundle().apply {
                putString("page_type", "search")
            }
            findNavController().navigate(R.id.navigation_pharmacies, bundle)
        }

        // notification icon
        binding.notificationIcon.setOnClickListener {
            val intent = Intent(requireContext(), NotificationsActivity::class.java)
            startActivity(intent)
        }

        // join as pharmacy button
        binding.btnJoin.setOnClickListener {
            val url = "https://demoapplication.jawebhom.com/where-my-treatment-app"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = url.toUri()
            startActivity(intent)
        }

        // upload Prescription
        binding.btnUpload.setOnClickListener {
            val intent = Intent(requireContext(), AddingPrescriptionActivity::class.java)
            startActivity(intent)
        }

        showMedicineType()
        showPharmacy()
        showMedicine()

        return root
    }

    private fun showMedicine() {
        medicineItems.clear()

        ApiClient.apiService.getTopTreatment(token)
            .enqueue(object : Callback<MedicinesWithCategoryResponse> {
                override fun onResponse(
                    call: Call<MedicinesWithCategoryResponse>,
                    response: Response<MedicinesWithCategoryResponse>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        val allMedicines = response.body()?.data ?: emptyList()

                        // الآن اجلب المفضلة
                        ApiClient.apiService.getFavoriteMedicines("Bearer $token")
                            .enqueue(object : Callback<FavoriteTreatmentResponse> {
                                @SuppressLint("NotifyDataSetChanged")
                                override fun onResponse(
                                    call2: Call<FavoriteTreatmentResponse>,
                                    response2: Response<FavoriteTreatmentResponse>
                                ) {
                                    if (response2.isSuccessful && response2.body()?.success == true) {
                                        val favoriteIds =
                                            response2.body()?.data?.map { it.treatment.id }
                                                ?: emptyList()

                                        // حدث قيمة is_favorite
                                        val updatedList = allMedicines.map {
                                            it.copy(is_favorite = favoriteIds.contains(it.id))
                                        }

                                        medicineItems.addAll(updatedList)
                                        medicineAdapter.notifyDataSetChanged()
                                    }
                                }

                                override fun onFailure(
                                    call2: Call<FavoriteTreatmentResponse>,
                                    t: Throwable
                                ) {
                                }
                            })
                    }
                }

                override fun onFailure(call: Call<MedicinesWithCategoryResponse>, t: Throwable) {}
            })

        medicineAdapter = MedicineAdapter(requireActivity(), medicineItems, this@HomeFragment)
        binding.rvMedicine.adapter = medicineAdapter
    }

    private fun showMedicineType() {
        items.clear()
        ApiClient.apiService.viewCategories(token)
            .enqueue(object : Callback<ViewCategoriesResponse> {
                override fun onResponse(
                    call: Call<ViewCategoriesResponse>,
                    response: Response<ViewCategoriesResponse>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {

                        val categories = response.body()?.data ?: emptyList()
                        val updatedList = categories.map {
                            it.copy(isFeatured = false)
                        }
                        items.addAll(updatedList)
                        categoryAdapter =
                            CategoryAdapter(requireActivity(), items, this@HomeFragment)
                        binding.rvMedicinetype.layoutManager =
                            GridLayoutManager(requireContext(), 4)
                        binding.rvMedicinetype.adapter = categoryAdapter
                    } else {
                        Toast.makeText(
                            requireContext(),
                            " لم يتم العثور على نتائج الانواع",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ViewCategoriesResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "فشل الاتصال: ${t.message}", Toast.LENGTH_LONG)
                        .show()
                }
            })

    }

    private fun showPharmacy() {
        pharmacyItems.clear()

        ApiClient.apiService.nearbyPharmacies(token).enqueue(object : Callback<PharmacyResponse> {
            override fun onResponse(
                call: Call<PharmacyResponse>,
                response: Response<PharmacyResponse>
            ) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val pharmacies = response.body()?.data ?: emptyList()
                    pharmacyItems.addAll(pharmacies)
                    pharmacyHomeAdapter =
                        PharmacyAdapter(requireActivity(), pharmacyItems, this@HomeFragment)
                    binding.rvPharmacy.adapter = pharmacyHomeAdapter
                }
            }

            override fun onFailure(call: Call<PharmacyResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "فشل الاتصال: ${t.message}", Toast.LENGTH_LONG)
                    .show()
            }
        })
    }

    private fun sendFcmTokenToServer(fcmToken: String) {
        if (token.isEmpty()) return

        val body = mapOf("fcm_token" to fcmToken)
        ApiClient.apiService.updateDeviceToken("Bearer $token", body)
            .enqueue(object : Callback<GeneralResponse> {
                override fun onResponse(
                    call: Call<GeneralResponse>,
                    response: Response<GeneralResponse>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        Log.d("FCM_UPDATE", "تم إرسال التوكن بنجاح")
                    } else {
                        Log.e("FCM_UPDATE", "فشل إرسال التوكن: ${response.code()}")
                        Log.e("FCM_UPDATE", "فشل إرسال التوكن: ${token}")
                    }
                }

                override fun onFailure(call: Call<GeneralResponse>, t: Throwable) {
                    Log.e("FCM_UPDATE", "فشل الاتصال بالسيرفر: ${t.message}")
                }
            })
    }

    private fun addPharmacyToFavorite(pharmacyId: Int) {
        if (token.isEmpty() || userId == -1) {
            Toast.makeText(requireContext(), "يرجى تسجيل الدخول أولاً", Toast.LENGTH_SHORT).show()
            return
        }
        val request = FavoritePharmacyRequest(userId, pharmacyId)
        ApiClient.apiService.storeFavorite(token, request)
            .enqueue(object : Callback<FavoritePharmacyResponse> {
                override fun onResponse(
                    call: Call<FavoritePharmacyResponse>,
                    response: Response<FavoritePharmacyResponse>
                ) {
                    val errorBody = response.errorBody()?.string()

                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(
                            requireContext(),
                            "تمت الإضافة إلى المفضلة",
                            Toast.LENGTH_SHORT
                        ).show()
                        // ✅ تحديث حالة isFavorite في القائمة وإبلاغ الـ Adapter
                        val index = pharmacyItems.indexOfFirst { it.id == pharmacyId }
                        if (index != -1) {
                            pharmacyItems[index].is_favorite = true
                            pharmacyHomeAdapter.notifyItemChanged(index)
                        }
                    } else {
                        val errorMessage = try {
                            val json = JSONObject(errorBody ?: "")
                            json.optJSONObject("data")?.optString("error")
                                ?: "فشل في الإضافة للمفضلة"
                        } catch (e: Exception) {
                            "فشل في الإضافة للمفضلة${e.message}"
                        }
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<FavoritePharmacyResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "خطأ: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun addMedicineToFavorite(medicineId: Int) {

        if (token.isEmpty() || userId == -1) {
            Toast.makeText(requireContext(), "يرجى تسجيل الدخول أولاً", Toast.LENGTH_SHORT).show()
            return
        }

        val request = FavoriteMedicineRequest(userId, medicineId)

        ApiClient.apiService.storFavoriteMedicine(token, request)
            .enqueue(object : Callback<FavoriteMedicineResponse> {
                override fun onResponse(
                    call: Call<FavoriteMedicineResponse>,
                    response: Response<FavoriteMedicineResponse>
                ) {
                    val errorBody = response.errorBody()?.string()

                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(
                            requireContext(),
                            "تمت الإضافة إلى المفضلة",
                            Toast.LENGTH_SHORT
                        ).show()

                        // ✅ تحديث حالة isFavorite في القائمة وإبلاغ الـ Adapter
                        val index = medicineItems.indexOfFirst { it.id == medicineId }
                        if (index != -1) {
                            medicineItems[index].is_favorite = true
                            medicineAdapter.notifyItemChanged(index)
                        }

                    } else {
                        val errorMessage = try {
                            val json = JSONObject(errorBody ?: "")
                            json.optJSONObject("data")?.optString("error")
                                ?: "فشل في الإضافة للمفضلة"
                        } catch (e: Exception) {
                            "${e.message}فشل في الإضافة للمفضلة"
                        }
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<FavoriteMedicineResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "خطأ: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun loadUserName() {
        val sharedPref = requireActivity().getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val token = "Bearer " + sharedPref.getString("ACCESS_TOKEN", "")

        val apiService = ApiClient.instance.create(ApiService::class.java)
        apiService.getCurrentUser(token).enqueue(object : Callback<UserResponse> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val user = response.body()!!.data
                    // show user name
                    binding.userName.text = "مرحبا, ${user.name}"
                    //show uaer image
                     user.image?.let {
                        Glide.with(this@HomeFragment)
                            .load(it)
                            .placeholder(R.drawable.user)
                            .into(binding.profileImage)
                    }

                } else {
                    val errorBody = response.errorBody()?.string()

                    if (errorBody != null && errorBody.trim().startsWith("{")) {
                        try {
                            val json = JSONObject(errorBody)
                            val errorMessage = json.optString("message", "حدث خطأ")

                            Toast.makeText(
                                requireContext(),
                                errorMessage,
                                Toast.LENGTH_SHORT
                            ).show()

                            if (errorMessage.contains("غير مصرح")) {
                                // 🧹 حذف التوكن
                                val sharedPref = requireActivity().getSharedPreferences(
                                    "MyAppPrefs",
                                    MODE_PRIVATE
                                )
                                sharedPref.edit { clear() }

                                // 🔁 إعادة التوجيه إلى تسجيل الدخول
                                val intent =
                                    Intent(requireContext(), LogInActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                requireActivity().finish()
                            }

                        } catch (e: Exception) {
                            Toast.makeText(
                                requireContext(),
                                e.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "فشل في الاتصال بالخادم",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Toast.makeText(
                    requireContext(),
                    "فشل الاتصال: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    // category click
    override fun onItemClick(position: Int, id: String) {
        val categoryName = items[position].name
        val categoryId = items[position].id
        val bundle = Bundle().apply {
            putString("category_name", categoryName)
            putString("category_id", categoryId.toString())
            putString("page_type", "medicine_type")
        }
        findNavController().navigate(R.id.navigation_medicines, bundle)

    }

    // pharmacy click
    override fun onItemClickPharmacy(position: Int, id: String) {
        val pharmacy = pharmacyItems[position]
        val intent = Intent(requireContext(), PharmacyDetailsActivity::class.java)
        intent.putExtra("pharmacy", pharmacy)
        startActivity(intent)
    }

    // medicine click
    override fun onItemClickMedicine(position: Int, id: String) {
        val medicine = medicineItems[position]
        val intent = Intent(requireContext(), MedicineDetailsActivity::class.java)
        intent.putExtra("pharmacy_name","")
        intent.putExtra("medicine", medicine)
        startActivity(intent)
    }

    // تنفيذ دالة إضافة المفضلة من Adapter
    override fun onAddToFavorite(pharmacyId: Int) {
        addPharmacyToFavorite(pharmacyId)
    }


    override fun onAddMedicineToFavorite(medicineId: Int) {
        addMedicineToFavorite(medicineId)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}