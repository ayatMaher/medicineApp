package com.example.medicineapplication.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
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
import com.example.medicineapplication.model.Medicine
import com.example.medicineapplication.model.MedicineType
import androidx.core.net.toUri
import com.example.medicineapplication.api.ApiClient
import com.example.medicineapplication.model.FavoritePharmacyRequest
import com.example.medicineapplication.model.FavoritePharmacyResponse
import com.example.medicineapplication.model.Pharmacy
import com.example.medicineapplication.model.PharmacyResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.log

@Suppress("DEPRECATION")
class HomeFragment : Fragment(), CategoryAdapter.ItemClickListener,
    PharmacyAdapter.ItemClickListener, MedicineAdapter.ItemClickListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var categoryAdapter: CategoryAdapter
    private var items: ArrayList<MedicineType> = ArrayList()

    private lateinit var pharmacyHomeAdapter: PharmacyAdapter
    private var pharmacy_items: ArrayList<Pharmacy> = ArrayList()

    private lateinit var medicineAdapter: MedicineAdapter
    private var medicine_items: ArrayList<Medicine> = ArrayList()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

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
        medicine_items.add(
            Medicine(
                "1",
                "باندول",
                R.drawable.medicine_img,
                description = "مسكن للألم و مخفض للحرارة",
                isFeatured = false
            )
        )
        // أضف المزيد من العناصر حسب الحاجة
        medicineAdapter = MedicineAdapter(requireActivity(), medicine_items, this)
        binding.rvMedicine.adapter = medicineAdapter
    }

    private fun showMedicineType() {
        items.clear()
        items.add(MedicineType("1", R.drawable.image1, "أقراص", false))
        items.add(MedicineType("2", R.drawable.image2, "شراب", false))
        items.add(MedicineType("3", R.drawable.image3, "حقن", false))
        items.add(MedicineType("4", R.drawable.image4, "فيتامين", false))
        items.add(MedicineType("5", R.drawable.image5, "قطرات", false))
        // المزيد...
        categoryAdapter = CategoryAdapter(requireActivity(), items, this)
        binding.rvMedicinetype.layoutManager = GridLayoutManager(requireContext(), 4)
        binding.rvMedicinetype.adapter = categoryAdapter
    }

    private fun showPharmacy() {
        pharmacy_items.clear()
        val sharedPref =
            requireActivity().getSharedPreferences("MyAppPrefs", AppCompatActivity.MODE_PRIVATE)
        val token = sharedPref.getString("ACCESS_TOKEN", "") ?: ""

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
        val sharedPref = requireActivity().getSharedPreferences("MyAppPrefs", AppCompatActivity.MODE_PRIVATE)
        val token = sharedPref.getString("ACCESS_TOKEN", "") ?: ""
        val userId = sharedPref.getInt("USER_ID", -1)

        if (token.isEmpty() || userId == -1) {
            Toast.makeText(requireContext(), "يرجى تسجيل الدخول أولاً", Toast.LENGTH_SHORT).show()
            return
        }

        val request = FavoritePharmacyRequest(userId, pharmacyId)

        ApiClient.apiService.storeFavorite("Bearer $token", request)
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
                            "فشل في الإضافة للمفضلة"
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



    // category click
    override fun onItemClick(position: Int, id: String) {
        val categoryName = items[position].nameType
        val bundle = Bundle().apply {
            putString("category_name", categoryName)
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
        val intent = Intent(requireContext(), MedicineDetailsActivity::class.java)
        startActivity(intent)
    }

    // تنفيذ دالة إضافة المفضلة من Adapter
    override fun onAddToFavorite(pharmacyId: Int) {
        addPharmacyToFavorite(pharmacyId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
