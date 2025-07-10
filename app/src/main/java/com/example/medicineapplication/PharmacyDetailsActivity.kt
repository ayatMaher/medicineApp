package com.example.medicineapplication

import RatingAdapter
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.medicineapplication.adapter.MedicinePharmacyDetailsAdapter
import com.example.medicineapplication.api.ApiClient
import com.example.medicineapplication.databinding.ActivityPharmacyDetailsBinding
import com.example.medicineapplication.model.FavoritePharmacyRequest
import com.example.medicineapplication.model.FavoritePharmacyResponse
import com.example.medicineapplication.model.GeneralResponse
import com.example.medicineapplication.model.Medicine
import com.example.medicineapplication.model.MedicineResponse
import com.example.medicineapplication.model.Pharmacy
import com.example.medicineapplication.model.Rating
import com.example.medicineapplication.model.storeTreatmentAvailbilteRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("DEPRECATION")
class PharmacyDetailsActivity : AppCompatActivity(),
    RatingAdapter.ItemClickListener,
    MedicinePharmacyDetailsAdapter.ItemClickListener {

    private lateinit var binding: ActivityPharmacyDetailsBinding
    private lateinit var pharmacy: Pharmacy

    private lateinit var ratingAdapter: RatingAdapter
    private var ratingItems: List<Rating> = emptyList()

    private lateinit var medicinePharmacyDetailsAdapter: MedicinePharmacyDetailsAdapter
    private var medicine_items: ArrayList<Medicine> = ArrayList()

    private var token: String=" "
    private var userId: Int=-1
    private var query: String=""

    private var confirmationDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPharmacyDetailsBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        val sharedPref = getSharedPreferences("MyAppPrefs", AppCompatActivity.MODE_PRIVATE)
        token = sharedPref.getString("ACCESS_TOKEN", "") ?: ""
        userId = sharedPref.getInt("USER_ID", -1)

        pharmacy = intent.getParcelableExtra("pharmacy") ?: run {
            Toast.makeText(this, "فشل في تحميل بيانات الصيدلية", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupUI()
        showPharmacyData()
        showRatingComments()
        showMedicine()
        setupSearchListener()
    }

    private fun setupUI() {
        binding.header.titleText.text = "تفاصيل الصيدلية"

        binding.header.backButton.setOnClickListener {
            finish()
        }

        updateFavoriteIcon(pharmacy.is_favorite)

        binding.favoriteImg.setOnClickListener {
            val userId = getSharedPreferences("MyAppPrefs", MODE_PRIVATE).getInt("USER_ID", -1)
            if (userId == -1) {
                Toast.makeText(this, "يرجى تسجيل الدخول أولاً", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            addPharmacyToFavorite(pharmacy.id, userId)
        }


        binding.searchImage.setOnClickListener {
            query = binding.edtSearch.text.toString().trim()
            if (query.isNotEmpty()) {
                searchMedicine(query)
            } else {
                Toast.makeText(this, "الرجاء كتابة اسم الدواء", Toast.LENGTH_SHORT).show()
            }
        }

        binding.contactWhatsApp.setOnClickListener {
            val phone = pharmacy.phone_number_pharmacy.replace("+", "").replace(" ", "")
            val message = "مرحبا، أود الاستفسار من فضلك."
            val url = "https://wa.me/$phone?text=${Uri.encode(message)}"
            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, " ${e.message}واتساب غير مثبت على الجهاز", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnRate.setOnClickListener {
            val intent = Intent(this, AppEvaluationActivity::class.java)
            intent.putExtra("PHARMACY_ID", pharmacy.id)
            startActivity(intent)
        }
    }

    private fun updateFavoriteIcon(isFavorite: Boolean) {
        if (isFavorite) {
            binding.favoriteImg.setImageResource(R.drawable.red_favorite)
        } else {
            binding.favoriteImg.setImageResource(R.drawable.favorite)
        }
    }

    private fun showPharmacyData() {
        Glide.with(this)
            .load(pharmacy.image_pharmacy)
            .placeholder(R.drawable.new_background)
            .into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    binding.imgPharmacyLayout.background = resource
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    binding.imgPharmacyLayout.background = placeholder
                }
            })

        binding.txtPharmacyName.text = pharmacy.name_pharmacy
        binding.txtAddress.text = pharmacy.address.formatted_address
        binding.txtPharmacyDescription.text = pharmacy.description
        binding.txtRatings.text = pharmacy.rating?.size.toString()
    }

    private fun showRatingComments() {
        val allRatings = pharmacy.rating ?: emptyList()
        val ratingsWithComments = allRatings.filter { !it.comment.isNullOrBlank() }
        val firstTwoWithComments = ratingsWithComments.take(2)

        if (firstTwoWithComments.isEmpty()) {
            Toast.makeText(this, "لا توجد تعليقات لعرضها", Toast.LENGTH_SHORT).show()
        }

        ratingItems = firstTwoWithComments
        ratingAdapter = RatingAdapter(this, ratingItems, this)
        binding.rvRate.adapter = ratingAdapter
    }

    private fun showMedicine() {
        medicine_items.clear()
        medicinePharmacyDetailsAdapter = MedicinePharmacyDetailsAdapter(this, medicine_items, this)
        binding.rvMedicine.adapter = medicinePharmacyDetailsAdapter
    }

    private fun setupSearchListener() {
        binding.edtSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE ||
                actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {

                query = binding.edtSearch.text.toString().trim()
                if (query.isNotEmpty()) {
                    searchMedicine(query)
                } else {
                    Toast.makeText(this, "الرجاء كتابة اسم الدواء", Toast.LENGTH_SHORT).show()
                }
                true
            } else {
                false
            }
        }

    }

    private fun searchMedicine(query: String) {

        ApiClient.apiService.searchTreatmentOfStock(token, pharmacy.id.toString(), query)
            .enqueue(object : Callback<MedicineResponse> {
                override fun onResponse(call: Call<MedicineResponse>, response: Response<MedicineResponse>) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        val result = response.body()?.data ?: emptyList()
                        val medicineList = result.map {
                            Medicine(
                                id = it.id,
                                name = it.name,
                                image = it.image,
                                description = it.description,
                                about_the_medicine = it.about_the_medicine,
                                how_to_use = it.how_to_use,
                                instructions = it.instructions,
                                side_effects = it.side_effects,
                                is_favorite = it.is_favorite,
                                pharmacy_stock = it.pharmacy_stock
                            )

                        }
                        medicinePharmacyDetailsAdapter.updateList(medicineList)

                        if (medicineList.isEmpty()){
                            showConfirmationDialog()
                        }

                    } else {
                        Toast.makeText(this@PharmacyDetailsActivity, "لم يتم العثور على نتائج", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<MedicineResponse>, t: Throwable) {
                    Toast.makeText(this@PharmacyDetailsActivity, "فشل الاتصال بالسيرفر", Toast.LENGTH_SHORT).show()
                }
            })
    }



    fun showConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        //builder.setTitle("تأكيد")
        builder.setMessage("هل ترغب في ان ارسل لك اشعار في حال توفر هذا العلاج؟")

        builder.setPositiveButton("نعم") { dialog, which ->
            storeRequestTreatmentAvailbilte(token,userId,query,pharmacy.id)
            Toast.makeText(this, "تم الاختيار: نعم", Toast.LENGTH_SHORT).show()
        }

        builder.setNegativeButton("لا") { dialog, which ->
            Toast.makeText(this, "تم الاختيار: لا", Toast.LENGTH_SHORT).show()
        }

        builder.setNeutralButton("إلغاء") { dialog, which ->
            dialog.dismiss()
        }

        confirmationDialog = builder.create()
        confirmationDialog?.show()
    }



    fun showErrorDialog(message: String) {
        confirmationDialog?.dismiss()
        confirmationDialog = null

        val builder = AlertDialog.Builder(this)
        builder.setMessage(message)

        builder.setNeutralButton("إلغاء") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }




    private fun storeRequestTreatmentAvailbilte(token: String,userId: Int,treatment_name: String,pharmacy_id: Int){

        val request = storeTreatmentAvailbilteRequest(userId, treatment_name,pharmacy_id)
        ApiClient.apiService.storeRequestTreatmentAvailbilte( token, request)
            .enqueue(object : Callback<GeneralResponse> {
                override fun onResponse(
                    call: Call<GeneralResponse>,
                    response: Response<GeneralResponse>
                ) {
                    val errorBody = response.errorBody()?.string()

                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(this@PharmacyDetailsActivity, "تم إرسال الطلب بنجاح", Toast.LENGTH_SHORT).show()
                    } else {
                        // اغلق نافذة التأكيد
                        confirmationDialog?.dismiss()

                        val errorMessage = try {
                            val json = org.json.JSONObject(errorBody ?: "")
                            json.optJSONObject("data")?.optString("error") ?: "فشل غير معروف"
                        } catch (e: Exception) {
                            "فشل غير معروف"
                        }

                        showErrorDialog(errorMessage)
                        Log.e("FavoriteError", "Response code: ${response.code()}, Error body: $errorBody")
                    }
                }

                override fun onFailure(call: Call<GeneralResponse>, t: Throwable) {
                    Toast.makeText(this@PharmacyDetailsActivity, "خطأ: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }



    private fun addPharmacyToFavorite(pharmacyId: Int, userId: Int) {
        val token = getSharedPreferences("MyAppPrefs", MODE_PRIVATE).getString("ACCESS_TOKEN", "") ?: ""

        if (token.isEmpty()) {
            Toast.makeText(this, "رمز الدخول مفقود", Toast.LENGTH_SHORT).show()
            return
        }

        val request = FavoritePharmacyRequest(userId, pharmacyId)

        ApiClient.apiService.storeFavorite(token, request)
            .enqueue(object : Callback<FavoritePharmacyResponse> {
                override fun onResponse(
                    call: Call<FavoritePharmacyResponse>,
                    response: Response<FavoritePharmacyResponse>
                ) {
                    val errorBody = response.errorBody()?.string()

                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(this@PharmacyDetailsActivity, "تمت الإضافة إلى المفضلة", Toast.LENGTH_SHORT).show()

                        pharmacy.is_favorite = true
                        updateFavoriteIcon(true)
                    } else {
                        val errorMessage = try {
                            val json = org.json.JSONObject(errorBody ?: "")
                            json.optJSONObject("data")?.optString("error") ?: "فشل في الإضافة للمفضلة"
                        } catch (e: Exception) {
                            "فشل في الإضافة للمفضلة"
                        }

                        Log.e("FavoriteError", "Response code: ${response.code()}, Error body: $errorBody")
                        Toast.makeText(this@PharmacyDetailsActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<FavoritePharmacyResponse>, t: Throwable) {
                    Toast.makeText(this@PharmacyDetailsActivity, "خطأ: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }


    override fun onItemClick(position: Int, id: String) {
        // لم يُستخدم بعد
    }

    override fun onItemClickMedicine(position: Int, id: String) {
        val intent = Intent(this, MedicineDetailsActivity::class.java)
        intent.putExtra("pharmacy_name", pharmacy.name_pharmacy)
        startActivity(intent)
    }
}
