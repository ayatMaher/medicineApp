package com.example.medicineapplication

import android.content.Intent
import android.os.Bundle
import android.view.animation.TranslateAnimation
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.medicineapplication.databinding.ActivitySplashBinding

@Suppress("DEPRECATION")
class SplashActivity : AppCompatActivity() {
    lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        //statusBar Color
        window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar_color_log)
        // اجعل الأيقونات داكنة إذا كان الخلفية فاتحة
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        // حركة نزول الصورة
        val imageAnim = TranslateAnimation(0f, 0f, -500f, 0f).apply {
            duration = 1000
            fillAfter = true
        }

        // حركة صعود النص
        val textAnim = TranslateAnimation(0f, 0f, 500f, 0f).apply {
            duration = 1000
            fillAfter = true
        }

        // تأخير قبل الانتقال (2 ثانية)
        binding.root.postDelayed({
            val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
            val token = sharedPref.getString("ACCESS_TOKEN", null)

            if (token != null) {
                startActivity(Intent(this, NavigationDrawerActivity::class.java))
            } else {
                startActivity(Intent(this, LogInActivity::class.java))
            }
            finish()
        }, 2000) // انتظر 2 ثانية ثم نفذ الشرط

    }
}