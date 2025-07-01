package com.example.medicineapplication

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
import retrofit2.Callback
import com.example.medicineapplication.databinding.ActivityRegisterBinding
import com.example.medicineapplication.model.RegisterResponse
import retrofit2.Call
import retrofit2.Response
import androidx.core.content.edit
import org.json.JSONObject

@Suppress("DEPRECATION")
class RegisterActivity : AppCompatActivity() {
    private lateinit var apiService: ApiService
    lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apiService = ApiClient.instance.create(ApiService::class.java)

        //statusBar Color
        window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar_color_log)
        // اجعل الأيقونات داكنة إذا كان الخلفية فاتحة
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        // create acccount button
        binding.btnCreateAccount.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            binding.btnCreateAccount.isEnabled = false
            binding.btnCreateAccount.alpha = 0.5f
            val name = binding.nameEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            val phone = binding.phoneEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()
            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "لو سمحت ادخل جميع البيانات", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
                binding.btnCreateAccount.isEnabled = true
                binding.btnCreateAccount.alpha = 1f
            } else {
                registerUser(name, email, phone, password)
            }
        }
        // log button
        binding.txtLogin.setOnClickListener {
            startActivity(Intent(this, LogInActivity::class.java))
        }
    }

    private fun registerUser(name: String, email: String, phone: String, password: String) {
        val call = apiService.register(name, email, phone, password)
        call.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                binding.progressBar.visibility = View.GONE
                binding.btnCreateAccount.isEnabled = true
                binding.btnCreateAccount.alpha = 1f
                if (response.isSuccessful && response.body() != null) {
                    val token = response.body()?.data?.accessToken
                    Toast.makeText(
                        this@RegisterActivity,
                        response.body()!!.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    val pref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                    pref.edit { putString("ACCESS_TOKEN", token) }
                    binding.btnCreateAccount.isEnabled = true
                    startActivity(
                        Intent(
                            this@RegisterActivity,
                            LocationActivity::class.java
                        )
                    )
                    finish()
                } else {
                    val errorBody = response.errorBody()?.string()
                    try {
                        val jsonObject = JSONObject(errorBody!!)
                        val errorMessage = jsonObject.getJSONObject("data").getString("error")
                        Toast.makeText(this@RegisterActivity, errorMessage, Toast.LENGTH_LONG)
                            .show()
                        binding.progressBar.visibility = View.GONE
                        binding.btnCreateAccount.isEnabled = true
                        binding.btnCreateAccount.alpha = 1f
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@RegisterActivity,
                            e.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                binding.btnCreateAccount.isEnabled = true
                binding.btnCreateAccount.alpha = 1f
                Toast.makeText(this@RegisterActivity, "Error: ${t.message}", Toast.LENGTH_LONG)
                    .show()
            }
        })
    }
}