package com.example.medicineapplication

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medicineapplication.adapter.MedicineTypeAdapter
import com.example.medicineapplication.adapter.PharmacyHomeAdapter
import com.example.medicineapplication.model.MedicineType
import com.example.medicineapplication.model.Pharmacy

public class MainActivity : AppCompatActivity(), MedicineTypeAdapter.ItemClickListener,
    PharmacyHomeAdapter.ItemClickListener {
    //medicine type
    lateinit var medicineTypeAdapter: MedicineTypeAdapter
    lateinit var rvMedicineType: RecyclerView
    var items: ArrayList<MedicineType> = ArrayList<MedicineType>()
    //pharmacy
    lateinit var pharmacyHomeAdapter: PharmacyHomeAdapter
    lateinit var rvPharmacy: RecyclerView
    var pharmacy_item: ArrayList<Pharmacy> = ArrayList<Pharmacy>()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Medicine type
        rvMedicineType = findViewById(R.id.rvMedicinetype);
        items.add(MedicineType("1", R.drawable.image1, "أقراص"))
        items.add(MedicineType("2", R.drawable.image2, "شراب"))
        items.add(MedicineType("3", R.drawable.image3, "حقن"))
        items.add(MedicineType("4", R.drawable.image4, "فيتامين"))
        items.add(MedicineType("5", R.drawable.image5, "قطرات"))
        items.add(MedicineType("5", R.drawable.image6, "براهم"))
        items.add(MedicineType("5", R.drawable.image7, "تحاميل"))
        items.add(MedicineType("5", R.drawable.image8, "بخاخ"))

        medicineTypeAdapter = MedicineTypeAdapter(this, items, this)
        rvMedicineType.setLayoutManager(GridLayoutManager(this, 4)) // 4 columns
        rvMedicineType.setAdapter(medicineTypeAdapter)

        // Pharmacy
        rvPharmacy=findViewById(R.id.rvPharmacy)
        pharmacy_item.add(Pharmacy("1",R.drawable.pharmacy_img,"صيدلية الخنساء الطبية",
            "4.5","شارع بور سعيد "))
        pharmacy_item.add(Pharmacy("2",R.drawable.pharmacy_img,"صيدلية الخنساء الطبية",
            "4.5","شارع بور سعيد "))
        pharmacy_item.add(Pharmacy("3",R.drawable.pharmacy_img,"صيدلية الخنساء الطبية",
            "4.5","شارع بو سعيد "))
        pharmacy_item.add(Pharmacy("4",R.drawable.pharmacy_img,"صيدلية الخنساء الطبية",
            "4.5","شارع بور سعيد "))

        pharmacyHomeAdapter=PharmacyHomeAdapter(this,pharmacy_item,this)
        rvPharmacy.setAdapter(pharmacyHomeAdapter)


    }

    override fun onItemClick(position: Int, id: String?) {
        //when click to type card
        TODO("Not yet implemented")
    }

    override fun onItemClickPharmacy(position: Int, id: String?) {
        //when click to pharmacy card
        TODO("Not yet implemented")
    }
}