package com.example.medicineapplication

import android.os.Bundle
import android.util.Log
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
        binding.header.titleText.text = ""

        // زر الرجوع
        binding.header.backButton.setOnClickListener {
            finish()
        }

        // جلب البيانات من SharedPreferences
        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val token = sharedPref.getString("ACCESS_TOKEN", "") ?: ""
        val userId = sharedPref.getInt("USER_ID", -1)

        // جلب pharmacyId من الـ Intent
        val pharmacyId = intent.getIntExtra("PHARMACY_ID", -1)

        // زر إرسال التقييم
        binding.btnSubmitRating.setOnClickListener {
            val rating = String.format("%.1f", binding.ratingBar.rating) // "3.0" مثلاً
            val comment = binding.txtComment.text.toString()

            if (userId == -1 || token.isBlank() || pharmacyId == -1) {
                Toast.makeText(this, "معلومات المستخدم أو الصيدلية غير صحيحة", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
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
                            Toast.makeText(this@AppEvaluationActivity, "تم إرسال التقييم بنجاح", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Log.e("API_ERROR", "Response code: ${response.code()}")
                            Log.e("API_ERROR", "Response body: ${response.body()}")
                            Log.e("API_ERROR", "Error body: ${response.errorBody()?.string()}")
                            Toast.makeText(this@AppEvaluationActivity, "فشل في إرسال التقييم", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<StoreRatingResponse>, t: Throwable) {
                        Toast.makeText(this@AppEvaluationActivity, "حدث خطأ: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}
