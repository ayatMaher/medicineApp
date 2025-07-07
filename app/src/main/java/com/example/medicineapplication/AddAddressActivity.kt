package com.example.medicineapplication

import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.medicineapplication.api.ApiClient
import com.example.medicineapplication.databinding.ActivityAddAddressBinding
import com.example.medicineapplication.model.StoreLocationRequest
import com.example.medicineapplication.model.StoreLocationResponse
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
            if (selectedLatLng != null) {
                val addresses = geocoder.getFromLocation(
                    selectedLatLng!!.latitude,
                    selectedLatLng!!.longitude,
                    1
                )

                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    selectedAddress = address.getAddressLine(0) ?: ""

                    val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                    val userId = sharedPref.getInt("USER_ID", -1)
                    val token = sharedPref.getString("ACCESS_TOKEN", "") ?: ""
                    val bearerToken = if (token.startsWith("Bearer ")) token else "Bearer $token"

                    if (userId == -1 || token.isEmpty()) {
                        Toast.makeText(this, "بيانات المستخدم غير موجودة", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    //  استخدام قيم افتراضية إذا كانت البيانات ناقصة
                    val countryName = address.countryName ?: "Palestine"
                    val regionName = address.adminArea ?: "Gaza"
                    val cityName = address.locality ?: "Gaza City"
                    val districtName = address.subLocality ?: "Unknown District"
                    val postalCode = address.postalCode ?: "00000"


                    val request = StoreLocationRequest(
                        user_id = userId,
                        latitude = selectedLatLng!!.latitude,
                        longitude = selectedLatLng!!.longitude,
                        formatted_address = selectedAddress,
                        country = countryName,
                        region = regionName,
                        city = cityName,
                        district = districtName,
                        postal_code = postalCode,
                        location_type = "home"
                    )

                    Log.d("MyLog", "Token = $bearerToken")
                    Log.d("MyLog", "Request = $request")

                    ApiClient.apiService.storeUserLocation(bearerToken, request)
                        .enqueue(object : Callback<StoreLocationResponse> {
                            override fun onResponse(
                                call: Call<StoreLocationResponse>,
                                response: Response<StoreLocationResponse>
                            ) {
                                if (response.isSuccessful && response.body()?.success == true) {
                                    Toast.makeText(
                                        this@AddAddressActivity,
                                        " تم حفظ العنوان بنجاح",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    Log.d("MyLog", "تم حفظ العنوان بنجاح")
                                    val intent = Intent(this@AddAddressActivity, NavigationDrawerActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    val errorBody = response.errorBody()?.string()
                                    Log.e("MyLog", "فشل في الحفظ - كود: ${response.code()}\nالرد: $errorBody")
                                    Toast.makeText(
                                        this@AddAddressActivity,
                                        "فشل في الحفظ: ${response.message()}",
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
                                Log.e("MyLog", "API error", t)
                            }
                        })

                } else {
                    Toast.makeText(this, "تعذر العثور على العنوان", Toast.LENGTH_SHORT).show()
                }
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
    }
}
