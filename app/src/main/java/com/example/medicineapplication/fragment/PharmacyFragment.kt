package com.example.medicineapplication.fragment

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.medicineapplication.MedicineDetailsActivity
import com.example.medicineapplication.PharmacyDetailsActivity
import com.example.medicineapplication.R
import com.example.medicineapplication.adapter.PharmacyAdapter
import com.example.medicineapplication.api.ApiClient
import com.example.medicineapplication.databinding.FragmentPharmacyBinding
import com.example.medicineapplication.model.Pharmacy
import com.example.medicineapplication.model.PharmacyResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("DEPRECATION")
class PharmacyFragment : Fragment(), PharmacyAdapter.ItemClickListener {

    private var _binding: FragmentPharmacyBinding? = null
    private val binding get() = _binding!!

    private lateinit var pharmacyAdapter: PharmacyAdapter

    private val pharmacyList = MutableLiveData<List<Pharmacy>>()
    private val error = MutableLiveData<String>()
    private var token: String=""
    private var userId: Int=-1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPharmacyBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Status bar customization
        val window = requireActivity().window
        window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white2)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        val pageType = arguments?.getString("page_type")
        binding.backArrow.visibility =
            if (pageType == "search" || pageType == "favorite" || pageType == "pharmacy") View.VISIBLE else View.GONE


        val sharedPref = requireActivity().getSharedPreferences("MyAppPrefs", AppCompatActivity.MODE_PRIVATE)
        token = sharedPref.getString("ACCESS_TOKEN", "") ?: ""
        userId = sharedPref.getInt("USER_ID", -1)


        binding.backArrow.setOnClickListener {
            when (pageType) {
                "search" -> findNavController().navigate(R.id.action_global_to_firstFragment)
                "favorite" -> {
                    val bundle = Bundle().apply { putString("page_type", "favorite") }
                    findNavController().navigate(R.id.action_global_to_favoriteFragment, bundle)
                }
                "pharmacy" -> {
                    val intent = Intent(requireContext(), MedicineDetailsActivity::class.java)
                    startActivity(intent)
                }
            }
        }

        pharmacyAdapter = PharmacyAdapter(requireActivity(), emptyList(), this)
        binding.rvPharmacy.adapter = pharmacyAdapter

        observeData()

        setupSearchListeners()

        return root
    }

    private fun setupSearchListeners() {
        // الضغط على Enter من الكيبورد
        binding.edtSearch.setOnEditorActionListener { _: TextView, actionId: Int, event: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                performSearch()
                true
            } else {
                false
            }
        }

        // الضغط على أيقونة البحث
        binding.searchImage.setOnClickListener {
            performSearch()
        }
    }

    private fun performSearch() {
        val medicineName = binding.edtSearch.text.toString().trim()
        if (medicineName.isEmpty()) {
            Toast.makeText(requireContext(), "يرجى إدخال اسم الدواء", Toast.LENGTH_SHORT).show()
            return
        }



        if (token.isNotEmpty()) {
            fetchPharmaciesNearby(token, medicineName)
        } else {
            Toast.makeText(requireContext(), "يرجى تسجيل الدخول أولاً", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchPharmaciesNearby(token: String, treatment: String) {
        ApiClient.apiService.searchTreatmentPharmacyNearby("Bearer $token", treatment)
            .enqueue(object : Callback<PharmacyResponse> {
                override fun onResponse(
                    call: Call<PharmacyResponse>,
                    response: Response<PharmacyResponse>
                ) {
                    if (response.isSuccessful) {
                        val pharmacies = response.body()?.data?.map {
                            it.copy(isFeatured = true) // ⭐️ هذا هو المفتاح لتفعيل ViewHolderPharmacySearch
                        } ?: emptyList()
                        pharmacyList.value = pharmacies
                        if (pharmacies.isNotEmpty()) {
                            Toast.makeText(
                                requireContext(),
                                "توجد صيدليات قريبة لهذا العلاج",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "لا توجد صيدليات قريبة لهذا العلاج",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "فشل في جلب البيانات من السيرفر",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<PharmacyResponse>, t: Throwable) {
                    Toast.makeText(
                        requireContext(),
                        "حدث خطأ أثناء الاتصال: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun observeData() {
        pharmacyList.observe(viewLifecycleOwner, Observer { pharmacies ->
            pharmacyAdapter.updateData(pharmacies)
        })

        error.observe(viewLifecycleOwner, Observer { msg ->
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        })
    }

    override fun onItemClickPharmacy(position: Int, id: String) {
        val currentList = pharmacyList.value ?: return
        val pharmacy = currentList[position]
        val intent = Intent(requireContext(), PharmacyDetailsActivity::class.java)
        intent.putExtra("pharmacy", pharmacy)
        startActivity(intent)
    }

    override fun onAddToFavorite(pharmacyId: Int) {
        TODO("Not yet implemented")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
