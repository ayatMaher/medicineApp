package com.example.medicineapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.medicineapplication.api.ApiClient
import com.example.medicineapplication.api.ApiService
import com.example.medicineapplication.databinding.ActivityCurrentUserLocationBinding
import com.example.medicineapplication.model.UserResponse
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("DEPRECATION")
class CurrentUserLocationActivity : AppCompatActivity(), OnMapReadyCallback {
    lateinit var binding: ActivityCurrentUserLocationBinding
    private lateinit var apiService: ApiService
    private lateinit var token: String
    private lateinit var mMap: GoogleMap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCurrentUserLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //statusBar Color
        window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar_color_log)
        // اجعل الأيقونات داكنة إذا كان الخلفية فاتحة
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true


        //back button
        binding.header.backButton.setOnClickListener {
            finish()
        }
        // إعداد Retrofit
        apiService = ApiClient.instance.create(ApiService::class.java)
        token =
            getSharedPreferences("MyAppPrefs", MODE_PRIVATE).getString("ACCESS_TOKEN", "") ?: " "
        getUserLocation()

        //map
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // change button
        binding.changeButton.setOnClickListener {
            val intent = Intent(this, AddAddressActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getUserLocation() {
        apiService.getCurrentUser(token).enqueue(object : Callback<UserResponse> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val location = response.body()!!.data.location
                    location?.let {
                        binding.address.text = "${it.formatted_address}, ${it.city}, ${it.country}"
                        val userLatLng = LatLng(it.latitude, it.longitude)
                        // Marker + Zoom
                        mMap.addMarker(
                            MarkerOptions().position(userLatLng).title(it.formatted_address)
                        )
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15f))
                    }
                } else {
                    Toast.makeText(
                        this@CurrentUserLocationActivity,
                        "فشل في جلب الموقع",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Toast.makeText(
                    this@CurrentUserLocationActivity,
                    "خطأ: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }
}