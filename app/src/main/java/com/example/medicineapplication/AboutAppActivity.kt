package com.example.medicineapplication

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.medicineapplication.databinding.ActivityAboutAppBinding
import com.example.medicineapplication.databinding.ActivityMedicineDetailsBinding
import com.example.medicineapplication.fragment.ProfileFragment

class AboutAppActivity : AppCompatActivity() {
    lateinit var binding: ActivityAboutAppBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutAppBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        //statusBar Color
        window.statusBarColor = ContextCompat.getColor(this, R.color.light_green)
        // اجعل الأيقونات داكنة إذا كان الخلفية فاتحة
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
        // title text
        binding.header.titleText.text = "عن التطبيق "
        // back arrow
        binding.header.backButton.setOnClickListener {
                finish()
        }

    }
}