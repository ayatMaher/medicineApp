@file:Suppress("DEPRECATION")

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
import com.example.medicineapplication.databinding.ActivityLogInBinding
import com.example.medicineapplication.model.LoginResponse
import com.example.medicineapplication.validator.LoginValidator
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
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
class LogInActivity : AppCompatActivity() {
    lateinit var binding: ActivityLogInBinding
    private lateinit var apiService: ApiService

    // test
    private val loginValidator = LoginValidator()

    // call manager facebook
    private lateinit var callbackManager: CallbackManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogInBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        FacebookSdk.sdkInitialize(applicationContext)
        setContentView(binding.root)
//         إعداد Retrofit
        apiService = ApiClient.instance.create(ApiService::class.java)
        // Facebook login setup
        callbackManager = CallbackManager.Factory.create()
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
            unEnableAndUnVisible()
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (!loginValidator.isValidEmail(email) || !loginValidator.isValidPassword(password)) {
                Toast.makeText(
                    this,
                    "الرجاء إدخال بريد إلكتروني وكلمة مرور صحيحة",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
                enableAndVisible()
            }

            loginUser(email, password)
        }
        //log in with google
        binding.btnGoogle.setOnClickListener {
            logInWithGoogle()
        }
        binding.btnFacebook.setOnClickListener {
            logInWithFacebook()
        }


    }

    private fun enableAndVisible() {
        binding.progressBar.visibility = View.GONE
        binding.btnLog.isEnabled = true
        binding.btnLog.alpha = 1f
        binding.btnFacebook.isEnabled = true
        binding.btnFacebook.alpha = 1f
        binding.btnGoogle.isEnabled = true
        binding.btnGoogle.alpha = 1f
        binding.emailEditText.isEnabled = true
        binding.emailEditText.alpha = 1f
        binding.passwordEditText.isEnabled = true
        binding.passwordEditText.alpha = 1f
        binding.txtCreateAccount.isEnabled = true
    }

    private fun unEnableAndUnVisible() {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnLog.isEnabled = false
        binding.btnLog.alpha = 0.5f
        binding.btnFacebook.isEnabled = false
        binding.btnFacebook.alpha = 0.5f
        binding.btnGoogle.isEnabled = false
        binding.btnGoogle.alpha = 0.5f
        binding.emailEditText.isEnabled = false
        binding.emailEditText.alpha = 0.5f
        binding.passwordEditText.isEnabled = false
        binding.passwordEditText.alpha = 0.5f
        binding.txtCreateAccount.isEnabled = false
    }

    private fun logInWithFacebook() {
        LoginManager.getInstance().logInWithReadPermissions(
            this,
            listOf("email", "public_profile")
        )

        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    val accessToken = result.accessToken
                    val request = GraphRequest.newMeRequest(accessToken) { obj, _ ->
                        try {
                            val name = obj?.getString("name")
                            val email = obj?.getString("email")
                            val idToken = obj?.getString("id")
                            val imageUrl = "https://graph.facebook.com/$idToken/picture?type=large"

                            // استخدمي البيانات كما تريدين (تخزين، انتقال لواجهة أخرى..)
                            Log.d("FBLogin", "Name: $name")
                            Log.d("FBLogin", " Email: $email")
                            Log.d("FBLogin", " Id: $idToken")
                            Log.d("FBLogin", " Image: $imageUrl")
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    val parameters = Bundle()
                    parameters.putString("fields", "id,name,email,picture.type(large)")
                    request.parameters = parameters
                    request.executeAsync()
                }

                override fun onCancel() {
                    Toast.makeText(this@LogInActivity, "تم الإلغاء", Toast.LENGTH_SHORT).show()
                }

                override fun onError(error: FacebookException) {
                    Toast.makeText(this@LogInActivity, "خطأ: ${error.message}", Toast.LENGTH_SHORT)
                        .show()
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
                    credentialManager.getCredential(request = request, context = this@LogInActivity)
                val credential = result.credential
                handleSignIn(credential)
            } catch (e: Exception) {
                if (e.message?.contains("cancelled by the user", ignoreCase = true) == true) {
                    Toast.makeText(this@LogInActivity, "تم إلغاء تسجيل الدخول", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(
                        this@LogInActivity,
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
            val name = googleCredential.displayName
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
                            val location = responseData.user.location
                            // حفظ التوكن
                            val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                            sharedPref.edit {
                                putString("ACCESS_TOKEN", "Bearer $token")
                                putInt("USER_ID", userId)
                                apply()
                            }
                            if (location == null) {
                                enableAndVisible()
                                // المستخدم ليس لديه موقع، انتقلي إلى واجهة إضافة الموقع
                                val intent =
                                    Intent(this@LogInActivity, LocationActivity::class.java)
                                intent.putExtra("USER_ID", userId)
                                startActivity(intent)
                                finish()
                            } else {
                                enableAndVisible()
                                // المستخدم لديه موقع، انتقلي للواجهة الرئيسية
                                val intent =
                                    Intent(this@LogInActivity, NavigationDrawerActivity::class.java)
                                startActivity(intent)

                                finish()
                            }
                            Toast.makeText(this@LogInActivity, "مرحبًا $name", Toast.LENGTH_SHORT)
                                .show()
                            enableAndVisible()
                        } else {
                            Toast.makeText(
                                this@LogInActivity,
                                "فشل في تسجيل الدخول عبر Google",
                                Toast.LENGTH_SHORT
                            ).show()
                            enableAndVisible()
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Toast.makeText(this@LogInActivity, "خطأ: ${t.message}", Toast.LENGTH_SHORT)
                            .show()
                        enableAndVisible()
                    }
                })
        } else {
            Toast.makeText(
                this@LogInActivity,
                "Credential is not Google ID token",
                Toast.LENGTH_SHORT
            ).show()
            enableAndVisible()
        }
    }

    private fun loginUser(email: String, password: String) {
        val call = apiService.login(email, password)
        binding.btnLog.isEnabled = false
        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                enableAndVisible()
                if (response.isSuccessful && response.body() != null) {
                    val responseData = response.body()?.data
                    if (responseData != null) {
                        val token = responseData.accessToken
                        val userId = responseData.user.id

                        // save token
                        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                        sharedPref.edit {
                            putString("ACCESS_TOKEN", "Bearer $token")
                            putInt("USER_ID", userId)
                            apply()
                        }
                        Toast.makeText(
                            this@LogInActivity,
                            response.body()!!.message,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        enableAndVisible()
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
                            enableAndVisible()
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
                    enableAndVisible()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                enableAndVisible()
                Toast.makeText(this@LogInActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }
}