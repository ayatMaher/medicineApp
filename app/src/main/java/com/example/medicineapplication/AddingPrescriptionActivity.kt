package com.example.medicineapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.WindowInsetsControllerCompat
import com.example.medicineapplication.databinding.ActivityAddingPrescriptionBinding
import com.example.medicineapplication.validator.DrugNameExtractor
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File

@Suppress("DEPRECATION")
class AddingPrescriptionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddingPrescriptionBinding
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

        // صلاحيات READ_MEDIA_IMAGES (Android 13+)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                    102
                )
            }
        }

        binding.iconAdd.setOnClickListener {
            showImagePickerDialog()
        }

        binding.header.backButton.setOnClickListener {
            finish()
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
        container.removeAllViews()

        val imageView = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            scaleType = ImageView.ScaleType.FIT_CENTER
            setImageURI(uri)
            adjustViewBounds = true
        }
        container.addView(imageView)
        try {
            val image = InputImage.fromFilePath(this@AddingPrescriptionActivity, uri)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val fullText = visionText.text
                    val foundDrugs = DrugNameExtractor().extract(fullText)
                    if (foundDrugs.isNotEmpty()) {
                        val result = foundDrugs.joinToString(", ")
                        Log.d("EXTRACTED_DRUGS", "تم التعرف على الأدوية: $result")
                        Toast.makeText(
                            this@AddingPrescriptionActivity,
                            "تم التعرف على: $result",
                            Toast.LENGTH_LONG
                        ).show()
                        // يمكنك الانتقال لصفحة البحث هنا أو عرضها للمستخدم
                        if (foundDrugs.isNotEmpty()) {
                            val result = foundDrugs.joinToString(", ")
                            val drugNameForSearch = foundDrugs.first()
                            Toast.makeText(this, "تم التعرف على: $result", Toast.LENGTH_LONG).show()
                            binding.iconAdd.isEnabled = false
                            binding.btnContinue.setOnClickListener {
                                val intent = Intent(this, PharmacyActivity::class.java)
                                intent.putExtra("drug_name", drugNameForSearch)
                                startActivity(intent)
                            }
                        }
                    } else {
                        Toast.makeText(
                            this@AddingPrescriptionActivity,
                            "لم يتم العثور على أسماء أدوية معروفة",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                .addOnFailureListener { e ->
                    Log.e("OCR_ERROR", "خطأ أثناء تحليل النص: ${e.localizedMessage}")
                    Toast.makeText(
                        this@AddingPrescriptionActivity,
                        "فشل في قراءة النص: ${e.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "خطأ في تحميل الصورة", Toast.LENGTH_SHORT).show()
        }
    }

}
