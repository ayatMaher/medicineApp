package com.example.medicineapplication

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.medicineapplication.databinding.ActivityEditProfileBinding
import com.example.medicineapplication.databinding.ActivitySettingPageBinding
import com.example.medicineapplication.fragment.ProfileFragment

class SettingPage : AppCompatActivity() {
    lateinit var binding: ActivitySettingPageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivitySettingPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.header.titleText.text = "الإعدادات"

        binding.header.backButton.setOnClickListener {
            val intent = Intent(this, ProfileFragment::class.java)
            startActivity(intent)
            finish()
        }

    }
}