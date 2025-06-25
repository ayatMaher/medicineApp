package com.example.medicineapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.medicineapplication.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val token = sharedPref.getString("ACCESS_TOKEN", null)

        if (token != null) {
            // المستخدم مسجل دخول → ننتقل مباشرة إلى Home
            startActivity(Intent(this, NavigationDrawerActivity::class.java))
            finish()
        } else {
            // المستخدم غير مسجل → نوجهه إلى LoginActivity
            startActivity(Intent(this, LogInActivity::class.java))
            finish()
        }
    }
}