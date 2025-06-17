package com.example.medicineapplication.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
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
        // back arrow
        binding.backArrow.setOnClickListener {
            findNavController().navigate(R.id.action_global_to_firstFragment)
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
        findNavController().navigate(R.id.navigation_home)
    }
}