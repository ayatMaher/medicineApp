package com.example.medicineapplication

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.medicineapplication.api.ApiClient
import com.example.medicineapplication.api.ApiService
import com.example.medicineapplication.databinding.ActivityVerifyTokenBinding
import com.example.medicineapplication.model.GenericResponse
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("DEPRECATION")
class VerifyTokenActivity : AppCompatActivity() {
    lateinit var binding: ActivityVerifyTokenBinding
    private lateinit var apiService: ApiService
    private lateinit var email: String
    private lateinit var countDownTimer: CountDownTimer
    private var isTokenExpired = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityVerifyTokenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apiService = ApiClient.instance.create(ApiService::class.java)

        //statusBar Color
        window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar_color_log)
        // اجعل الأيقونات داكنة إذا كان الخلفية فاتحة
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        // click to back arrow
        binding.btnBack.setOnClickListener { finish() }


        // الحصول على البريد الذي تم تمريره من الشاشة السابقة
        email = intent.getStringExtra("email") ?: ""
        binding.txtEmail.text = email

        setupCodeInputs()
        startCountdown()
        //click to verify button
        binding.btnVerify.setOnClickListener {

            binding.progressBar.visibility = View.VISIBLE
            binding.btnVerify.isEnabled = false
            binding.btnVerify.alpha = 0.5f

            if (isTokenExpired) {
                showExpiredMessage()
                return@setOnClickListener
            }

            val code = getEnteredCode()
            if (code.length == 4) {
                verifyCode(email, code)
            } else {
                Toast.makeText(this, "يرجى إدخال الكود كاملًا", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
                binding.btnVerify.isEnabled = true
                binding.btnVerify.alpha = 1f
            }

        }

        // resend verify code
        binding.txtResend.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            binding.btnVerify.isEnabled = false
            binding.btnVerify.alpha = 0.5f
            resendVerificationCode()
        }

    }

    private fun getEnteredCode(): String {
        return binding.edtCode4.text.toString() +
                binding.edtCode3.text.toString() +
                binding.edtCode2.text.toString() +
                binding.edtCode1.text.toString()
    }

    private fun startCountdown() {
        countDownTimer = object : CountDownTimer(1 * 60 * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = millisUntilFinished / 60000
                val seconds = (millisUntilFinished % 60000) / 1000
                binding.txtCountDownTime.text =
                    "%02d:%02d".format(minutes, seconds)
            }

            override fun onFinish() {
                isTokenExpired = true
                binding.txtCountDownTime.text = "⛔ انتهت صلاحية الكود"
                binding.btnVerify.isEnabled = false
                binding.btnVerify.alpha = 0.5f

                showExpiredMessage()
            }
        }.start()
    }

    private fun showExpiredMessage() {
        binding.progressBar.visibility = View.GONE
        binding.btnVerify.isEnabled = true
        binding.btnVerify.alpha = 1f
        Toast.makeText(this, "انتهت صلاحية الكود. حاول مرة أخرى لاحقًا.", Toast.LENGTH_LONG).show()
    }

    private fun setupCodeInputs() {
        val inputs = listOf(binding.edtCode1, binding.edtCode2, binding.edtCode3, binding.edtCode4)

        for (i in inputs.indices) {
            inputs[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1) {
                        if (i < inputs.size - 1) {
                            inputs[i + 1].requestFocus()
                        } else {
                            inputs[i].clearFocus()
                            // يمكنك هنا تنفيذ التحقق أو الإرسال التلقائي
                        }
                    } else if (s?.isEmpty() == true && i > 0) {
                        inputs[i - 1].requestFocus()
                    }
                }
            })
        }
    }

    private fun verifyCode(email: String, token: String) {
        apiService.verifyToken(email = email, token = token)
            .enqueue(object : Callback<GenericResponse> {
                override fun onResponse(
                    call: Call<GenericResponse>,
                    response: Response<GenericResponse>
                ) {
                    binding.progressBar.visibility = View.GONE
                    binding.btnVerify.isEnabled = true
                    binding.btnVerify.alpha = 1f
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@VerifyTokenActivity,
                            response.body()!!.message,
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent =
                            Intent(this@VerifyTokenActivity, ResetPasswordActivity::class.java)
                        intent.putExtra("email", email)
                        intent.putExtra("token", token)
                        startActivity(intent)

                    } else {
                        try {
                            val errorBody = response.errorBody()?.string()
                            val errorMsg =
                                JSONObject(errorBody!!).getJSONObject("data").getString("error")
                            Toast.makeText(this@VerifyTokenActivity, errorMsg, Toast.LENGTH_LONG)
                                .show()
                            binding.progressBar.visibility = View.GONE
                            binding.btnVerify.isEnabled = true
                            binding.btnVerify.alpha = 1f
                            if (errorMsg.contains("انتهت صلاحية الكود")) {
                                isTokenExpired = true
                                binding.btnVerify.isEnabled = false
                                binding.btnVerify.alpha = 0.5f
                            }

                        } catch (e: Exception) {
                            Toast.makeText(
                                this@VerifyTokenActivity,
                                e.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    binding.progressBar.visibility = View.GONE
                    binding.btnVerify.isEnabled = true
                    binding.btnVerify.alpha = 1f
                    Toast.makeText(
                        this@VerifyTokenActivity,
                        "خطأ في الاتصال: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun resendVerificationCode() {
        apiService.forgotPassword(email = email).enqueue(object : Callback<GenericResponse> {
            override fun onResponse(
                call: Call<GenericResponse>,
                response: Response<GenericResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    Toast.makeText(
                        this@VerifyTokenActivity,
                        response.body()!!.message,
                        Toast.LENGTH_LONG
                    ).show()

                    // إعادة تفعيل زر التحقق
                    binding.progressBar.visibility = View.GONE
                    isTokenExpired = false
                    binding.btnVerify.isEnabled = true
                    binding.btnVerify.alpha = 1f

                    // إعادة تشغيل العداد
                    countDownTimer.cancel()
                    startCountdown()

                } else {
                    Toast.makeText(
                        this@VerifyTokenActivity,
                        "فشل في إعادة إرسال الكود",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(
                    this@VerifyTokenActivity,
                    "فشل الاتصال: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer.cancel()
    }
}


