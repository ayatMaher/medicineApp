package com.example.medicineapplication

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.medicineapplication.databinding.ActivityCurrentLocationBinding
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import java.util.*

class CurrentLocationActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityCurrentLocationBinding
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationProvider: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private lateinit var geocoder: Geocoder

    private var locationMarker: Marker? = null
    private val LOCATION_PERMISSION_CODE = 1001
    private val LOCATION_SETTINGS_REQUEST_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCurrentLocationBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        //statusBar Color
        window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar_color_log)
        // اجعل الأيقونات داكنة إذا كان الخلفية فاتحة
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this, Locale("ar")) // استخدام اللغة العربية

        // إعداد طلب الموقع
        locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 5000
        ).apply {
            setMinUpdateIntervalMillis(2000)
        }.build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation ?: return
                val latLng = LatLng(location.latitude, location.longitude)
                updateMapLocation(latLng)
            }
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.confirmButton.setOnClickListener {
            locationMarker?.let {
                val latLng = it.position
                val addressText = binding.address.text.toString()
                Toast.makeText(
                    this,
                    "الموقع: ${latLng.latitude}, ${latLng.longitude}\n$addressText",
                    Toast.LENGTH_LONG
                ).show()
            } ?: Toast.makeText(this, "لم يتم تحديد الموقع", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        if (checkLocationPermission()) {
            try {
                map.isMyLocationEnabled = true
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
            getLastKnownLocation()
            checkLocationSettingsAndStartUpdates()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_CODE
            )
        }

        map.setOnMapClickListener { latLng ->
            updateMapLocation(latLng)
        }
    }

    private fun getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        fusedLocationProvider.lastLocation
            .addOnSuccessListener { location ->
                location?.let {
                    val userLatLng = LatLng(it.latitude, it.longitude)
                    updateMapLocation(userLatLng)
                } ?: Toast.makeText(this, "تعذر تحديد الموقع الحالي", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateMapLocation(latLng: LatLng) {
        locationMarker?.remove()
        locationMarker = map.addMarker(
            MarkerOptions()
                .position(latLng)
                .title("الموقع الحالي")
        )
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))

        val addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
        val addressLine = if (!addressList.isNullOrEmpty()) {
            addressList[0].getAddressLine(0)
        } else {
            "عنوان غير معروف"
        }

        binding.address.text = addressLine
    }

    private fun checkLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun startLocationUpdates() {
        if (checkLocationPermission()) {
            fusedLocationProvider.requestLocationUpdates(
                locationRequest,
                locationCallback,
                mainLooper
            )
        }
    }

    private fun checkLocationSettingsAndStartUpdates() {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)

        val settingsClient = LocationServices.getSettingsClient(this)
        settingsClient.checkLocationSettings(builder.build())
            .addOnSuccessListener {
                startLocationUpdates()
            }
            .addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    try {
                        exception.startResolutionForResult(this, LOCATION_SETTINGS_REQUEST_CODE)
                    } catch (sendEx: Exception) {
                        sendEx.printStackTrace()
                    }
                } else {
                    Toast.makeText(this, "يرجى تفعيل الموقع من الإعدادات", Toast.LENGTH_LONG).show()
                }
            }
    }

    @Suppress("DEPRECATION")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_CODE &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            try {
                map.isMyLocationEnabled = true
                getLastKnownLocation()
                checkLocationSettingsAndStartUpdates()
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        } else {
            Toast.makeText(this, "يرجى منح إذن الموقع", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LOCATION_SETTINGS_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            startLocationUpdates()
        }
    }

    override fun onPause() {
        super.onPause()
        fusedLocationProvider.removeLocationUpdates(locationCallback)
    }

    override fun onResume() {
        super.onResume()
        if (::map.isInitialized && checkLocationPermission()) {
            startLocationUpdates()
        }
    }
}
