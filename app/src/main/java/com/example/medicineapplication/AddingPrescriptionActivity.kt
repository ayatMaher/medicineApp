package com.example.medicineapplication

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.WindowInsetsControllerCompat
import android.widget.ImageView
import android.widget.LinearLayout
import com.example.medicineapplication.databinding.ActivityAddingPrescriptionBinding
import java.io.File

class AddingPrescriptionActivity : AppCompatActivity() {

    lateinit var binding: ActivityAddingPrescriptionBinding
    private lateinit var imageUri: Uri

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                showImageInIconAdd(it)
            }
        }

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                showImageInIconAdd(imageUri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddingPrescriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        // عند الضغط على icon_add، نعرض حوار اختيار الصورة
        binding.iconAdd.setOnClickListener {
            showImagePickerDialog()
        }
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
        // طلب صلاحية الكاميرا فقط عند الحاجة (اختياري)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 100)
            return
        }

        val imageFile = File(
            getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "captured_image.jpg"
        )
        imageUri = FileProvider.getUriForFile(this, "$packageName.provider", imageFile)
        takePictureLauncher.launch(imageUri)
    }

    private fun showImageInIconAdd(uri: Uri) {
        val container = binding.iconAdd

        // حذف كل العناصر داخل icon_add
        container.removeAllViews()

        // إنشاء صورة جديدة
        val imageView = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            scaleType = ImageView.ScaleType.FIT_CENTER // ✅ مهم: لعرض الصورة بالحجم الطبيعي
            setImageURI(uri)
            adjustViewBounds = true // ✅ يسمح للارتفاع يتكيف حسب حجم الصورة
        }


        // إضافة الصورة داخل icon_add
        container.addView(imageView)
    }
}
