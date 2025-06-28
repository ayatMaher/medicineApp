package com.example.medicineapplication.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.medicineapplication.MedicineDetailsActivity
import com.example.medicineapplication.PharmacyDetailsActivity
import com.example.medicineapplication.R
import com.example.medicineapplication.adapter.PharmacyAdapter
import com.example.medicineapplication.databinding.FragmentPharmacyBinding
import com.example.medicineapplication.model.Pharmacy

@Suppress("DEPRECATION")
class PharmacyFragment : Fragment(), PharmacyAdapter.ItemClickListener {

    private var _binding: FragmentPharmacyBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    //pharmacy
    lateinit var pharmacyAdapter: PharmacyAdapter
    var pharmacy_item: ArrayList<Pharmacy> = ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPharmacyBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //status bar
        val window = requireActivity().window
        window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white2)
//        // اجعل الأيقونات داكنة إذا كان الخلفية فاتحة
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        val pageType = arguments?.getString("page_type")
        val medicineName = arguments?.getString("medicine Name")
        binding.edtSearch.setText(medicineName)
        if (pageType == "search" || pageType == "favorite" || pageType == "pharmacy") {
            binding.backArrow.visibility = View.VISIBLE
        } else {
            binding.backArrow.visibility = View.GONE
        }
        // back arrow
        binding.backArrow.setOnClickListener {
            if (pageType == "search") {
                findNavController().navigate(R.id.action_global_to_firstFragment)
            } else if (pageType == "favorite") {
                val bundle = Bundle().apply {
                    putString("page_type", "favorite")
                }
                findNavController().navigate(R.id.action_global_to_favoriteFragment, bundle)

            }else if (pageType == "pharmacy"){
                val intent = Intent(requireContext(),MedicineDetailsActivity::class.java)
                requireContext().startActivity(intent)
            }
        }
        showPharmacy()

        return root
    }

    private fun showPharmacy() {
        pharmacy_item.add(
            Pharmacy(
                "id",
                R.drawable.pharmacy2,
                "صيدلية الأسرى",
                4.5,
                "النفق_شارع بور سعيد",
                450.5,
                "8:00 ص _ 1:00 م",
                true
            )
        )
        pharmacy_item.add(
            Pharmacy(
                "id",
                R.drawable.pharmacy2,
                "صيدلية الأسرى",
                4.5,
                "النفق_شارع بور سعيد",
                450.5,
                "8:00 ص _ 1:00 م",
                true
            )
        )
        pharmacy_item.add(
            Pharmacy(
                "id",
                R.drawable.pharmacy2,
                "صيدلية الأسرى",
                4.5,
                "النفق_شارع بور سعيد",
                450.5,
                "8:00 ص _ 1:00 م",
                true
            )
        )
        pharmacy_item.add(
            Pharmacy(
                "id",
                R.drawable.pharmacy2,
                "صيدلية الأسرى",
                4.5,
                "النفق_شارع بور سعيد",
                450.5,
                "8:00 ص _ 1:00 م",
                true
            )
        )
        pharmacy_item.add(
            Pharmacy(
                "id",
                R.drawable.pharmacy2,
                "صيدلية الأسرى",
                4.5,
                "النفق_شارع بور سعيد",
                450.5,
                "8:00 ص _ 1:00 م",
                true
            )
        )
        pharmacy_item.add(
            Pharmacy(
                "id",
                R.drawable.pharmacy2,
                "صيدلية الأسرى",
                4.5,
                "النفق_شارع بور سعيد",
                450.5,
                "8:00 ص _ 1:00 م",
                true
            )
        )
        pharmacyAdapter = PharmacyAdapter(requireActivity(), pharmacy_item, this)
        binding.rvPharmacy.adapter = pharmacyAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClickPharmacy(position: Int, id: String) {
        val intent= Intent(requireContext(), PharmacyDetailsActivity::class.java)
        startActivity(intent)
    }
}