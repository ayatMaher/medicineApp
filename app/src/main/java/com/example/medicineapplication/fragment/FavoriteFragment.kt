package com.example.medicineapplication.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.medicineapplication.MedicineDetailsActivity
import com.example.medicineapplication.PharmacyDetailsActivity
import com.example.medicineapplication.R
import com.example.medicineapplication.adapter.FavoriteMedicineAdapter
import com.example.medicineapplication.adapter.FavoritePharmacyAdapter
import com.example.medicineapplication.api.ApiClient
import com.example.medicineapplication.databinding.FragmentFavoriteBinding
import com.example.medicineapplication.model.FavoritePharmacyListResponse
import com.example.medicineapplication.model.FavoriteTreatmentItem
import com.example.medicineapplication.model.FavoriteTreatmentResponse
import com.example.medicineapplication.model.Medicine
import com.example.medicineapplication.model.Pharmacy
import retrofit2.Response
import retrofit2.Call
import retrofit2.Callback

@Suppress("DEPRECATION")
class FavoriteFragment : Fragment(), FavoriteMedicineAdapter.ItemClickListener,
    FavoritePharmacyAdapter.ItemClickListener {
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    // medicine
    lateinit var favoriteMedicineAdapter: FavoriteMedicineAdapter
    private var medicine_items: ArrayList<FavoriteTreatmentItem> = ArrayList()

    private var token: String=""

    //pharmacy
    lateinit var favoritePharmacyAdapter: FavoritePharmacyAdapter
    var pharmacy_items: ArrayList<Pharmacy> = ArrayList()

    @SuppressLint("ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        val root: View = binding.root


        val sharedPref =
            requireActivity().getSharedPreferences("MyAppPrefs", AppCompatActivity.MODE_PRIVATE)
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

        if (pageType == "favorite") {
            clickOnMedicineButton()
            showMedicine()
        } else {
            showPharmacy()
        }

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
        medicine_items.clear()

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

                        medicine_items.clear()
                        medicine_items.addAll(favorites)

                        favoriteMedicineAdapter = FavoriteMedicineAdapter(requireActivity(), medicine_items, this@FavoriteFragment)
                        binding.rvFavoriteMedicine.layoutManager = GridLayoutManager(requireContext(), 2)
                        binding.rvFavoriteMedicine.adapter = favoriteMedicineAdapter

                        binding.rvFavoriteMedicine.visibility = View.VISIBLE
                        binding.rvFavoritePharmacy.visibility = View.GONE
                    } else {
                        Toast.makeText(requireContext(), "فشل في جلب الأدوية المفضلة", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<FavoriteTreatmentResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "فشل الاتصال: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }




    private fun showPharmacy() {
        pharmacy_items.clear()

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
                        val pharmacies = response.body()?.data?.map {
                            it.pharmacy.copy(is_favorite = true)
                        } ?: emptyList()

                        pharmacy_items.addAll(pharmacies)

                        // عرض القائمة
                        binding.rvFavoritePharmacy.visibility = View.VISIBLE
                        binding.rvFavoriteMedicine.visibility = View.GONE

                        favoritePharmacyAdapter = FavoritePharmacyAdapter(requireActivity(), pharmacy_items, this@FavoriteFragment)
                        binding.rvFavoritePharmacy.layoutManager = GridLayoutManager(requireContext(), 2)
                        binding.rvFavoritePharmacy.adapter = favoritePharmacyAdapter
                    } else {
                        Toast.makeText(requireContext(), "فشل في جلب المفضلة", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<FavoritePharmacyListResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "خطأ في الاتصال: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }


    override fun onItemClickMedicine(position: Int, id: String) {
        //when click to medicine card
        val medicine=medicine_items[position]
        val intent = Intent(requireContext(), MedicineDetailsActivity::class.java)
        intent.putExtra("medicine", medicine)
        startActivity(intent)
    }

    override fun onItemClickPharmacy(position: Int, id: String) {
        // go to pharmacy details page
        val pharmacy = pharmacy_items[position]
        val intent = Intent(requireContext(), PharmacyDetailsActivity::class.java)
        intent.putExtra("pharmacy", pharmacy)
        startActivity(intent)
    }

}