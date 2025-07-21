package com.example.medicineapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.view.WindowInsetsControllerCompat
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.lifecycleScope
import com.example.medicineapplication.api.ApiClient
import com.example.medicineapplication.api.ApiService
import com.example.medicineapplication.databinding.ActivityRegisterBinding
import com.example.medicineapplication.model.LoginResponse
import com.example.medicineapplication.model.RegisterResponse
import com.example.medicineapplication.validator.RegisterValidator
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("DEPRECATION")
class RegisterActivity : AppCompatActivity() {
    private lateinit var apiService: ApiService
    lateinit var binding: ActivityRegisterBinding
    private val validator = RegisterValidator()
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
            unEnableAndUnVisible()
            val name = binding.nameEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            val phone = binding.phoneEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()
            if (!validator.validateAll(name, email, phone, password)) {
                Toast.makeText(this, "تحقق من صحة البيانات المدخلة", Toast.LENGTH_SHORT).show()
                enableAndVisible()
            } else {
                registerUser(name, email, phone, password)
            }
        }
        // log button
        binding.txtLogin.setOnClickListener {
            startActivity(Intent(this, LogInActivity::class.java))
        }
        //google button
        binding.btnGoogle.setOnClickListener {
            logInWithGoogle()
        }
    }

    private fun enableAndVisible() {
        binding.progressBar.visibility = View.GONE
        binding.btnCreateAccount.isEnabled = true
        binding.btnCreateAccount.alpha = 1f
        binding.btnFacebook.isEnabled = true
        binding.btnFacebook.alpha = 1f
        binding.btnGoogle.isEnabled = true
        binding.btnGoogle.alpha = 1f
        binding.txtLogin.isEnabled = true
        binding.emailEditText.isEnabled = true
        binding.emailEditText.alpha = 1f
        binding.passwordEditText.isEnabled = true
        binding.passwordEditText.alpha = 1f
        binding.nameEditText.isEnabled = true
        binding.nameEditText.alpha = 1f
        binding.phoneEditText.isEnabled = true
        binding.phoneEditText.alpha = 1f
    }

    private fun unEnableAndUnVisible() {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnCreateAccount.isEnabled = false
        binding.btnCreateAccount.alpha = 0.5f
        binding.btnFacebook.isEnabled = false
        binding.btnFacebook.alpha = 0.5f
        binding.btnGoogle.isEnabled = false
        binding.btnGoogle.alpha = 0.5f
        binding.txtLogin.isEnabled = false
        binding.emailEditText.isEnabled = false
        binding.emailEditText.alpha = 0.5f
        binding.passwordEditText.isEnabled = false
        binding.passwordEditText.alpha = 0.5f
        binding.nameEditText.isEnabled = false
        binding.nameEditText.alpha = 0.5f
        binding.phoneEditText.isEnabled = false
        binding.phoneEditText.alpha = 0.5f
    }

    private fun registerUser(name: String, email: String, phone: String, password: String) {
        val call = apiService.register(name, email, phone, password)
        call.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                enableAndVisible()
                if (response.isSuccessful && response.body() != null) {
                    val token = response.body()?.data?.accessToken
                    val id = response.body()?.data?.user?.id
                    Toast.makeText(
                        this@RegisterActivity,
                        response.body()!!.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    val pref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                    pref.edit { putString("ACCESS_TOKEN", "Bearer $token") }
                    pref.edit { putInt("USER_ID", id!!) }
                    enableAndVisible()
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
                        Log.e("Exception", errorMessage )
                        enableAndVisible()
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@RegisterActivity,
                            e.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("Exception", e.message.toString() )
                    }
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                enableAndVisible()
                Toast.makeText(this@RegisterActivity, "Error: ${t.message}", Toast.LENGTH_LONG)
                    .show()
                Log.e("Exception", t.message.toString() )
            }
        })
    }

    private fun logInWithGoogle() {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId("158399876041-les6jmpm5gj3tmteicdtjsa25grs17pq.apps.googleusercontent.com")
            // من Firebase Console - Web client ID
            .setFilterByAuthorizedAccounts(false) // ✅ عرض كل حسابات Google بالجهاز
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val credentialManager = CredentialManager.create(this)

        lifecycleScope.launch {
            try {
                val result =
                    credentialManager.getCredential(
                        request = request,
                        context = this@RegisterActivity
                    )
                val credential = result.credential
                handleSignIn(credential)
            } catch (e: Exception) {
                if (e.message?.contains("cancelled by the user", ignoreCase = true) == true) {
                    Toast.makeText(
                        this@RegisterActivity,
                        "تم إلغاء تسجيل الدخول",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else {
                    Toast.makeText(
                        this@RegisterActivity,
                        "فشل تسجيل الدخول: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleSignIn(credential: Credential) {
        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)

            val idToken = googleCredential.idToken
//            val email = googleCredential.id
            val name = googleCredential.displayName
//            val picture = googleCredential.profilePictureUri

            val provider = RequestBody.create("text/plain".toMediaTypeOrNull(), "google")
            val tokenBody = RequestBody.create("text/plain".toMediaTypeOrNull(), idToken)
            apiService.loginWithGoogle(provider, tokenBody)
                .enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {
                        enableAndVisible()
                        if (response.isSuccessful && response.body() != null) {
                            val responseData = response.body()!!.data
                            val token = responseData?.accessToken
                            val userId = responseData?.user!!.id
                            // حفظ التوكن
                            val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                            sharedPref.edit {
                                putString("ACCESS_TOKEN", "Bearer $token")
                                putInt("USER_ID", userId)
                                apply()
                            }
                            Toast.makeText(
                                this@RegisterActivity,
                                "مرحبًا $name",
                                Toast.LENGTH_SHORT
                            ).show()
                            enableAndVisible()
                            val intent = Intent(this@RegisterActivity, LocationActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(
                                this@RegisterActivity,
                                "فشل في تسجيل الدخول عبر Google",
                                Toast.LENGTH_SHORT
                            ).show()
                            enableAndVisible()
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Toast.makeText(
                            this@RegisterActivity,
                            "خطأ: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                        enableAndVisible()
                    }
                })
        } else {
            Toast.makeText(
                this@RegisterActivity,
                "Credential is not Google ID token",
                Toast.LENGTH_SHORT
            ).show()
            enableAndVisible()
        }
    }

}