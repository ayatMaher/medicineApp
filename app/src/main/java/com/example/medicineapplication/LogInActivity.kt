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
import com.example.medicineapplication.databinding.ActivityLogInBinding
import com.example.medicineapplication.model.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.core.content.edit
import org.json.JSONObject

@Suppress("DEPRECATION")
class LogInActivity : AppCompatActivity() {
    lateinit var binding: ActivityLogInBinding
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogInBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
//         إعداد Retrofit
        apiService = ApiClient.instance.create(ApiService::class.java)
        //statusBar Color
        window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar_color_log)
        // اجعل الأيقونات داكنة إذا كان الخلفية فاتحة
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
        //click to forget password
        binding.txtForgetPassword.setOnClickListener {
            val intent = Intent(this, ForgetPasswordActivity::class.java)
            startActivity(intent)
        }
        // click to create account
        binding.txtCreateAccount.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        //click to log button
        binding.btnLog.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            binding.btnLog.isEnabled = false
            binding.btnLog.alpha = 0.5f
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "لو سمحت ادخل الايميل او كلمة المرور", Toast.LENGTH_SHORT)
                    .show()
                binding.progressBar.visibility = View.GONE
                binding.btnLog.isEnabled = true
                binding.btnLog.alpha = 1f
            } else {
                loginUser(email, password)
            }
        }


    }

    private fun loginUser(email: String, password: String) {
        val call = apiService.login(email, password)
        binding.btnLog.isEnabled = false
        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                binding.progressBar.visibility = View.GONE
                binding.btnLog.isEnabled = true
                binding.btnLog.alpha = 1f
                if (response.isSuccessful && response.body() != null) {
                    val responseData = response.body()?.data
                    if (responseData != null) {
                        val token = responseData.accessToken
                        // save token
                        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                        sharedPref.edit { putString("ACCESS_TOKEN", token) }

                        Toast.makeText(
                            this@LogInActivity,
                            response.body()!!.message,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        binding.btnLog.isEnabled = true
                        // الانتقال للواجهة التالية
                        val intent =
                            Intent(this@LogInActivity, NavigationDrawerActivity::class.java)
                        startActivity(intent)
                        finish()

                    } else {
                        val errorBody = response.errorBody()?.string()
                        try {
                            val jsonObject = JSONObject(errorBody!!)
                            val errorMessage = jsonObject.getJSONObject("data").getString("error")
                            Toast.makeText(this@LogInActivity, errorMessage, Toast.LENGTH_SHORT)
                                .show()
                            binding.progressBar.visibility = View.GONE
                            binding.btnLog.isEnabled = true
                            binding.btnLog.alpha = 1f
                        } catch (e: Exception) {
                            Toast.makeText(
                                this@LogInActivity,
                                e.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val jsonObject = JSONObject(errorBody!!)
                    val errorMessage = jsonObject.getJSONObject("data").getString("error")
                    Toast.makeText(this@LogInActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility = View.GONE
                    binding.btnLog.isEnabled = true
                    binding.btnLog.alpha = 1f
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                binding.btnLog.isEnabled = true
                binding.btnLog.alpha = 1f
                Toast.makeText(this@LogInActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}