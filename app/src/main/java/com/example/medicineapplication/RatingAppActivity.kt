package com.example.medicineapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.medicineapplication.api.ApiClient
import com.example.medicineapplication.databinding.ActivityRatingAppBinding
import com.example.medicineapplication.model.StoreAppRatingResponse
import com.example.medicineapplication.model.StoreRatingAppRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("DEPRECATION")
class RatingAppActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRatingAppBinding

    @SuppressLint("DefaultLocale")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRatingAppBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // تغيير لون الستاتس بار
        window.statusBarColor = ContextCompat.getColor(this, android.R.color.transparent)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        // عنوان الهيدر
        binding.header.titleText.text = "تقييم التطبيق"
        // زر الرجوع
        binding.header.backButton.setOnClickListener {
            finish()
        }

        binding.btnSubmitRating.setOnClickListener {
            rateApp()
        }

    }

    @SuppressLint("DefaultLocale")
    fun rateApp() {
        // جلب البيانات من SharedPreferences
        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val token = sharedPref.getString("ACCESS_TOKEN", "") ?: ""
        val userId = sharedPref.getInt("USER_ID", -1)
        val rating = String.format("%.1f", binding.ratingBar.rating) // "3.0" مثلاً
        val comment = binding.txtComment.text.toString()

        if (userId == -1 || token.isBlank()) {
            Toast.makeText(this, "معلومات المستخدم  صحيحة", Toast.LENGTH_SHORT).show()
            return
        }

        val request = StoreRatingAppRequest(
            user_id = userId,
            rating = rating,
            comment = comment
        )
        ApiClient.apiService.ratingApp(token, request)
            .enqueue(object : Callback<StoreAppRatingResponse> {
                override fun onResponse(
                    call: Call<StoreAppRatingResponse>,
                    response: Response<StoreAppRatingResponse>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(
                            this@RatingAppActivity,
                            "تم إرسال التقييم بنجاح",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this@RatingAppActivity,
                            "فشل في إرسال التقييم",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<StoreAppRatingResponse>, t: Throwable) {
                    Toast.makeText(
                        this@RatingAppActivity,
                        "حدث خطأ: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })


    }
}


