package com.example.medicineapplication

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.medicineapplication.databinding.ActivityHeaderBinding

class HeaderActivity : AppCompatActivity() {
    lateinit var binding: ActivityHeaderBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding= ActivityHeaderBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}