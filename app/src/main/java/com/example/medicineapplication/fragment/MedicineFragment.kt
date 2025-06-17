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
import androidx.recyclerview.widget.GridLayoutManager
import com.example.medicineapplication.MedicineDetailsActivity
import com.example.medicineapplication.R
import com.example.medicineapplication.adapter.CategoryAdapter
import com.example.medicineapplication.adapter.MedicineAdapter
import com.example.medicineapplication.databinding.FragmentMedicineBinding
import com.example.medicineapplication.model.Medicine
import com.example.medicineapplication.model.MedicineType

@Suppress("DEPRECATION")
class MedicineFragment : Fragment(), CategoryAdapter.ItemClickListener,
    MedicineAdapter.ItemClickListener {

    private var _binding: FragmentMedicineBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // category
    private lateinit var categoryAdapter: CategoryAdapter
    val data = ArrayList<MedicineType>()

    // medicine
    private lateinit var medicineAdapter: MedicineAdapter
    private val medicineData = ArrayList<Medicine>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMedicineBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //status bar
        val window = requireActivity().window
        window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white2)
        // اجعل الأيقونات داكنة إذا كان الخلفية فاتحة
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        // back arrow
        binding.backArrow.setOnClickListener {
            findNavController().navigate(R.id.action_global_to_firstFragment)
        }

        //category
        showCategory()
        //medicine
        showMedicine()
        return root
    }

    private fun showCategory() {
        data.add(MedicineType("1", nameType = "أقراص", isFeatured = true))
        data.add(MedicineType("2", nameType = "شراب", isFeatured = true))
        data.add(MedicineType("3", nameType = "حقن", isFeatured = true))
        data.add(MedicineType("4", nameType = "فيتامين", isFeatured = true))
        data.add(MedicineType("5", nameType = "قطرات", isFeatured = true))
        data.add(MedicineType("6", nameType = "براهم", isFeatured = true))
        data.add(MedicineType("7", nameType = "تحاميل", isFeatured = true))
        data.add(MedicineType("8", nameType = "بخاخ", isFeatured = true))
        val selectedCategoryName = arguments?.getString("category_name")
        categoryAdapter = CategoryAdapter(requireActivity(), data, this,selectedCategoryName)
        binding.rvCategory.adapter = categoryAdapter
    }

    private fun showMedicine() {
        //medicine
        medicineData.add(
            Medicine(
                "1",
                "بنادول",
                R.drawable.medicine_img,
                description = "مسكن للالم و خافض للحرارة",
                isFeatured = true
            )
        )
        medicineData.add(
            Medicine(
                "2",
                "بنادول",
                R.drawable.medicine_img,
                description = "مسكن للالم و خافض للحرارة",
                isFeatured = true
            )
        )
        medicineData.add(
            Medicine(
                "3",
                "بنادول",
                R.drawable.medicine_img,
                description = "مسكن للالم و خافض للحرارة",
                isFeatured = true
            )
        )
        medicineData.add(
            Medicine(
                "4",
                "بنادول",
                R.drawable.medicine_img,
                description = "مسكن للالم و خافض للحرارة",
                isFeatured = true
            )
        )
        medicineData.add(
            Medicine(
                "5",
                "بنادول",
                R.drawable.medicine_img,
                description = "مسكن للالم و خافض للحرارة",
                isFeatured = true
            )
        )
        medicineData.add(
            Medicine(
                "6",
                "بنادول",
                R.drawable.medicine_img,
                description = "مسكن للالم و خافض للحرارة",
                isFeatured = true
            )
        )
        medicineData.add(
            Medicine(
                "7",
                "بنادول",
                R.drawable.medicine_img,
                description = "مسكن للالم و خافض للحرارة",
                isFeatured = true
            )
        )
        medicineData.add(
            Medicine(
                "8",
                "بنادول",
                R.drawable.medicine_img,
                description = "مسكن للالم و خافض للحرارة",
                isFeatured = true
            )
        )
        medicineData.add(
            Medicine(
                "9",
                "بنادول",
                R.drawable.medicine_img,
                description = "مسكن للالم و خافض للحرارة",
                isFeatured = true
            )
        )
        medicineData.add(
            Medicine(
                "1",
                "بنادول",
                R.drawable.medicine_img,
                description = "مسكن للالم و خافض للحرارة",
                isFeatured = true
            )
        )

        medicineAdapter = MedicineAdapter(requireActivity(), medicineData, this)
        binding.rvCategoryMedicine.layoutManager = GridLayoutManager(requireContext(), 2) //2 column
        binding.rvCategoryMedicine.adapter = medicineAdapter

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(position: Int, id: String) {
        Log.e("TAG", "onItemClick: ")
    }

    override fun onItemClickMedicine(position: Int, id: String) {
        val intent= Intent(requireContext(), MedicineDetailsActivity::class.java)
        startActivity(intent)
    }


}