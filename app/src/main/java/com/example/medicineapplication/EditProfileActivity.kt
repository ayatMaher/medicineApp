package com.example.medicineapplication

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.content.edit
import com.bumptech.glide.Glide
import com.example.medicineapplication.api.ApiClient
import com.example.medicineapplication.api.ApiService
import com.example.medicineapplication.databinding.ActivityEditProfileBinding
import com.example.medicineapplication.model.UserResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class EditProfileActivity : AppCompatActivity() {
    lateinit var binding: ActivityEditProfileBinding
    private lateinit var imageView: ImageView
    private lateinit var selectButton: ImageView
    private lateinit var cameraImageUri: Uri
    private var selectedImageUri: Uri? = null

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                selectedImageUri = it
                imageView.setImageURI(it)
            }
        }

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                selectedImageUri = cameraImageUri
                imageView.setImageURI(cameraImageUri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }
        imageView = binding.profileImage
        selectButton = binding.addIcon

        selectButton.setOnClickListener { showImagePickerDialog() }
        binding.btnSave.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            binding.btnSave.isEnabled = false
            binding.btnSave.alpha = 0.5f
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
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE),
            1
        )
        val imageFile =
            File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "profile_image.jpg")
        cameraImageUri = FileProvider.getUriForFile(this, "$packageName.provider", imageFile)
        takePictureLauncher.launch(cameraImageUri)
    }

    private fun getFileFromUri(uri: Uri): File? {
        val inputStream = contentResolver.openInputStream(uri) ?: return null
        val tempFile = File.createTempFile("temp_image", ".jpg", cacheDir)
        tempFile.outputStream().use { output ->
            inputStream.copyTo(output)
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
                    handleErrorResponse(response)
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

    private fun updateProfile() {
        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val token = "Bearer " + sharedPref.getString("ACCESS_TOKEN", "")
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

        apiService.updateUser(token, nameBody, emailBody, phoneBody, passwordBody, null)
            .enqueue(object : Callback<UserResponse> {
                override fun onResponse(
                    call: Call<UserResponse>,
                    response: Response<UserResponse>
                ) {
                    binding.progressBar.visibility = View.GONE
                    binding.btnSave.isEnabled = true
                    binding.btnSave.alpha = 1f
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(
                            this@EditProfileActivity,
                            "تم تحديث البيانات بنجاح",
                            Toast.LENGTH_SHORT
                        ).show()
                        loadUserData()
                    } else {
                        handleErrorResponse(response)
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
                }
            })
    }

    private fun handleErrorResponse(response: Response<*>) {
        val errorBody = response.errorBody()?.string()
        if (errorBody != null && errorBody.trim().startsWith("{")) {
            try {
                val json = JSONObject(errorBody)
                val errorMessage = json.optString("message", "حدث خطأ")
                Toast.makeText(this@EditProfileActivity, errorMessage, Toast.LENGTH_SHORT).show()

                if (errorMessage.contains("غير مصرح")) {
                    val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                    sharedPref.edit { clear() }
                    val intent = Intent(this@EditProfileActivity, LogInActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            } catch (e: Exception) {
                Toast.makeText(this@EditProfileActivity, "خطأ غير متوقع", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this@EditProfileActivity, "فشل في الاتصال بالخادم", Toast.LENGTH_SHORT)
                .show()
        }
    }
}
