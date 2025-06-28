package com.example.medicineapplication.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.medicineapplication.model.Pharmacy

@Suppress("DEPRECATION")
class HomeFragment : Fragment(), CategoryAdapter.ItemClickListener,
    PharmacyAdapter.ItemClickListener, MedicineAdapter.ItemClickListener {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    //medicine type
    private lateinit var categoryAdapter: CategoryAdapter
    private var items: ArrayList<MedicineType> = ArrayList()

    //pharmacy
    private lateinit var pharmacyHomeAdapter: PharmacyAdapter
    private var pharmacy_items: ArrayList<Pharmacy> = ArrayList()

    //medicine
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
        //statusBar Color
        val window = requireActivity().window
        window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.light_green)
        // اجعل الأيقونات داكنة إذا كان الخلفية فاتحة
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        // search icon
        binding.searchIcon.setOnClickListener {
            val bundle = Bundle().apply {
                putString("page_type", "search")
            }
            findNavController().navigate(R.id.navigation_pharmacies, bundle)
        }
        //notification icon
        binding.notificationIcon.setOnClickListener {
            val intent = Intent(requireContext(), NotificationsActivity::class.java)
            startActivity(intent)

        }
        // join as pharmacy button
        binding.btnJoin.setOnClickListener {
            val url = "https://demoapplication.jawebhom.com/where-my-treatment-app"  // ضع هنا رابط الموقع المطلوب
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
        // upload Prescription
        binding.btnUpload.setOnClickListener {
            val intent = Intent(requireContext(), AddingPrescriptionActivity::class.java)
            startActivity(intent)
        }
        //Medicine type
        showMedicineType()

        // Pharmacy
        showPharmacy()

        //Medicine
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
        medicine_items.add(
            Medicine(
                "1",
                "باندول",
                R.drawable.medicine_img,
                description = "مسكن للألم و مخفض للحرارة",
                isFeatured = false
            )
        )
        medicine_items.add(
            Medicine(
                "1",
                "باندول",
                R.drawable.medicine_img,
                description = "مسكن للألم و مخفض للحرارة",
                isFeatured = false
            )
        )
        medicine_items.add(
            Medicine(
                "1",
                "باندول",
                R.drawable.medicine_img,
                description = "مسكن للألم و مخفض للحرارة",
                isFeatured = false
            )
        )
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
        items.add(MedicineType("5", R.drawable.image6, "براهم", false))
        items.add(MedicineType("5", R.drawable.image7, "تحاميل", false))
        items.add(MedicineType("5", R.drawable.image8, "بخاخ", false))

        categoryAdapter = CategoryAdapter(requireActivity(), items, this)
        binding.rvMedicinetype.layoutManager = GridLayoutManager(requireContext(), 4)
        binding.rvMedicinetype.adapter = categoryAdapter
    }

    private fun showPharmacy() {
        pharmacy_items.clear()
        pharmacy_items.add(
            Pharmacy(
                "1", R.drawable.pharmacy_img, "صيدلية الخنساء الطبية",
                4.5, "شارع بور سعيد ", isFeatured = false
            )
        )
        pharmacy_items.add(
            Pharmacy(
                "2", R.drawable.pharmacy_img, "صيدلية الخنساء الطبية",
                4.5, "شارع بور سعيد ", isFeatured = false
            )
        )
        pharmacy_items.add(
            Pharmacy(
                "3", R.drawable.pharmacy_img, "صيدلية الخنساء الطبية",
                4.5, "شارع بو سعيد ", isFeatured = false
            )
        )
        pharmacy_items.add(
            Pharmacy(
                "4", R.drawable.pharmacy_img, "صيدلية الخنساء الطبية",
                4.5, "شارع بور سعيد ", isFeatured = false
            )
        )

        pharmacyHomeAdapter = PharmacyAdapter(requireActivity(), pharmacy_items, this)
        binding.rvPharmacy.adapter = pharmacyHomeAdapter

    }


    //category
    override fun onItemClick(position: Int, id: String) {
        //when click to category card
        val categoryName = items[position].nameType
        Log.e("category", "$categoryName ")
        val bundle = Bundle().apply {
            putString("category_name", categoryName)
            putString("page_type", "medicine_type")
        }
        findNavController().navigate(R.id.navigation_medicines, bundle)

    }

    //pharmacy
    override fun onItemClickPharmacy(position: Int, id: String) {
        //when click to pharmacy card
        val intent = Intent(requireContext(), PharmacyDetailsActivity::class.java)
        startActivity(intent)
    }

    //medicine
    override fun onItemClickMedicine(position: Int, id: String) {
        //when click to medicine card
        val intent = Intent(requireContext(), MedicineDetailsActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}