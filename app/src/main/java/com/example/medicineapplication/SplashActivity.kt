package com.example.medicineapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.medicineapplication.databinding.ActivitySplashBinding



@SuppressLint("CustomSplashScreen")
@Suppress("DEPRECATION")
class SplashActivity : AppCompatActivity() {
    lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        // statusBar Color
        window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar_color_log)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        // تأخير 2 ثانية
        binding.root.postDelayed({
            val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val isFirstLaunch = prefs.getBoolean("isFirstLaunch", true)

            if (isFirstLaunch) {
                // فقط نفتح شاشة OnBoarding، بدون تغيير isFirstLaunch
                startActivity(Intent(this, OnBoardingActivity::class.java))
                finish()
                return@postDelayed
            }

            val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
            val token = sharedPref.getString("ACCESS_TOKEN", null)

            if (token != null) {
                startActivity(Intent(this, NavigationDrawerActivity::class.java))
            } else {
                startActivity(Intent(this, LogInActivity::class.java))
            }

            finish()
        }, 2000)

    }
}
