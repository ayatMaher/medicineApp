package com.example.medicineapplication

import android.location.Geocoder
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.medicineapplication.databinding.ActivityAddAddressBinding
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import java.util.*

@Suppress("DEPRECATION")
class AddAddressActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityAddAddressBinding
    private lateinit var map: GoogleMap
    private lateinit var geocoder: Geocoder
    private var selectedLatLng: LatLng? = null
    private var marker: Marker? = null
    private var selectedAddress: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // statusBar Color
        window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar_color_log)
        // اجعل الأيقونات داكنة إذا كان الخلفية فاتحة
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        binding.btnBack.setOnClickListener {
            finish()
        }

        geocoder = Geocoder(this, Locale.getDefault())

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.confirmButton.setOnClickListener {
            if (selectedLatLng != null) {
                Toast.makeText(
                    this,
                    "العنوان: $selectedAddress\nالإحداثيات: ${selectedLatLng!!.latitude}, ${selectedLatLng!!.longitude}",
                    Toast.LENGTH_LONG
                ).show()
                // يمكنك هنا إرسال الإحداثيات والعنوان إلى API أو Activity أخرى
            } else {
                Toast.makeText(this, "يرجى اختيار موقع على الخريطة", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        val defaultLatLng = LatLng(31.5015, 34.4666) // غزة
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLatLng, 13f))

        map.setOnMapClickListener { latLng ->
            selectedLatLng = latLng

            // إزالة الماركر السابق
            marker?.remove()

            // إضافة ماركر جديد
            marker = map.addMarker(MarkerOptions().position(latLng).title("الموقع المحدد"))

            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                selectedAddress = addresses[0].getAddressLine(0)
                binding.addressText.text = selectedAddress
            } else {
                selectedAddress = ""
                binding.addressText.text = "لم يتم العثور على عنوان"
            }
        }
    }
}
