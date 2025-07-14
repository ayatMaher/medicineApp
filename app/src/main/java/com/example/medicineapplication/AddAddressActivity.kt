package com.example.medicineapplication

import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.medicineapplication.api.ApiClient
import com.example.medicineapplication.databinding.ActivityAddAddressBinding
import com.example.medicineapplication.model.GeneralResponse
import com.example.medicineapplication.model.StoreLocationRequest
import com.example.medicineapplication.model.StoreLocationResponse
import com.example.medicineapplication.model.UserResponse
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

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

        // تغيير لون شريط الحالة
        window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar_color_log)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        binding.btnBack.setOnClickListener {
            finish()
        }

        geocoder = Geocoder(this, Locale.getDefault())

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.confirmButton.setOnClickListener {
            saveOrUpdateLocation()

        }

    }

    private fun saveOrUpdateLocation() {
        val latLng = selectedLatLng
        if (latLng == null) {
            Toast.makeText(this, "يرجى اختيار موقع على الخريطة", Toast.LENGTH_SHORT).show()
            return
        }

        val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
        if (addresses.isNullOrEmpty()) {
            Toast.makeText(this, "تعذر العثور على العنوان", Toast.LENGTH_SHORT).show()
            return
        }

        val address = addresses[0]
        val selectedAddress = address.getAddressLine(0) ?: ""

        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val userId = sharedPref.getInt("USER_ID", -1)
        val token = sharedPref.getString("ACCESS_TOKEN", "") ?: ""

        if (userId == -1 || token.isEmpty()) {
            Toast.makeText(this, "يرجى تسجيل الدخول أولاً", Toast.LENGTH_SHORT).show()
            return
        }

        val bearerToken = if (token.startsWith("Bearer ")) token else "Bearer $token"

        // طلب جلب الموقع الحالي من السيرفر
        ApiClient.apiService.getCurrentUser(bearerToken).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val existingLocation = response.body()?.data?.location
                    if (existingLocation != null) {
                        // تحديث الموقع
                        updateLocation(
                            userId,
                            latLng,
                            address,
                            bearerToken
                        )
                    } else {
                        // إضافة موقع جديد
                        storeLocation(
                            userId,
                            latLng,
                            address,
                            bearerToken
                        )
                    }
                } else {
                    // في حالة فشل جلب الموقع الحالي، يمكن اختيار إضافة الموقع
                    storeLocation(
                        userId,
                        latLng,
                        address,
                        bearerToken
                    )
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Toast.makeText(
                    this@AddAddressActivity,
                    "خطأ في الاتصال: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun updateLocation(
        userId: Int,
        latLng: LatLng,
        address: android.location.Address,
        token: String
    ) {
        val request = StoreLocationRequest(
            user_id = userId,
            latitude = latLng.latitude,
            longitude = latLng.longitude,
            formatted_address = address.getAddressLine(0) ?: "",
            country = address.countryName ?: "Palestine",
            region = address.adminArea ?: "Gaza",
            city = address.locality ?: "Gaza City",
            district = address.subLocality ?: "Unknown District",
            postal_code = address.postalCode ?: "00000",
            location_type = "home"
        )

        ApiClient.apiService.updateLocation(token, request)
            .enqueue(object : Callback<GeneralResponse> {
                override fun onResponse(
                    call: Call<GeneralResponse>,
                    response: Response<GeneralResponse>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(
                            this@AddAddressActivity,
                            "تم تحديث الموقع بنجاح",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent =
                            Intent(this@AddAddressActivity, NavigationDrawerActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            this@AddAddressActivity,
                            "فشل في تحديث الموقع",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<GeneralResponse>, t: Throwable) {
                    Toast.makeText(
                        this@AddAddressActivity,
                        "خطأ في الاتصال: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val defaultLatLng = LatLng(31.5015, 34.4666) // غزة
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLatLng, 13f))

        map.setOnMapClickListener { latLng ->
            selectedLatLng = latLng
            marker?.remove()
            marker = map.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title("الموقع المحدد")
            )

            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                selectedAddress = addresses[0].getAddressLine(0)
                binding.addressText.text = selectedAddress
            } else {
                selectedAddress = ""
                binding.addressText.text = "لم يتم العثور على عنوان"
            }
        }
        loadCurrentUserLocation()
    }

    private fun storeLocation(
        userId: Int,
        latLng: LatLng,
        address: android.location.Address,
        token: String
    ) {
        val request = StoreLocationRequest(
            user_id = userId,
            latitude = latLng.latitude,
            longitude = latLng.longitude,
            formatted_address = address.getAddressLine(0) ?: "",
            country = address.countryName ?: "Palestine",
            region = address.adminArea ?: "Gaza",
            city = address.locality ?: "Gaza City",
            district = address.subLocality ?: "Unknown District",
            postal_code = address.postalCode ?: "00000",
            location_type = "home"
        )

        ApiClient.apiService.storeUserLocation(token, request)
            .enqueue(object : Callback<StoreLocationResponse> {
                override fun onResponse(
                    call: Call<StoreLocationResponse>,
                    response: Response<StoreLocationResponse>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(
                            this@AddAddressActivity,
                            "تم حفظ الموقع بنجاح",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this@AddAddressActivity,
                            "فشل في حفظ الموقع",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<StoreLocationResponse>, t: Throwable) {
                    Toast.makeText(
                        this@AddAddressActivity,
                        "خطأ في الاتصال: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }


    private fun loadCurrentUserLocation() {
        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val token = sharedPref.getString("ACCESS_TOKEN", "") ?: ""
        if (token.isEmpty()) {
            Toast.makeText(this, "بيانات المستخدم غير موجودة", Toast.LENGTH_SHORT).show()
            return
        }
        val bearerToken = if (token.startsWith("Bearer ")) token else "Bearer $token"

        ApiClient.apiService.getCurrentUser(bearerToken).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val location = response.body()!!.data.location
                    if (location != null) {
                        selectedLatLng = LatLng(location.latitude, location.longitude)
                        marker?.remove()
                        marker = map.addMarker(
                            MarkerOptions().position(selectedLatLng!!)
                                .title(location.formatted_address)
                        )
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLatLng!!, 15f))
                        binding.addressText.text = location.formatted_address
                    }
                } else {
                    Toast.makeText(
                        this@AddAddressActivity,
                        "فشل في جلب الموقع الحالي",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Toast.makeText(
                    this@AddAddressActivity,
                    "خطأ في الاتصال: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

}


