package com.example.medicineapplication.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.medicineapplication.AddingPrescriptionActivity
import com.example.medicineapplication.MedicineDetailsActivity
import com.example.medicineapplication.NotificationsActivity
import com.example.medicineapplication.PharmacyDetailsActivity
import com.example.medicineapplication.R
import com.example.medicineapplication.adapter.CategoryAdapter
import com.example.medicineapplication.adapter.MedicineAdapter
import com.example.medicineapplication.adapter.PharmacyAdapter
import com.example.medicineapplication.databinding.FragmentHomeBinding
import com.example.medicineapplication.model.MedicineType
import androidx.core.net.toUri
import com.example.medicineapplication.api.ApiClient
import com.example.medicineapplication.fragment.MedicineFragment
import com.example.medicineapplication.model.Category
import com.example.medicineapplication.model.FavoriteMedicineRequest
import com.example.medicineapplication.model.FavoriteMedicineResponse
import com.example.medicineapplication.model.FavoritePharmacyRequest
import com.example.medicineapplication.model.FavoritePharmacyResponse
import com.example.medicineapplication.model.FavoriteTreatmentResponse
import com.example.medicineapplication.model.Medicine
import com.example.medicineapplication.model.MedicinesWithCategoryResponse
import com.example.medicineapplication.model.Pharmacy
import com.example.medicineapplication.model.PharmacyResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import com.example.medicineapplication.model.Treatment
import com.example.medicineapplication.model.ViewCategoriesResponse
import kotlin.math.log


@Suppress("DEPRECATION")
class HomeFragment : Fragment(), CategoryAdapter.ItemClickListener,
    PharmacyAdapter.ItemClickListener, MedicineAdapter.ItemClickListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var categoryAdapter: CategoryAdapter
    private var items: ArrayList<Category> = ArrayList()

    private lateinit var pharmacyHomeAdapter: PharmacyAdapter
    private var pharmacy_items: ArrayList<Pharmacy> = ArrayList()

    private var token: String=" "
    private var userId: Int=-1

    private lateinit var medicineAdapter: MedicineAdapter
    private var medicine_items: ArrayList<Treatment> = ArrayList()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val sharedPref = requireActivity().getSharedPreferences("MyAppPrefs", AppCompatActivity.MODE_PRIVATE)
        token = sharedPref.getString("ACCESS_TOKEN", "") ?: ""
        userId = sharedPref.getInt("USER_ID", -1)

        // تغيير لون status bar
        val window = requireActivity().window
        window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.light_green)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

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
        medicine_items.clear()

        ApiClient.apiService.getTopTreatment(token)
            .enqueue(object : Callback<MedicinesWithCategoryResponse> {
                override fun onResponse(call: Call<MedicinesWithCategoryResponse>, response: Response<MedicinesWithCategoryResponse>) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        val allMedicines = response.body()?.data ?: emptyList()

                        // الآن اجلب المفضلة
                        ApiClient.apiService.getFavoriteMedicines("Bearer $token")
                            .enqueue(object : Callback<FavoriteTreatmentResponse> {
                                override fun onResponse(call2: Call<FavoriteTreatmentResponse>, response2: Response<FavoriteTreatmentResponse>) {
                                    if (response2.isSuccessful && response2.body()?.success == true) {
                                        val favoriteIds = response2.body()?.data?.map { it.treatment.id } ?: emptyList()

                                        // حدث قيمة is_favorite
                                        val updatedList = allMedicines.map {
                                            it.copy(is_favorite = favoriteIds.contains(it.id))
                                        }

                                        medicine_items.addAll(updatedList)
                                        medicineAdapter.notifyDataSetChanged()
                                    }
                                }

                                override fun onFailure(call2: Call<FavoriteTreatmentResponse>, t: Throwable) {}
                            })
                    }
                }

                override fun onFailure(call: Call<MedicinesWithCategoryResponse>, t: Throwable) {}
            })

        medicineAdapter = MedicineAdapter(requireActivity(), medicine_items, this@HomeFragment)
        binding.rvMedicine.adapter = medicineAdapter
    }



    private fun showMedicineType() {
        items.clear()
        ApiClient.apiService.viewCategories(token)
            .enqueue(object : Callback<ViewCategoriesResponse> {
                override fun onResponse(call: Call<ViewCategoriesResponse>, response: Response<ViewCategoriesResponse>) {
                    if (response.isSuccessful && response.body()?.success == true) {

                        val categories = response.body()?.data ?: emptyList()
                        val updatedList = categories.map {
                            it.copy(isFeatured=false)
                        }
                        items.addAll(updatedList)
                        Toast.makeText(requireContext(), "تم العثور على نتائج الانواع", Toast.LENGTH_SHORT).show()
                        categoryAdapter = CategoryAdapter(requireActivity(), items, this@HomeFragment)
                        binding.rvMedicinetype.layoutManager = GridLayoutManager(requireContext(), 4)
                        binding.rvMedicinetype.adapter = categoryAdapter
                    }else {
                        Toast.makeText(requireContext(), " لم يتم العثور على نتائج الانواع", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ViewCategoriesResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "فشل الاتصال: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })

    }

    private fun showPharmacy() {
        pharmacy_items.clear()

        ApiClient.apiService.nearbyPharmacies(token).enqueue(object : Callback<PharmacyResponse> {
            override fun onResponse(call: Call<PharmacyResponse>, response: Response<PharmacyResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val pharmacies = response.body()?.data ?: emptyList()
                    pharmacy_items.addAll(pharmacies)
                    pharmacyHomeAdapter = PharmacyAdapter(requireActivity(), pharmacy_items, this@HomeFragment)
                    binding.rvPharmacy.adapter = pharmacyHomeAdapter
                }
            }

            override fun onFailure(call: Call<PharmacyResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "فشل الاتصال: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun addPharmacyToFavorite(pharmacyId: Int) {

        if (token.isEmpty() || userId == -1) {
            Toast.makeText(requireContext(), "يرجى تسجيل الدخول أولاً", Toast.LENGTH_SHORT).show()
            return
        }

        val request = FavoritePharmacyRequest(userId, pharmacyId)

        ApiClient.apiService.storeFavorite( token, request)
            .enqueue(object : Callback<FavoritePharmacyResponse> {
                override fun onResponse(
                    call: Call<FavoritePharmacyResponse>,
                    response: Response<FavoritePharmacyResponse>
                ) {
                    val errorBody = response.errorBody()?.string()

                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(requireContext(), "تمت الإضافة إلى المفضلة", Toast.LENGTH_SHORT).show()

                        // ✅ تحديث حالة isFavorite في القائمة وإبلاغ الـ Adapter
                        val index = pharmacy_items.indexOfFirst { it.id == pharmacyId }
                        if (index != -1) {
                            pharmacy_items[index].is_favorite = true
                            pharmacyHomeAdapter.notifyItemChanged(index)
                        }

                    } else {
                        val errorMessage = try {
                            val json = org.json.JSONObject(errorBody ?: "")
                            json.optJSONObject("data")?.optString("error") ?: "فشل في الإضافة للمفضلة"
                        } catch (e: Exception) {
                            e.message
                        }

                        Log.e("FavoriteError", "Response code: ${response.code()}, Error body: $errorBody")
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

        ApiClient.apiService.storFavoriteMedicine( token, request)
            .enqueue(object : Callback<FavoriteMedicineResponse> {
                override fun onResponse(
                    call: Call<FavoriteMedicineResponse>,
                    response: Response<FavoriteMedicineResponse>
                ) {
                    val errorBody = response.errorBody()?.string()

                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(requireContext(), "تمت الإضافة إلى المفضلة", Toast.LENGTH_SHORT).show()

                        // ✅ تحديث حالة isFavorite في القائمة وإبلاغ الـ Adapter
                        val index = medicine_items.indexOfFirst { it.id == medicineId }
                        if (index != -1) {
                            medicine_items[index].is_favorite = true
                            medicineAdapter.notifyItemChanged(index)
                        }

                    } else {
                        val errorMessage = try {
                            val json = org.json.JSONObject(errorBody ?: "")
                            json.optJSONObject("data")?.optString("error") ?: "فشل في الإضافة للمفضلة"
                        } catch (e: Exception) {
                            "فشل في الإضافة للمفضلة"
                        }

                        Log.e("FavoriteError", "Response code: ${response.code()}, Error body: $errorBody")
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<FavoriteMedicineResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "خطأ: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }



    // category click
    override fun onItemClick(position: Int, id: String) {
        val categoryName = items[position].name
        val categoryId=items[position].id
        val bundle = Bundle().apply {
            putString("category_name", categoryName)
            putString("category_id", categoryId.toString())
            putString("page_type", "medicine_type")
        }
        findNavController().navigate(R.id.navigation_medicines, bundle)

    }

    // pharmacy click
    override fun onItemClickPharmacy(position: Int, id: String) {
        val pharmacy = pharmacy_items[position]
        val intent = Intent(requireContext(), PharmacyDetailsActivity::class.java)
        intent.putExtra("pharmacy", pharmacy)
        startActivity(intent)
    }

    // medicine click
    override fun onItemClickMedicine(position: Int, id: String) {
        val medicine=medicine_items[position]
        val intent = Intent(requireContext(), MedicineDetailsActivity::class.java)
        intent.putExtra("medicine", medicine)
        startActivity(intent)
    }

    // تنفيذ دالة إضافة المفضلة من Adapter
    override fun onAddToFavorite(pharmacyId: Int) {
        addPharmacyToFavorite(pharmacyId)
    }


    override fun onAddMedicineToFavorite(medicineId: Int) {
        Log.d("DEBUG", "medicineId clicked: $medicineId")
        addMedicineToFavorite(medicineId)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
