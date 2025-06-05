package com.example.medicineapplication

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.medicineapplication.adapter.CategoryAdapter
import com.example.medicineapplication.adapter.MedicineAdapter
import com.example.medicineapplication.databinding.ActivityCategoryBinding
import com.example.medicineapplication.model.Medicine
import com.example.medicineapplication.model.MedicineType

class CategoryActivity : AppCompatActivity(), CategoryAdapter.ItemClickListener,
    MedicineAdapter.ItemClickListener {
    private lateinit var binding: ActivityCategoryBinding

    // category
    private lateinit var categoryAdapter: CategoryAdapter
    val data = ArrayList<MedicineType>()

    // medicine
    lateinit var medicineAdapter: MedicineAdapter
    val medicineData = ArrayList<Medicine>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //category
        data.add(MedicineType("1", nameType = "أقراص", isFeatured = true))
        data.add(MedicineType("2", nameType = "شراب", isFeatured = true))
        data.add(MedicineType("3", nameType = "حقن", isFeatured = true))
        data.add(MedicineType("4", nameType = "فيتامين", isFeatured = true))
        data.add(MedicineType("5", nameType = "قطرات", isFeatured = true))
        data.add(MedicineType("6", nameType = "براهم", isFeatured = true))
        data.add(MedicineType("7", nameType = "تحاميل", isFeatured = true))
        data.add(MedicineType("8", nameType = "بخاخ", isFeatured = true))
        categoryAdapter = CategoryAdapter(this, data, this)
        binding.rvCategory.adapter = categoryAdapter

        //medicine
        medicineData.add(
            Medicine(
                "1",
                "بنادول",
                R.drawable.medicine_img,
                80.0,
                "مسكن للالم و خافض للحرارة",
                120.0,
                true
            )
        )
        medicineData.add(
            Medicine(
                "2",
                "بنادول",
                R.drawable.medicine_img,
                80.0,
                "مسكن للالم و خافض للحرارة",
                120.0,
                true
            )
        )
        medicineData.add(
            Medicine(
                "3",
                "بنادول",
                R.drawable.medicine_img,
                80.0,
                "مسكن للالم و خافض للحرارة",
                120.0,
                true
            )
        )
        medicineData.add(
            Medicine(
                "4",
                "بنادول",
                R.drawable.medicine_img,
                80.0,
                "مسكن للالم و خافض للحرارة",
                120.0,
                true
            )
        )
        medicineData.add(
            Medicine(
                "5",
                "بنادول",
                R.drawable.medicine_img,
                80.0,
                "مسكن للالم و خافض للحرارة",
                120.0,
                true
            )
        )
        medicineData.add(
            Medicine(
                "6",
                "بنادول",
                R.drawable.medicine_img,
                80.0,
                "مسكن للالم و خافض للحرارة",
                120.0,
                true
            )
        )
        medicineData.add(
            Medicine(
                "7",
                "بنادول",
                R.drawable.medicine_img,
                80.0,
                "مسكن للالم و خافض للحرارة",
                120.0,
                true
            )
        )
        medicineData.add(
            Medicine(
                "8",
                "بنادول",
                R.drawable.medicine_img,
                80.0,
                "مسكن للالم و خافض للحرارة",
                120.0,
                true
            )
        )
        medicineData.add(
            Medicine(
                "9",
                "بنادول",
                R.drawable.medicine_img,
                80.0,
                "مسكن للالم و خافض للحرارة",
                120.0,
                true
            )
        )
        medicineData.add(
            Medicine(
                "1",
                "بنادول",
                R.drawable.medicine_img,
                80.0,
                "مسكن للالم و خافض للحرارة",
                120.0,
                true
            )
        )

        medicineAdapter = MedicineAdapter(this, medicineData, this)
        binding.rvCategoryMedicine.layoutManager = GridLayoutManager(this, 2) //2 column
        binding.rvCategoryMedicine.adapter = medicineAdapter
    }

    override fun onItemClick(position: Int, id: String) {

    }

    override fun onItemClickMedicine(position: Int, id: String) {
        TODO("Not yet implemented")
    }
}