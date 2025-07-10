package com.example.medicineapplication

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.medicineapplication.databinding.ActivitySettingPageBinding


class SettingActivity : AppCompatActivity() {
    lateinit var binding: ActivitySettingPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivitySettingPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.header.titleText.text = "الإعدادات"

        binding.header.backButton.setOnClickListener {
            finish()
        }

    }
}