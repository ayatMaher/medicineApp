package com.example.medicineapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.medicineapplication.api.ApiClient
import com.example.medicineapplication.api.ApiService
import com.example.medicineapplication.databinding.ActivityResetPasswordBinding
import com.example.medicineapplication.model.GenericResponse
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("DEPRECATION")
class ResetPasswordActivity : AppCompatActivity() {
    lateinit var binding: ActivityResetPasswordBinding
    private lateinit var apiService: ApiService
    private lateinit var email: String
    private lateinit var token: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apiService = ApiClient.instance.create(ApiService::class.java)

        email = intent.getStringExtra("email") ?: ""
        token = intent.getStringExtra("token") ?: ""

        //statusBar Color
        window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar_color_log)
        // اجعل الأيقونات داكنة إذا كان الخلفية فاتحة
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        // click to back arrow
        binding.btnBack.setOnClickListener { finish() }

        //save button
        binding.btnSave.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            binding.btnSave.isEnabled = false
            binding.btnSave.alpha = 0.5f

            val newPassword = binding.passwordEditText.text.toString().trim()
            val confirmPassword = binding.confirmPasswordEditText.text.toString().trim()
            Log.d("ResetPassword", "Password: '$newPassword'")
            Log.d("ResetPassword", "Confirm: '$confirmPassword'")
            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "الرجاء إدخال كلمة المرور", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
                binding.btnSave.isEnabled = true
                binding.btnSave.alpha = 1f
            } else if (newPassword != confirmPassword) {
                Toast.makeText(this, "كلمات المرور غير متطابقة", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
                binding.btnSave.isEnabled = true
                binding.btnSave.alpha = 1f
            } else {
                resetPassword(email, token, newPassword, confirmPassword)
            }

        }


    }

    private fun resetPassword(
        email: String,
        token: String,
        password: String,
        confirmation: String
    ) {
        apiService.resetPassword(
            method = "email",
            email = email,
            token = token,
            password = password,
            passwordConfirmation = confirmation
        )
            .enqueue(object : Callback<GenericResponse> {
                override fun onResponse(
                    call: Call<GenericResponse>,
                    response: Response<GenericResponse>
                ) {
                    binding.progressBar.visibility = View.GONE
                    binding.btnSave.isEnabled = true
                    binding.btnSave.alpha = 1f
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(
                            this@ResetPasswordActivity,
                            response.body()!!.message,
                            Toast.LENGTH_LONG
                        ).show()

                        // الانتقال لتسجيل الدخول
                        val intent = Intent(this@ResetPasswordActivity, LogInActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    } else {
                        val errorBody = response.errorBody()?.string()
                        val msg = try {
                            JSONObject(errorBody!!).getJSONObject("data").getString("error")
                        } catch (e: Exception) {
                            "فشل في إعادة تعيين كلمة المرور"
                        }
                        Toast.makeText(this@ResetPasswordActivity, msg, Toast.LENGTH_LONG).show()
                        binding.progressBar.visibility = View.GONE
                        binding.btnSave.isEnabled = true
                        binding.btnSave.alpha = 1f
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    binding.progressBar.visibility = View.GONE
                    binding.btnSave.isEnabled = true
                    binding.btnSave.alpha = 1f
                    Toast.makeText(
                        this@ResetPasswordActivity,
                        "فشل الاتصال: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}