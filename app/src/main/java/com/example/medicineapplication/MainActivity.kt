package com.example.medicineapplication

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medicineapplication.adapter.MedicineTypeAdapter
import com.example.medicineapplication.model.MedicineType

public class MainActivity : AppCompatActivity(),MedicineTypeAdapter.ItemClickListener {
    lateinit var medicineTypeAdapter: MedicineTypeAdapter
    lateinit var rvMedicineType: RecyclerView
    var items: ArrayList<MedicineType> = ArrayList<MedicineType>()


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
        rvMedicineType= findViewById(R.id.rvMedicinetype);
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



    }

    override fun onItemClick(position: Int, id: String?) {
        //when click to type card
        TODO("Not yet implemented")
    }
}