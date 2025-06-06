package com.example.medicineapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.medicineapplication.adapter.CategoryAdapter
import com.example.medicineapplication.adapter.MedicineAdapter
import com.example.medicineapplication.adapter.PharmacyAdapter
import com.example.medicineapplication.databinding.ActivityMainBinding
import com.example.medicineapplication.model.Medicine
import com.example.medicineapplication.model.MedicineType
import com.example.medicineapplication.model.Pharmacy

class MainActivity : AppCompatActivity(), CategoryAdapter.ItemClickListener,
    PharmacyAdapter.ItemClickListener, MedicineAdapter.ItemClickListener {

    private lateinit var binding: ActivityMainBinding

    //medicine type
    private lateinit var categoryAdapter: CategoryAdapter
    private var items: ArrayList<MedicineType> = ArrayList()

    //pharmacy
    private lateinit var pharmacyHomeAdapter: PharmacyAdapter
    private var pharmacy_item: ArrayList<Pharmacy> = ArrayList()

    //medicine
    private lateinit var medicineAdapter: MedicineAdapter
    private var medicine_item: ArrayList<Medicine> = ArrayList()


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //statusBar Color
        window.statusBarColor = ContextCompat.getColor(this, R.color.light_green)
        // اجعل الأيقونات داكنة إذا كان الخلفية فاتحة
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars =
            true
        // search icon
        binding.searchIcon.setOnClickListener {
            val intent = Intent(this,PharmacySearchActivity::class.java)
            startActivity(intent)
        }
        //Medicine type
        items.add(MedicineType("1", R.drawable.image1, "أقراص", false))
        items.add(MedicineType("2", R.drawable.image2, "شراب", false))
        items.add(MedicineType("3", R.drawable.image3, "حقن", false))
        items.add(MedicineType("4", R.drawable.image4, "فيتامين", false))
        items.add(MedicineType("5", R.drawable.image5, "قطرات", false))
        items.add(MedicineType("5", R.drawable.image6, "براهم", false))
        items.add(MedicineType("5", R.drawable.image7, "تحاميل", false))
        items.add(MedicineType("5", R.drawable.image8, "بخاخ", false))

        categoryAdapter = CategoryAdapter(this, items, this)
        binding.rvMedicinetype.layoutManager = GridLayoutManager(this, 4)
        binding.rvMedicinetype.adapter = categoryAdapter

        // Pharmacy
        pharmacy_item.add(
            Pharmacy(
                "1", R.drawable.pharmacy_img, "صيدلية الخنساء الطبية",
                4.5, "شارع بور سعيد ", isFeatured = false
            )
        )
        pharmacy_item.add(
            Pharmacy(
                "2", R.drawable.pharmacy_img, "صيدلية الخنساء الطبية",
                4.5, "شارع بور سعيد ", isFeatured = false
            )
        )
        pharmacy_item.add(
            Pharmacy(
                "3", R.drawable.pharmacy_img, "صيدلية الخنساء الطبية",
                4.5, "شارع بو سعيد ", isFeatured = false
            )
        )
        pharmacy_item.add(
            Pharmacy(
                "4", R.drawable.pharmacy_img, "صيدلية الخنساء الطبية",
                4.5, "شارع بور سعيد ", isFeatured = false
            )
        )

        pharmacyHomeAdapter = PharmacyAdapter(this, pharmacy_item, this)
        binding.rvPharmacy.adapter=pharmacyHomeAdapter

        //Medicine
        medicine_item.add(
            Medicine(
                "1",
                "باندول",
                R.drawable.medicine_img,
                120.0,
                "مسكن للألم و مخفض للحرارة", isFeatured = false
            )
        )
        medicine_item.add(
            Medicine(
                "1",
                "باندول",
                R.drawable.medicine_img,
                120.0,
                "مسكن للألم و مخفض للحرارة",
                isFeatured = false
            )
        )
        medicine_item.add(
            Medicine(
                "1",
                "باندول",
                R.drawable.medicine_img,
                120.0,
                "مسكن للألم و مخفض للحرارة",
                isFeatured = false
            )
        )
        medicine_item.add(
            Medicine(
                "1",
                "باندول",
                R.drawable.medicine_img,
                120.0,
                "مسكن للألم و مخفض للحرارة",
                isFeatured = false
            )
        )
        medicineAdapter = MedicineAdapter(this, medicine_item, this)
        binding.rvMedicine.adapter = medicineAdapter

    }

    //category
    override fun onItemClick(position: Int, id: String) {
        //when click to category card
        val intent = Intent(this, CategoryActivity::class.java)
        startActivity(intent)

    }

    //pharmacy
    override fun onItemClickPharmacy(position: Int, id: String) {
        //when click to pharmacy card
        TODO("Not yet implemented")
    }


    //medicine
    override fun onItemClickMedicine(position: Int, id: String) {
        //when click to medicine card
        TODO("Not yet implemented")
    }

}