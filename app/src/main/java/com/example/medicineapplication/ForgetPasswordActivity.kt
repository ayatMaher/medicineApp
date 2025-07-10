package com.example.medicineapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response
import android.widget.Toast
import androidx.core.view.WindowInsetsControllerCompat
import com.example.medicineapplication.api.ApiClient
import com.example.medicineapplication.api.ApiService
import com.example.medicineapplication.databinding.ActivityForgetPasswordBinding
import com.example.medicineapplication.model.GenericResponse
import org.json.JSONObject

@Suppress("DEPRECATION")
class ForgetPasswordActivity : AppCompatActivity() {
    lateinit var binding: ActivityForgetPasswordBinding
    private lateinit var apiService: ApiService

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityForgetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        apiService = ApiClient.instance.create(ApiService::class.java)

        //statusBar Color
        window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar_color_log)
        // اجعل الأيقونات داكنة إذا كان الخلفية فاتحة
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        // click to back arrow
        binding.btnBack.setOnClickListener { finish() }

        // send button
        binding.btnSend.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            binding.btnSend.isEnabled = false
            binding.btnSend.alpha = 0.5f

            val email = binding.emailEditText.text.toString().trim()

            if (email.isEmpty()) {
                binding.emailEditText.error = "الايميل"
                binding.progressBar.visibility = View.GONE
                binding.btnSend.isEnabled = true
                binding.btnSend.alpha = 1f
            } else {
                sendForgotPasswordRequest(email)
            }
        }

    }


    private fun sendForgotPasswordRequest(email: String) {
        val call = apiService.forgotPassword(email = email)

        call.enqueue(object : Callback<GenericResponse> {
            override fun onResponse(
                call: Call<GenericResponse>,
                response: Response<GenericResponse>
            ) {
                binding.progressBar.visibility = View.GONE
                binding.btnSend.isEnabled = true
                binding.btnSend.alpha = 1f
                if (response.isSuccessful && response.body() != null) {
                    Toast.makeText(
                        this@ForgetPasswordActivity,
                        response.body()!!.message,
                        Toast.LENGTH_LONG
                    ).show()

                    // الانتقال إلى شاشة إدخال الكود
                    val intent =
                        Intent(this@ForgetPasswordActivity, VerifyTokenActivity::class.java)
                    intent.putExtra("email", email)
                    startActivity(intent)
                } else {
                    val errorBody = response.errorBody()?.string()
                    try {
                        val jsonObject = JSONObject(errorBody!!)
                        val errorMessage = jsonObject.getJSONObject("data").getString("error")
                        Toast.makeText(this@ForgetPasswordActivity, errorMessage, Toast.LENGTH_LONG)
                            .show()
                        Log.e("errorMessage", errorMessage)
                        binding.progressBar.visibility = View.GONE
                        binding.btnSend.isEnabled = true
                        binding.btnSend.alpha = 1f
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@ForgetPasswordActivity,
                            e.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("Exception", e.message.toString())
                    }
                }
            }

            override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                binding.btnSend.isEnabled = true
                binding.btnSend.alpha = 1f
                Toast.makeText(
                    this@ForgetPasswordActivity,
                    "Error: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
                Log.e("Exception", "Error: ${t.message}")
            }
        })
    }
}
