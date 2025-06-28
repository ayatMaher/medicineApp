package com.example.medicineapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.medicineapplication.adapter.PharmacyAdapter
import com.example.medicineapplication.databinding.ActivityPharmacyBinding
import com.example.medicineapplication.model.Pharmacy

@Suppress("DEPRECATION")
class PharmacyActivity : AppCompatActivity(), PharmacyAdapter.ItemClickListener {
    lateinit var binding: ActivityPharmacyBinding
    lateinit var pharmacyAdapter: PharmacyAdapter
    var pharmacy_item: ArrayList<Pharmacy> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPharmacyBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        //status bar
        val window = this.window
        window.statusBarColor = ContextCompat.getColor(this, R.color.white2)
//        // اجعل الأيقونات داكنة إذا كان الخلفية فاتحة
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        //back arrow
        binding.backArrow.setOnClickListener {
            finish()
        }
        val medicineName= intent.getStringExtra("medicine_name")
        binding.edtSearch.setText(medicineName)
        showPharmacy()

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
        pharmacyAdapter = PharmacyAdapter(this, pharmacy_item, this)
        binding.rvPharmacy.adapter = pharmacyAdapter
    }

    override fun onItemClickPharmacy(position: Int, id: String) {
        val intent= Intent(this, PharmacyDetailsActivity::class.java)
        startActivity(intent)
    }
}