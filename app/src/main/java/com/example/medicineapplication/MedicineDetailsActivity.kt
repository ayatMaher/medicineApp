package com.example.medicineapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.bumptech.glide.Glide
import com.example.medicineapplication.PharmacyDetailsActivity
import com.example.medicineapplication.api.ApiClient
import com.example.medicineapplication.databinding.ActivityMedicineDetailsBinding
import com.example.medicineapplication.model.FavoriteMedicineRequest
import com.example.medicineapplication.model.FavoriteMedicineResponse
import com.example.medicineapplication.model.Pharmacy
import com.example.medicineapplication.model.Treatment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("DEPRECATION")
class MedicineDetailsActivity : AppCompatActivity() {
    lateinit var binding: ActivityMedicineDetailsBinding
    private lateinit var medicine: Treatment
    private var token: String = " "
    private var userId: Int = -1


    @SuppressLint("CommitTransaction")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMedicineDetailsBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        token = sharedPref.getString("ACCESS_TOKEN", "") ?: ""
        userId = sharedPref.getInt("USER_ID", -1)


        medicine = intent.getParcelableExtra("medicine") ?: run {
            Toast.makeText(this, "فشل في تحميل بيانات العلاج", Toast.LENGTH_SHORT).show()
            finish()
            return
        }


        //statusBar Color
        window.statusBarColor = ContextCompat.getColor(this, R.color.light_green)
        // اجعل الأيقونات داكنة إذا كان الخلفية فاتحة
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
        // title text
        binding.header.titleText.text = "تفاصيل الدواء"
        // back arrow
        binding.header.backButton.setOnClickListener {
            finish()
        }



        binding.medicineName.text = medicine.name
        binding.txtMedicineDescription.text = medicine.description

        Glide.with(this)
            .load(medicine.image)
            .placeholder(R.drawable.medicine_img)
            .into(binding.imageMedicine)


        binding.txtMedicineType.text = medicine.category.name

        if (medicine.is_favorite == true) {
            binding.favoriteImg.setImageResource(R.drawable.red_favorite)
        } else {
            binding.favoriteImg.setImageResource(R.drawable.favorite)
        }


        binding.priceLayout.visibility = View.VISIBLE
        binding.line1.visibility = View.VISIBLE
        val count = medicine.pharmacy_count_available.toString()
        binding.countPharmacis.text = "متاح في ${count} صيدلية"
        binding.txtMedicinePrice.text =
            "${medicine.pharmacy_count_available ?: 0} صيدلية توفر هذا الدواء"



        showUsage()

        // show pharmacy icon
        binding.showPharmacyPage.setOnClickListener {
            val medicineName = binding.medicineName.text.toString()
            val intent = Intent(this, PharmacyActivity::class.java)
            intent.putExtra("medicine_name", medicineName)
            startActivity(intent)
        }

        binding.favoriteImg.setOnClickListener {
            val userId = getSharedPreferences("MyAppPrefs", MODE_PRIVATE).getInt("USER_ID", -1)
            if (userId == -1) {
                Toast.makeText(this, "يرجى تسجيل الدخول أولاً", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            addMedicineToFavorite(medicine.id)
        }

        binding.txtUsage.setOnClickListener {
            showUsage()
        }

        binding.txtInstructions.setOnClickListener {
            showInstruction()
        }
        binding.txtSideEffects.setOnClickListener {
            showSideEffects()
        }
    }


    private fun updateFavoriteIcon(isFavorite: Boolean) {
        if (isFavorite) {
            binding.favoriteImg.setImageResource(R.drawable.red_favorite)
        } else {
            binding.favoriteImg.setImageResource(R.drawable.favorite)
        }
    }

    @SuppressLint("ResourceAsColor", "SetTextI18n")
    private fun showUsage() {
        binding.txtUsage.setTextColor(ContextCompat.getColor(this, R.color.primary_color))
        binding.txtLineUsage.visibility = View.VISIBLE
        binding.txtSideEffects.setTextColor(ContextCompat.getColor(this, R.color.black))
        binding.txtLineSideEffect.visibility = View.INVISIBLE
        binding.txtInstructions.setTextColor(ContextCompat.getColor(this, R.color.black))
        binding.txtLineInstructions.visibility = View.INVISIBLE
        binding.txtTabContent.text = medicine.how_to_use
    }

    @SuppressLint("ResourceAsColor", "SetTextI18n")
    private fun showInstruction() {
        binding.txtUsage.setTextColor(ContextCompat.getColor(this, R.color.black))
        binding.txtLineUsage.visibility = View.INVISIBLE
        binding.txtSideEffects.setTextColor(ContextCompat.getColor(this, R.color.black))
        binding.txtLineSideEffect.visibility = View.INVISIBLE
        binding.txtInstructions.setTextColor(ContextCompat.getColor(this, R.color.primary_color))
        binding.txtLineInstructions.visibility = View.VISIBLE
        binding.txtTabContent.text = medicine.instructions

    }

    @SuppressLint("ResourceAsColor", "SetTextI18n")
    private fun showSideEffects() {
        binding.txtUsage.setTextColor(ContextCompat.getColor(this, R.color.black))
        binding.txtLineUsage.visibility = View.INVISIBLE
        binding.txtSideEffects.setTextColor(ContextCompat.getColor(this, R.color.primary_color))
        binding.txtLineSideEffect.visibility = View.VISIBLE
        binding.txtInstructions.setTextColor(ContextCompat.getColor(this, R.color.black))
        binding.txtLineInstructions.visibility = View.INVISIBLE
        binding.txtTabContent.text = medicine.side_effects
    }


    private fun addMedicineToFavorite(medicineId: Int) {

        if (token.isEmpty() || userId == -1) {
            Toast.makeText(this, "يرجى تسجيل الدخول أولاً", Toast.LENGTH_SHORT).show()
            return
        }

        val request = FavoriteMedicineRequest(userId, medicineId)

        ApiClient.apiService.storFavoriteMedicine(token, request)
            .enqueue(object : Callback<FavoriteMedicineResponse> {
                override fun onResponse(
                    call: Call<FavoriteMedicineResponse>,
                    response: Response<FavoriteMedicineResponse>
                ) {
                    val errorBody = response.errorBody()?.string()

                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(
                            this@MedicineDetailsActivity,
                            "تمت الإضافة إلى المفضلة",
                            Toast.LENGTH_SHORT
                        ).show()

                        // ✅ تحديث حالة isFavorite في القائمة وإبلاغ الـ Adapter
                        medicine.is_favorite = true
                        updateFavoriteIcon(true)

                    } else {
                        val errorMessage = try {
                            val json = org.json.JSONObject(errorBody ?: "")
                            json.optJSONObject("data")?.optString("error")
                                ?: "فشل في الإضافة للمفضلة"
                        } catch (e: Exception) {
                            "فشل في الإضافة للمفضلة"
                        }

                        Log.e(
                            "FavoriteError",
                            "Response code: ${response.code()}, Error body: $errorBody"
                        )
                        Toast.makeText(
                            this@MedicineDetailsActivity,
                            errorMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<FavoriteMedicineResponse>, t: Throwable) {
                    Toast.makeText(this@MedicineDetailsActivity, "خطأ: ${t.message}", Toast.LENGTH_SHORT).show()

                }
            })



    }
}







