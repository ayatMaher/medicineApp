package com.example.medicineapplication.fragment

import android.annotation.SuppressLint
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.medicineapplication.MedicineDetailsActivity
import com.example.medicineapplication.PharmacyDetailsActivity
import com.example.medicineapplication.R
import com.example.medicineapplication.adapter.FavoriteMedicineAdapter
import com.example.medicineapplication.adapter.FavoritePharmacyAdapter
import com.example.medicineapplication.api.ApiClient
import com.example.medicineapplication.api.ApiService
import com.example.medicineapplication.databinding.FragmentFavoriteBinding
import com.example.medicineapplication.model.FavoritePharmacyListResponse
import com.example.medicineapplication.model.FavoriteTreatmentItem
import com.example.medicineapplication.model.FavoriteTreatmentResponse
import com.example.medicineapplication.model.GenericResponse
import com.example.medicineapplication.model.Pharmacy
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("DEPRECATION")
class FavoriteFragment : Fragment(), FavoriteMedicineAdapter.ItemClickListener,
    FavoritePharmacyAdapter.ItemClickListener {
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    // medicine
    lateinit var favoriteMedicineAdapter: FavoriteMedicineAdapter
    private var medicineItems: ArrayList<FavoriteTreatmentItem> = ArrayList()

    private var token: String = ""

    //pharmacy
    lateinit var favoritePharmacyAdapter: FavoritePharmacyAdapter
    var pharmacyItems: ArrayList<Pharmacy> = ArrayList()


    @SuppressLint("ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val sharedPref =
            requireActivity().getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        token = sharedPref.getString("ACCESS_TOKEN", "") ?: ""

        //statusBar Color
        val window = requireActivity().window
        window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
        // اجعل الأيقونات داكنة إذا كان الخلفية فاتحة
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars =
            true
        // title text
        binding.header.titleText.text = "المفضلة"

        //
        val pageType = arguments?.getString("page_type")
        if (pageType == "favorite_fragment") {
            binding.header.backButton.visibility = View.VISIBLE
        } else {
            binding.header.backButton.visibility = View.GONE
        }
        // back arrow
        binding.header.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_global_to_profileFragment)
        }
        // default tab(medicine)
        clickOnMedicineButton()
        showMedicine()

        binding.btnMedicines.setOnClickListener {
            clickOnMedicineButton()
            showMedicine()
        }
        binding.btnPharmacies.setOnClickListener {
            clickOnPharmacyButton()
            showPharmacy()
        }
        return root

    }

    @SuppressLint("ResourceType")
    fun clickOnMedicineButton() {
        binding.btnMedicines.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        binding.btnMedicines.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.primary_color
            )
        )
        binding.btnPharmacies.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        binding.btnPharmacies.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.light_green
            )
        )

    }

    private fun clickOnPharmacyButton() {
        binding.btnPharmacies.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        binding.btnPharmacies.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.primary_color
            )
        )
        binding.btnMedicines.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        binding.btnMedicines.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.light_green
            )
        )

    }

    private fun showMedicine() {
        medicineItems.clear()

        if (token.isEmpty()) {
            Toast.makeText(requireContext(), "يرجى تسجيل الدخول", Toast.LENGTH_SHORT).show()
            return
        }
        ApiClient.apiService.getFavoriteMedicines("Bearer $token")
            .enqueue(object : Callback<FavoriteTreatmentResponse> {
                override fun onResponse(
                    call: Call<FavoriteTreatmentResponse>,
                    response: Response<FavoriteTreatmentResponse>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        val favorites = response.body()?.data ?: emptyList()

                        medicineItems.clear()
                        medicineItems.addAll(favorites)

                        favoriteMedicineAdapter = FavoriteMedicineAdapter(
                            requireActivity(),
                            medicineItems,
                            this@FavoriteFragment
                        )
                        binding.rvFavoriteMedicine.layoutManager =
                            GridLayoutManager(requireContext(), 2)
                        binding.rvFavoriteMedicine.adapter = favoriteMedicineAdapter

                        binding.rvFavoriteMedicine.visibility = View.VISIBLE
                        binding.rvFavoritePharmacy.visibility = View.GONE
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "فشل في جلب الأدوية المفضلة",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<FavoriteTreatmentResponse>, t: Throwable) {
                    Toast.makeText(
                        requireContext(),
                        "فشل الاتصال: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun showPharmacy() {
        pharmacyItems.clear()

        if (token.isEmpty()) {
            Toast.makeText(requireContext(), "يرجى تسجيل الدخول", Toast.LENGTH_SHORT).show()
            return
        }

        // استدعاء API
        ApiClient.apiService.getFavoritePharmacies(token)
            .enqueue(object : Callback<FavoritePharmacyListResponse> {
                override fun onResponse(
                    call: Call<FavoritePharmacyListResponse>,
                    response: Response<FavoritePharmacyListResponse>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        val favorites = response.body()?.data ?: emptyList()

                        val pharmacies = favorites.map { wrapper ->
                            wrapper.pharmacy.apply {
                                is_favorite = true
                                favorite_id = wrapper.id  // ← ربط الـ favorite_id مع الصيدلية
                            }
                        }
                        pharmacyItems.clear()
                        pharmacyItems.addAll(pharmacies)
                        // عرض القائمة
                        binding.rvFavoritePharmacy.visibility = View.VISIBLE
                        binding.rvFavoriteMedicine.visibility = View.GONE

                        favoritePharmacyAdapter = FavoritePharmacyAdapter(
                            requireActivity(),
                            pharmacyItems,
                            this@FavoriteFragment
                        )
                        binding.rvFavoritePharmacy.layoutManager =
                            GridLayoutManager(requireContext(), 2)
                        binding.rvFavoritePharmacy.adapter = favoritePharmacyAdapter
                    } else {
                        Toast.makeText(requireContext(), "فشل في جلب المفضلة", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(call: Call<FavoritePharmacyListResponse>, t: Throwable) {
                    Toast.makeText(
                        requireContext(),
                        "خطأ في الاتصال: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    override fun onItemClickMedicine(position: Int, id: String) {
        //when click to medicine card
        val medicine = medicineItems[position]
        val intent = Intent(requireContext(), MedicineDetailsActivity::class.java)
        intent.putExtra("medicine", medicine)
        startActivity(intent)
    }

    override fun onItemClickPharmacy(position: Int, id: String) {
        // go to pharmacy details page
        val pharmacy = pharmacyItems[position]
        val intent = Intent(requireContext(), PharmacyDetailsActivity::class.java)
        intent.putExtra("pharmacy", pharmacy)
        startActivity(intent)
    }

    override fun onDeletePharmacyFavorite(
        pharmacy: Pharmacy,
        position: Int
    ) {

        val token = "Bearer ${
            requireActivity().getSharedPreferences("MyAppPrefs", MODE_PRIVATE).getString(
                "ACCESS_TOKEN",
                ""
            )
        }"

        val call =
            ApiClient.apiService.removePharmacyFavorite(token, pharmacy.favorite_id ?: return)
        call.enqueue(object : Callback<GenericResponse> {
            override fun onResponse(
                call: Call<GenericResponse>,
                response: Response<GenericResponse>
            ) {
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(requireContext(), "تم الحذف بنجاح", Toast.LENGTH_SHORT).show()
                    showPharmacy()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "فشل في الحذف: ${response.body()?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                Toast.makeText(
                    requireContext(),
                    "فشل الاتصال: ${t.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })


    }

    //click to delete favorite
    override fun onDeleteMedicineFavorite(medicineId: Int) {
        deleteFromFavoriteMedicine(medicineId)
    }

    private fun deleteFromFavoriteMedicine(medicineId: Int) {
        val sharedPref =
            requireContext().getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val token = sharedPref.getString("ACCESS_TOKEN", "") ?: ""
        val bearerToken = if (token.startsWith("Bearer ")) token else "Bearer $token"

        val apiService = ApiClient.instance.create(ApiService::class.java)

        apiService.removeMedicineFavorite(bearerToken, medicineId)
            .enqueue(object : Callback<GenericResponse> {
                override fun onResponse(
                    call: Call<GenericResponse>,
                    response: Response<GenericResponse>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(requireContext(), "تم الحذف من المفضلة", Toast.LENGTH_SHORT)
                            .show()
                        // إعادة تحميل المفضلة مثلاً
                        showMedicine()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "فشل في الحذف: ${response.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    Toast.makeText(
                        requireContext(),
                        "خطأ في الاتصال: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }


}