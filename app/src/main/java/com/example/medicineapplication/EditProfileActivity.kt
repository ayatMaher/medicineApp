package com.example.medicineapplication

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.example.medicineapplication.databinding.ActivityEditProfileBinding
import java.io.File
import android.Manifest
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.medicineapplication.api.ApiClient
import com.example.medicineapplication.api.ApiService
import com.example.medicineapplication.model.UserResponse
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.core.content.edit
import com.bumptech.glide.Glide
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody


class EditProfileActivity : AppCompatActivity() {
    lateinit var binding: ActivityEditProfileBinding
    private lateinit var imageView: ImageView
    private lateinit var selectButton: ImageView
    private var selectedImageUri: Uri? = null
    private lateinit var cameraImageUri: Uri

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                selectedImageUri = it   // <-- أضف هذا السطر
                imageView.setImageURI(it)
            }
        }

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                selectedImageUri = cameraImageUri  // <-- أضف هذا السطر
                imageView.setImageURI(cameraImageUri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }
        imageView = binding.profileImage
        selectButton = binding.addIcon

        selectButton.setOnClickListener {
            showImagePickerDialog()
        }
        binding.btnSave.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            binding.btnSave.isEnabled = false
            binding.btnSave.alpha = 0.5f
            binding.editName.isEnabled = false
            binding.editName.alpha = 0.5f
            binding.editEmail.isEnabled = false
            binding.editEmail.alpha = 0.5f
            binding.editPhone.isEnabled = false
            binding.editPhone.alpha = 0.5f
            binding.editPassword.isEnabled = false
            binding.editPassword.alpha = 0.5f
            binding.addIcon.isEnabled = false
            binding.addIcon.alpha = 0.5f
            updateProfile()
        }
        loadUserData()
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("من المعرض", "من الكاميرا")
        AlertDialog.Builder(this)
            .setTitle("اختر مصدر الصورة")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> pickImageLauncher.launch("image/*")
                    1 -> openCamera()
                }
            }
            .show()
    }

    private fun openCamera() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ),
            1
        )

        val imageFile =
            File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "profile_image.jpg")
        cameraImageUri = FileProvider.getUriForFile(this, "$packageName.provider", imageFile)
        takePictureLauncher.launch(cameraImageUri)
    }

    private fun updateProfile() {
        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val token = sharedPref.getString("ACCESS_TOKEN", "") ?: ""
        val bearerToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
        val apiService = ApiClient.instance.create(ApiService::class.java)

        val name = binding.editName.text.toString().trim()
        val email = binding.editEmail.text.toString().trim()
        val phone = binding.editPhone.text.toString().trim()
        val password = binding.editPassword.text.toString().trim()


        val nameBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
        val emailBody = email.toRequestBody("text/plain".toMediaTypeOrNull())
        val phoneBody = phone.toRequestBody("text/plain".toMediaTypeOrNull())
        val passwordBody = password.toRequestBody("text/plain".toMediaTypeOrNull())

        var imagePart: MultipartBody.Part? = null
        selectedImageUri?.let { uri ->
            val file = getFileFromUri(uri)
            file?.let {
                val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
                imagePart = MultipartBody.Part.createFormData("image", it.name, requestFile)
            }
        }

        apiService.updateUser(bearerToken, nameBody, emailBody, phoneBody, passwordBody, imagePart)
            .enqueue(object : Callback<UserResponse> {
                override fun onResponse(
                    call: Call<UserResponse>,
                    response: Response<UserResponse>
                ) {
                    binding.progressBar.visibility = View.GONE
                    binding.btnSave.isEnabled = true
                    binding.btnSave.alpha = 1f
                    binding.editName.isEnabled = true
                    binding.editName.alpha = 1f
                    binding.editEmail.isEnabled = true
                    binding.editEmail.alpha = 1f
                    binding.editPhone.isEnabled = true
                    binding.editPhone.alpha = 1f
                    binding.editPassword.isEnabled = true
                    binding.editPassword.alpha = 1f
                    binding.addIcon.isEnabled = true
                    binding.addIcon.alpha = 1f
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(
                            this@EditProfileActivity,
                            response.body()!!.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        loadUserData()
                    } else {
                        val errorBody = response.errorBody()?.string()

                        if (errorBody != null && errorBody.trim().startsWith("{")) {
                            try {
                                val json = JSONObject(errorBody)
                                val errorMessage = json.optString("message", "حدث خطأ")

                                Toast.makeText(
                                    this@EditProfileActivity,
                                    errorBody,
                                    Toast.LENGTH_SHORT
                                ).show()

                                Log.e("errorBody", errorBody)
                                if (errorMessage.contains("غير مصرح")) {
                                    // 🧹 حذف التوكن
                                    val sharedPref =
                                        getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                                    sharedPref.edit { clear() }

                                    // 🔁 إعادة التوجيه إلى تسجيل الدخول
                                    val intent =
                                        Intent(this@EditProfileActivity, LogInActivity::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                    finish()
                                }

                            } catch (e: Exception) {
                                Toast.makeText(
                                    this@EditProfileActivity,
                                    e.message,
                                    Toast.LENGTH_SHORT
                                ).show()

                            }
                        } else {
                            Toast.makeText(
                                this@EditProfileActivity,
                                "فشل في الاتصال بالخادم",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    Toast.makeText(
                        this@EditProfileActivity,
                        "فشل الاتصال: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("onFailure", "فشل الاتصال: ${t.message}")
                    binding.progressBar.visibility = View.GONE
                    binding.btnSave.isEnabled = true
                    binding.btnSave.alpha = 1f
                    binding.editName.isEnabled = true
                    binding.editName.alpha = 1f
                    binding.editEmail.isEnabled = true
                    binding.editEmail.alpha = 1f
                    binding.editPhone.isEnabled = true
                    binding.editPhone.alpha = 1f
                    binding.editPassword.isEnabled = true
                    binding.editPassword.alpha = 1f
                    binding.addIcon.isEnabled = true
                    binding.addIcon.alpha = 1f
                }
            })
    }

    private fun getFileFromUri(uri: Uri): File? {
        val inputStream = contentResolver.openInputStream(uri) ?: return null
        val tempFile = File.createTempFile("profile", ".jpg", cacheDir)
        tempFile.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }
        return tempFile
    }

    private fun loadUserData() {
        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val token = "Bearer " + sharedPref.getString("ACCESS_TOKEN", "")

        val apiService = ApiClient.instance.create(ApiService::class.java)
        apiService.getCurrentUser(token).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val user = response.body()!!.data
                    Toast.makeText(
                        this@EditProfileActivity,
                        response.body()!!.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    // ✅ عرض بيانات المستخدم
                    binding.editName.setText(user.name)
                    binding.editEmail.setText(user.email)
                    binding.editPhone.setText(user.phone)
                    user.image?.let {
                        Glide.with(this@EditProfileActivity)
                            .load(it)
                            .placeholder(R.drawable.user)
                            .into(binding.profileImage)
                    }
                } else {
                    val errorBody = response.errorBody()?.string()

                    if (errorBody != null && errorBody.trim().startsWith("{")) {
                        try {
                            val json = JSONObject(errorBody)
                            val errorMessage = json.optString("message", "حدث خطأ")

                            Toast.makeText(
                                this@EditProfileActivity,
                                errorMessage,
                                Toast.LENGTH_SHORT
                            ).show()

                            if (errorMessage.contains("غير مصرح")) {
                                // 🧹 حذف التوكن
                                val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                                sharedPref.edit { clear() }

                                // 🔁 إعادة التوجيه إلى تسجيل الدخول
                                val intent =
                                    Intent(this@EditProfileActivity, LogInActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()
                            }

                        } catch (e: Exception) {
                            Toast.makeText(
                                this@EditProfileActivity,
                                e.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@EditProfileActivity,
                            "فشل في الاتصال بالخادم",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Toast.makeText(
                    this@EditProfileActivity,
                    "فشل الاتصال: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }


}