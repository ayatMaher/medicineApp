package com.example.medicineapplication

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.medicineapplication.adapter.MedicinePharmacyDetailsAdapter
import com.example.medicineapplication.adapter.RatingAdapter
import com.example.medicineapplication.databinding.ActivityMedicineDetailsBinding
import com.example.medicineapplication.databinding.ActivityPharmacyDetailsBinding
import com.example.medicineapplication.model.Medicine
import com.example.medicineapplication.model.User
import java.time.LocalDate

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
        TODO("Not yet implemented")
    }
}