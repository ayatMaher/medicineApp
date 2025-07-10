package com.example.medicineapplication

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.medicineapplication.databinding.ActivityLocationBinding
import android.Manifest
import android.content.Context
import android.location.LocationManager
import android.provider.Settings

@Suppress("DEPRECATION")
class LocationActivity : AppCompatActivity() {
    lateinit var binding: ActivityLocationBinding
    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startActivity(Intent(this, CurrentLocationActivity::class.java))
        } else {
            // المستخدم رفض الصلاحية
            Toast.makeText(this, "تم رفض الإذن للوصول إلى الموقع", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //statusBar Color
        window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar_color_log)
        // اجعل الأيقونات داكنة إذا كان الخلفية فاتحة
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true


        binding.btnCurrentLocation.setOnClickListener {

            checkLocationSettingsAndPermission()
        }

        binding.btnChooseLocation.setOnClickListener {
            var intent= Intent(this, AddAddressActivity::class.java)
            startActivity(intent)
            finish()
        }



    }
    private fun checkLocationSettingsAndPermission() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if (!isGpsEnabled) {
            // فتح إعدادات الموقع
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        } else {
            // طلب إذن الوصول للموقع

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
            ) {
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            } else {
                startActivity(Intent(this, CurrentLocationActivity::class.java))
            }
        }
        binding.btnChooseLocation.setOnClickListener {
            val intent = Intent(this, AddAddressActivity::class.java)
            startActivity(intent)
        }


    }
}