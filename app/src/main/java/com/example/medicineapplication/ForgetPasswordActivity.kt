package com.example.medicineapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.medicineapplication.api.ApiClient
import com.example.medicineapplication.api.ApiService
import com.example.medicineapplication.databinding.ActivityForgetPasswordBinding
import com.example.medicineapplication.model.GenericResponse
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
            unEnableAndUnVisible()
            val email = binding.emailEditText.text.toString().trim()

            if (email.isEmpty()) {
                binding.emailEditText.error = "الايميل"
                enableAndVisible()
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
                enableAndVisible()
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
                        enableAndVisible()
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@ForgetPasswordActivity,
                            e.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                enableAndVisible()
                Toast.makeText(
                    this@ForgetPasswordActivity,
                    "Error: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun unEnableAndUnVisible() {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnSend.isEnabled = false
        binding.btnSend.alpha = 0.5f
        binding.emailEditText.isEnabled = false
        binding.emailEditText.alpha = 0.5f
    }

    private fun enableAndVisible() {
        binding.progressBar.visibility = View.GONE
        binding.btnSend.isEnabled = true
        binding.btnSend.alpha = 1f
        binding.emailEditText.isEnabled = true
        binding.emailEditText.alpha = 1f
    }

}


