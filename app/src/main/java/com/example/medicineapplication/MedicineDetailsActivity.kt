package com.example.medicineapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.medicineapplication.databinding.ActivityMedicineDetailsBinding
import com.example.medicineapplication.fragment.MedicineFragment

@Suppress("DEPRECATION")
class MedicineDetailsActivity : AppCompatActivity() {
    lateinit var binding:ActivityMedicineDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMedicineDetailsBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        //statusBar Color
        window.statusBarColor = ContextCompat.getColor(this, R.color.light_green)
        // اجعل الأيقونات داكنة إذا كان الخلفية فاتحة
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
        // title text
        binding.header.titleText.text = "تفاصيل الدواء"
        // back arrow
        binding.header.backButton.setOnClickListener {
            finish()
        }
    }
}