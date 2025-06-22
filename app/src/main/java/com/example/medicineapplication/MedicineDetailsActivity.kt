package com.example.medicineapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.medicineapplication.databinding.ActivityMedicineDetailsBinding

@Suppress("DEPRECATION")
class MedicineDetailsActivity : AppCompatActivity() {
    lateinit var binding: ActivityMedicineDetailsBinding

    //    var item: ArrayList<Medicine> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMedicineDetailsBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

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
        showUsage()

        // favorite icon
        binding.favoriteImg.setOnClickListener {
            binding.favoriteImg.setImageResource(R.drawable.red_favorite)
        }
        // show pharmacy icon
        binding.showPharmacyPage.setOnClickListener {
//            val fragment = PharmacyFragment()
//            fragment.arguments = Bundle().apply {
//                putString("page_type", "pharmacy")
//            }
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.navigation_pharmacies, fragment)
//                .addToBackStack(null)
//                .commit()
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

    @SuppressLint("ResourceAsColor", "SetTextI18n")
    private fun showUsage() {
        binding.txtUsage.setTextColor(ContextCompat.getColor(this, R.color.primary_color))
        binding.txtLineUsage.visibility = View.VISIBLE
        binding.txtSideEffects.setTextColor(ContextCompat.getColor(this, R.color.black))
        binding.txtLineSideEffect.visibility = View.INVISIBLE
        binding.txtInstructions.setTextColor(ContextCompat.getColor(this, R.color.black))
        binding.txtLineInstructions.visibility = View.INVISIBLE
        binding.txtTabContent.text =""" 
            1.دعم المناعة وتعزيز مقاومة الجسم للأمراض
            2.تحسين صحة الشعر والبشرة والأظافر
            3.المساهمة في تقوية العظام والأسنان (حسب النوع)
            4.زيادة الطاقة والنشاط البدني والذهني
            5.تعويض نقص الفيتامينات الناتج عن سوء التغذية أو ضعف الامتصاص
            6.تحسين وظائف التمثيل الغذائي والهضم
            7.تعويض نقص الفيتامينات الناتج عن سوء التغذية أو ضعف الامتصاص 
            8.المساهمة في تقوية العظام والأسنان (حسب النوع)
        """.trimIndent()
    }

    @SuppressLint("ResourceAsColor", "SetTextI18n")
    private fun showInstruction() {
        binding.txtUsage.setTextColor(ContextCompat.getColor(this, R.color.black))
        binding.txtLineUsage.visibility = View.INVISIBLE
        binding.txtSideEffects.setTextColor(ContextCompat.getColor(this, R.color.black))
        binding.txtLineSideEffect.visibility = View.INVISIBLE
        binding.txtInstructions.setTextColor(ContextCompat.getColor(this, R.color.primary_color))
        binding.txtLineInstructions.visibility = View.VISIBLE
        binding.txtTabContent.text = """ 
            1.دعم المناعة وتعزيز مقاومة الجسم للأمراض
            2.تحسين صحة الشعر والبشرة والأظافر
            3.المساهمة في تقوية العظام والأسنان (حسب النوع)
            4..زيادة الطاقة والنشاط البدني والذهني
            5.تعويض نقص الفيتامينات الناتج عن سوء التغذية أو ضعف الامتصاص
        """.trimIndent()

    }

    @SuppressLint("ResourceAsColor", "SetTextI18n")
    private fun showSideEffects() {
        binding.txtUsage.setTextColor(ContextCompat.getColor(this, R.color.black))
        binding.txtLineUsage.visibility = View.INVISIBLE
        binding.txtSideEffects.setTextColor(ContextCompat.getColor(this, R.color.primary_color))
        binding.txtLineSideEffect.visibility = View.VISIBLE
        binding.txtInstructions.setTextColor(ContextCompat.getColor(this, R.color.black))
        binding.txtLineInstructions.visibility = View.INVISIBLE
        binding.txtTabContent.text = """ 
            1.دعم المناعة وتعزيز مقاومة الجسم للأمراض
            2.تحسين صحة الشعر والبشرة والأظافر
            3.المساهمة في تقوية العظام والأسنان (حسب النوع)
            4..زيادة الطاقة والنشاط البدني والذهني
        """.trimIndent()
    }
}