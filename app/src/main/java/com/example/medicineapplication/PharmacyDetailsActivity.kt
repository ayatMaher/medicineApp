package com.example.medicineapplication

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.medicineapplication.adapter.MedicinePharmacyDetailsAdapter
import com.example.medicineapplication.adapter.RatingAdapter
import com.example.medicineapplication.databinding.ActivityPharmacyDetailsBinding
import com.example.medicineapplication.model.Medicine
import com.example.medicineapplication.model.User
import androidx.core.net.toUri

class PharmacyDetailsActivity : AppCompatActivity(), RatingAdapter.ItemClickListener,
    MedicinePharmacyDetailsAdapter.ItemClickListener {
    lateinit var binding: ActivityPharmacyDetailsBinding

    // rating comments user
    lateinit var ratingAdapter: RatingAdapter
    var items: ArrayList<User> = ArrayList()

    // medicine
    lateinit var medicinePharmacyDetailsAdapter: MedicinePharmacyDetailsAdapter
    var medicine_items: ArrayList<Medicine> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPharmacyDetailsBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        // title text
        binding.header.titleText.text = "تفاصيل الصيدلية"
        // back arrow
        binding.header.backButton.setOnClickListener {
            finish()
        }
        //favorite icon
        binding.favoriteImg.setOnClickListener {
            binding.favoriteImg.setImageResource(R.drawable.red_favorite)
        }
        // contactWhatsApp
        binding.contactWhatsApp.setOnClickListener {
            val phoneNumber = "972592754492" // رقم الهاتف بصيغة دولية بدون "+"
            val message = "مرحبا، أود الاستفسار من فضلك."
            val url = "https://wa.me/$phoneNumber?text=${Uri.encode(message)}"

            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = url.toUri()

            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, "واتساب غير مثبت على الجهاز"+e.message, Toast.LENGTH_SHORT).show()
            }
        }

        // rate the pharmacy
        binding.btnRate.setOnClickListener {
            val intent = Intent(this, AppEvaluationActivity::class.java)
            startActivity(intent)
        }
        //rating comments
        showRatingComments()
        // medicine in pharmacy
        showMedicine()

    }

    private fun showRatingComments() {
        items.clear()
//        items.add(
//            User(
//                "1",
//                "غيداء جمال ",
//                R.drawable.user,
//                "صيدلية منظمة، توفر خدمة جيدة وأسعار مناسبة، لكن تحتاج لتحسين سرعة الاستجابة.",
//                "5 مارس 2025"
//            )
//        )
//        items.add(
//            User(
//                "1",
//                "غيداء جمال ",
//                R.drawable.user,
//                "صيدلية منظمة، توفر خدمة جيدة وأسعار مناسبة، لكن تحتاج لتحسين سرعة الاستجابة.",
//                "5 مارس 2025"
//            )
//        )
        ratingAdapter = RatingAdapter(this, items, this)
        binding.rvRate.adapter = ratingAdapter

    }

    private fun showMedicine() {
        medicine_items.clear()
        medicine_items.add(
            Medicine(
                "1",
                "باندول",
                R.drawable.medicine_details,
                80.0,
                "مسكن الألم وخافض للحرارة ",
                50.0
            )
        )
        medicine_items.add(
            Medicine(
                "1",
                "باندول",
                R.drawable.medicine_details,
                80.0,
                "مسكن الألم وخافض للحرارة ",
                50.0
            )
        )
        medicine_items.add(
            Medicine(
                "1",
                "باندول",
                R.drawable.medicine_details,
                80.0,
                "مسكن الألم وخافض للحرارة ",
                50.0
            )
        )
        medicinePharmacyDetailsAdapter = MedicinePharmacyDetailsAdapter(this, medicine_items, this)
        binding.rvMedicine.adapter = medicinePharmacyDetailsAdapter
    }

    override fun onItemClick(position: Int, id: String) {
        TODO("Not yet implemented")
    }

    override fun onItemClickMedicine(position: Int, id: String) {
        val pharmacyName = binding.txtPharmacyName.text.toString()
        val intent = Intent(this, MedicineDetailsActivity::class.java)
        intent.putExtra("pharmacy_name", pharmacyName)
        startActivity(intent)
    }
}