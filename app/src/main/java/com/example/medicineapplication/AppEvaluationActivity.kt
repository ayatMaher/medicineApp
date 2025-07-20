package com.example.medicineapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.medicineapplication.api.ApiClient
import com.example.medicineapplication.databinding.ActivityAppEvaluationBinding
import com.example.medicineapplication.model.StoreRatingRequest
import com.example.medicineapplication.model.StoreRatingResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("DEPRECATION")
class AppEvaluationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAppEvaluationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppEvaluationBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        // تغيير لون الستاتس بار
        window.statusBarColor = ContextCompat.getColor(this, android.R.color.transparent)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        // عنوان الهيدر
        binding.header.titleText.text = "تقييم الصيدلية"

        // زر الرجوع
        binding.header.backButton.setOnClickListener {
            finish()
        }
        // زر إرسال التقييم
        binding.btnSubmitRating.setOnClickListener {
            ratePharmacy()
            unEnableAndVisible()
        }
    }

    @SuppressLint("DefaultLocale")
    private fun ratePharmacy() {
        // جلب البيانات من SharedPreferences
        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val token = sharedPref.getString("ACCESS_TOKEN", "") ?: ""
        val userId = sharedPref.getInt("USER_ID", -1)
        // جلب pharmacyId من الـ Intent
        val pharmacyId = intent.getIntExtra("PHARMACY_ID", -1)

        val rating = String.format("%.1f", binding.ratingBar.rating) // "3.0" مثلاً
        val comment = binding.txtComment.text.toString()

        if (userId == -1 || token.isBlank() || pharmacyId == -1) {
            Toast.makeText(this, "معلومات المستخدم أو الصيدلية غير صحيحة", Toast.LENGTH_SHORT)
                .show()
            return
        }

        val request = StoreRatingRequest(
            user_id = userId,
            pharmacy_id = pharmacyId,
            rating = rating,
            comment = comment
        )

        ApiClient.apiService.ratingPharmacy(token, request)
            .enqueue(object : Callback<StoreRatingResponse> {
                override fun onResponse(
                    call: Call<StoreRatingResponse>,
                    response: Response<StoreRatingResponse>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(
                            this@AppEvaluationActivity,
                            "تم إرسال التقييم بنجاح",
                            Toast.LENGTH_SHORT
                        ).show()
                        enableAndVisible()
                        finish()
                    } else {
                        Toast.makeText(
                            this@AppEvaluationActivity,
                            "فشل في إرسال التقييم",
                            Toast.LENGTH_SHORT
                        ).show()
                        enableAndVisible()
                    }
                }

                override fun onFailure(call: Call<StoreRatingResponse>, t: Throwable) {
                    Toast.makeText(
                        this@AppEvaluationActivity,
                        "حدث خطأ: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    enableAndVisible()
                }
            })
    }

    private fun enableAndVisible() {
        binding.progressBar.visibility = View.GONE
        binding.btnSubmitRating.isEnabled = true
        binding.btnSubmitRating.alpha = 1f
        binding.txtComment.isEnabled = true
        binding.txtComment.alpha = 1f
        binding.ratingBar.isEnabled = true
        binding.ratingBar.alpha = 1f
    }

    private fun unEnableAndVisible() {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnSubmitRating.isEnabled = false
        binding.btnSubmitRating.alpha = 0.5f
        binding.txtComment.isEnabled = false
        binding.txtComment.alpha = 0.5f
        binding.ratingBar.isEnabled = false
        binding.ratingBar.alpha = 0.5f
    }


}
