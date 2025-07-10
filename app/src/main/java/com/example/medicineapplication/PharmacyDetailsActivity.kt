package com.example.medicineapplication

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.medicineapplication.adapter.MedicinePharmacyDetailsAdapter
import com.example.medicineapplication.adapter.RatingAdapter
import com.example.medicineapplication.databinding.ActivityPharmacyDetailsBinding
import com.example.medicineapplication.model.Medicine
import com.example.medicineapplication.model.Pharmacy
import com.example.medicineapplication.model.User
import androidx.core.net.toUri
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

@Suppress("DEPRECATION")
class PharmacyDetailsActivity : AppCompatActivity(),
    RatingAdapter.ItemClickListener,
    MedicinePharmacyDetailsAdapter.ItemClickListener {

    private lateinit var binding: ActivityPharmacyDetailsBinding
    private lateinit var pharmacy: Pharmacy

    private lateinit var ratingAdapter: RatingAdapter
    private var items: ArrayList<User> = ArrayList()

    private lateinit var medicinePharmacyDetailsAdapter: MedicinePharmacyDetailsAdapter
    private var medicine_items: ArrayList<Medicine> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPharmacyDetailsBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        // ✅ استقبال الكائن بأمان
        pharmacy = intent.getParcelableExtra("pharmacy") ?: run {
            Toast.makeText(this, "فشل في تحميل بيانات الصيدلية", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupUI()
        showPharmacyData()
        showRatingComments()
        showMedicine()
    }

    private fun setupUI() {
        binding.header.titleText.text = "تفاصيل الصيدلية"

        binding.header.backButton.setOnClickListener {
            finish()
        }

        // ✅ عرض الأيقونة حسب حالة is_favorite
        updateFavoriteIcon(pharmacy.is_favorite)

        binding.favoriteImg.setOnClickListener {
            // هنا مجرد قلب الشكل محليًا (إن كنت تريد حفظها لاحقًا في السيرفر أضف كود API هنا)
            pharmacy.is_favorite = !pharmacy.is_favorite
            updateFavoriteIcon(pharmacy.is_favorite)
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


        Log.e("PharmacyData", pharmacy.name_pharmacy + " - " +pharmacy.description+ " - " +pharmacy.address.formatted_address)
    }

    private fun showRatingComments() {
        items.clear()
        ratingAdapter = RatingAdapter(this, items, this)
        binding.rvRate.adapter = ratingAdapter
    }

    private fun showMedicine() {
        medicine_items.clear()
        medicinePharmacyDetailsAdapter =
            MedicinePharmacyDetailsAdapter(this, medicine_items, this)
        binding.rvMedicine.adapter = medicinePharmacyDetailsAdapter
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
