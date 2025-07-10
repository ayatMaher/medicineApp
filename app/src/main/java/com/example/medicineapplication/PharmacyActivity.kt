package com.example.medicineapplication

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.medicineapplication.adapter.PharmacyAdapter
import com.example.medicineapplication.api.ApiClient
import com.example.medicineapplication.databinding.ActivityPharmacyBinding
import com.example.medicineapplication.model.Pharmacy
import com.example.medicineapplication.model.PharmacyResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("DEPRECATION")
class PharmacyActivity : AppCompatActivity(), PharmacyAdapter.ItemClickListener {
    lateinit var binding: ActivityPharmacyBinding
    private lateinit var pharmacyAdapter: PharmacyAdapter

    private val pharmacyList = MutableLiveData<List<Pharmacy>>()
    private val error = MutableLiveData<String>()
    private var token: String=""
    private var userId: Int=-1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPharmacyBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        //status bar
        val window = this.window
        window.statusBarColor = ContextCompat.getColor(this, R.color.white2)
//        // اجعل الأيقونات داكنة إذا كان الخلفية فاتحة
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        //back arrow
        binding.backArrow.setOnClickListener {
            finish()
        }

        val sharedPref = getSharedPreferences("MyAppPrefs",MODE_PRIVATE)
        token = sharedPref.getString("ACCESS_TOKEN", "") ?: ""
        userId = sharedPref.getInt("USER_ID", -1)

        val medicineName= intent.getStringExtra("medicine_name")
        binding.edtSearch.setText(medicineName)


        fetchPharmaciesNearby(token,medicineName.toString())


        pharmacyAdapter = PharmacyAdapter(this, emptyList(), this)
        binding.rvPharmacy.adapter = pharmacyAdapter

        observeData()

        setupSearchListeners()


    }

    private fun setupSearchListeners() {
        // الضغط على Enter من الكيبورد
        binding.edtSearch.setOnEditorActionListener { _: TextView, actionId: Int, event: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                performSearch()
                true
            } else {
                false
            }
        }

        // الضغط على أيقونة البحث
        binding.searchImage.setOnClickListener {
            performSearch()
        }
    }

    private fun performSearch() {
        val medicineName = binding.edtSearch.text.toString().trim()
        if (medicineName.isEmpty()) {
            Toast.makeText(this, "يرجى إدخال اسم الدواء", Toast.LENGTH_SHORT).show()
            return
        }



        if (token.isNotEmpty()) {
            fetchPharmaciesNearby(token, medicineName)
        } else {
            Toast.makeText(this, "يرجى تسجيل الدخول أولاً", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchPharmaciesNearby(token: String, treatment: String) {
        ApiClient.apiService.searchTreatmentPharmacyNearby("Bearer $token", treatment)
            .enqueue(object : Callback<PharmacyResponse> {
                override fun onResponse(
                    call: Call<PharmacyResponse>,
                    response: Response<PharmacyResponse>
                ) {
                    if (response.isSuccessful) {
                        val pharmacies = response.body()?.data?.map {
                            it.copy(isFeatured = true) // ⭐️ هذا هو المفتاح لتفعيل ViewHolderPharmacySearch
                        } ?: emptyList()
                        pharmacyList.value = pharmacies
                        if (pharmacies.isNotEmpty()) {
                            Toast.makeText(
                                this@PharmacyActivity,
                                "توجد صيدليات قريبة لهذا العلاج",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                this@PharmacyActivity,
                                "لا توجد صيدليات قريبة لهذا العلاج",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@PharmacyActivity,
                            "فشل في جلب البيانات من السيرفر",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<PharmacyResponse>, t: Throwable) {
                    Toast.makeText(
                        this@PharmacyActivity,
                        "حدث خطأ أثناء الاتصال: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun observeData() {
        pharmacyList.observe(this, Observer { pharmacies ->
            pharmacyAdapter.updateData(pharmacies)
        })

        error.observe(this, Observer { msg ->
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        })
    }


    override fun onItemClickPharmacy(position: Int, id: String) {
        val currentList = pharmacyList.value ?: return
        val pharmacy = currentList[position]
        val intent = Intent(this@PharmacyActivity, PharmacyDetailsActivity::class.java)
        intent.putExtra("pharmacy", pharmacy)
        startActivity(intent)
    }

    override fun onAddToFavorite(pharmacyId: Int) {
        TODO("Not yet implemented")
    }


}