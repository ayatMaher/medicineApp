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
import android.widget.Toast
import com.example.medicineapplication.api.ApiClient
import com.example.medicineapplication.api.ApiService
import com.example.medicineapplication.model.UserResponse
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.core.content.edit


class EditProfileActivity : AppCompatActivity() {
    lateinit var binding: ActivityEditProfileBinding
    private lateinit var imageView: ImageView
    private lateinit var selectButton: ImageView

    private lateinit var cameraImageUri: Uri

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                imageView.setImageURI(it)
            }
        }

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
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
                                "خطأ غير متوقع",
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
