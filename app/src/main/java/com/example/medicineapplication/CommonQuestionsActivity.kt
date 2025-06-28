package com.example.medicineapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medicineapplication.adapter.QuationsAdapter
import com.example.medicineapplication.databinding.ActivityCommonQuestionsPageBinding
import com.example.medicineapplication.model.quationItem

@Suppress("DEPRECATION")
class CommonQuestionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCommonQuestionsPageBinding
    private lateinit var faqAdapter: QuationsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityCommonQuestionsPageBinding.inflate(layoutInflater)
        setContentView(binding.root)


        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true


        binding.btnBack.setOnClickListener {
            finish()
        }


        val faqList = listOf(
            quationItem(
                "كيف يمكنني معرفة العلاج المناسب لحالتي؟",
                "بعد تعبئة بياناتك الصحية، سيقترح التطبيق العلاجات المناسبة بناءً على حالتك."
            ),
            quationItem(
                "هل التطبيق مجاني؟",
                "نعم، التطبيق مجاني للاستخدام الأساسي."
            ),
            quationItem(
                "هل يمكنني التواصل مع طبيب؟",
                "نعم، التطبيق يوفر خاصية التواصل مع مختصين."
            )
        )


        faqAdapter = QuationsAdapter(faqList)
        binding.faqRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.faqRecyclerView.adapter = faqAdapter
    }
}