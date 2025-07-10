package com.example.medicineapplication

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.medicineapplication.api.ApiClient
import com.example.medicineapplication.databinding.ActivityCurrentLocationBinding
import com.example.medicineapplication.model.StoreLocationRequest
import com.example.medicineapplication.model.StoreLocationResponse
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*

@Suppress("DEPRECATION")
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
            storeCurrentLocation()
        }

    }

    @SuppressLint("MissingPermission")
    private fun storeCurrentLocation() {
        val currentLatLng = locationMarker?.position

        if (currentLatLng != null) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val addresses = geocoder.getFromLocation(
                        currentLatLng.latitude,
                        currentLatLng.longitude,
                        1
                    )

                    if (!addresses.isNullOrEmpty()) {
                        val address = addresses[0]
                        val formattedAddress = address.getAddressLine(0) ?: "عنوان غير متوفر"
                        val countryName = address.countryName ?: "Palestine"
                        val regionName = address.adminArea ?: "Gaza"
                        val cityName = address.locality ?: "Gaza City"
                        val districtName = address.subLocality ?: "Unknown District"
                        val postalCode = address.postalCode ?: "00000"

                        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                        val userId = sharedPref.getInt("USER_ID", -1)
                        val token = sharedPref.getString("ACCESS_TOKEN", "") ?: ""

                        if (userId == -1 || token.isEmpty()) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@CurrentLocationActivity,
                                    "بيانات المستخدم غير موجودة",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            return@launch
                        }

                        val bearerToken =
                            if (token.startsWith("Bearer ")) token else "Bearer $token"

                        val request = StoreLocationRequest(
                            user_id = userId,
                            latitude = currentLatLng.latitude,
                            longitude = currentLatLng.longitude,
                            formatted_address = formattedAddress,
                            country = countryName,
                            region = regionName,
                            city = cityName,
                            district = districtName,
                            postal_code = postalCode,
                            location_type = "home"
                        )

                        withContext(Dispatchers.Main) {
                            // Call API
                            ApiClient.apiService.storeUserLocation(bearerToken, request)
                                .enqueue(object : Callback<StoreLocationResponse> {
                                    override fun onResponse(
                                        call: Call<StoreLocationResponse>,
                                        response: Response<StoreLocationResponse>
                                    ) {
                                        if (response.isSuccessful && response.body()?.success == true) {
                                            Toast.makeText(
                                                this@CurrentLocationActivity,
                                                response.body()!!.message,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            val intent = Intent(
                                                this@CurrentLocationActivity,
                                                NavigationDrawerActivity::class.java
                                            )
                                            startActivity(intent)
                                        } else {
                                            val errorBody = response.errorBody()?.string()
                                            Log.e(
                                                "LocationError",
                                                "Response code: ${response.code()} - $errorBody"
                                            )
                                            Toast.makeText(
                                                this@CurrentLocationActivity,
                                                "❌ لم يتم الحفظ: ${response.message()}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }

                                    override fun onFailure(
                                        call: Call<StoreLocationResponse>,
                                        t: Throwable
                                    ) {
                                        Toast.makeText(
                                            this@CurrentLocationActivity,
                                            "📡 خطأ في الاتصال: ${t.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        Log.e("LocationError", "API Failure", t)
                                    }
                                })
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@CurrentLocationActivity,
                                "📍 لم يتم العثور على عنوان",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@CurrentLocationActivity,
                            "⚠️ فشل في جلب العنوان ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

        } else {
            Toast.makeText(this, "يرجى تحديد موقع على الخريطة", Toast.LENGTH_SHORT).show()
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

    @SuppressLint("SetTextI18n")
    private fun updateMapLocation(latLng: LatLng) {
        locationMarker?.remove()
        locationMarker = map.addMarker(
            MarkerOptions()
                .position(latLng)
                .title("الموقع الحالي")
        )
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                if (!addressList.isNullOrEmpty()) {
                    val address = addressList[0]
//                    val country = address.countryName ?: "غير معروف"
//                    val adminArea = address.adminArea ?: "غير معروف"    // المحافظة أو المنطقة
//                    val city = address.locality ?: "غير معروف"          // المدينة
//                    val district = address.subLocality ?: "غير معروف"   // الحي
//                    val postalCode = address.postalCode ?: "غير معروف"
                    val fullAddress = address.getAddressLine(0) ?: "غير معروف"

                    withContext(Dispatchers.Main) {
                        binding.address.text = fullAddress
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        binding.address.text = "تعذر العثور على تفاصيل العنوان"
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    binding.address.text = "فشل في جلب العنوان، تحقق من الإنترنت"
                }
            }
        }
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
        if (requestCode == LOCATION_SETTINGS_REQUEST_CODE && resultCode == RESULT_OK) {
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
