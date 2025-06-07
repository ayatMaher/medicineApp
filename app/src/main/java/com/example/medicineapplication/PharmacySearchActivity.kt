package com.example.medicineapplication

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.medicineapplication.adapter.PharmacyAdapter
import com.example.medicineapplication.databinding.ActivityPharmacySearchBinding
import com.example.medicineapplication.model.Pharmacy

class PharmacySearchActivity : AppCompatActivity(), PharmacyAdapter.ItemClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        var binding: ActivityPharmacySearchBinding
        //pharmacy
        lateinit var pharmacyAdapter: PharmacyAdapter
        var pharmacy_item: ArrayList<Pharmacy> = ArrayList()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPharmacySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
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
        pharmacyAdapter = PharmacyAdapter(this, pharmacy_item, this)
        binding.rvPharmacy.adapter = pharmacyAdapter
    }

    override fun onItemClickPharmacy(position: Int, id: String) {
        TODO("Not yet implemented")
    }
}