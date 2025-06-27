package com.example.medicineapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medicineapplication.adapter.QuationsAdapter
import com.example.medicineapplication.databinding.ActivityCommonQuestionsPageBinding
import com.example.medicineapplication.databinding.ActivityEditProfileBinding
import com.example.medicineapplication.fragment.ProfileFragment
import com.example.medicineapplication.model.quationItem

class CommonQuationsPage : AppCompatActivity() {
    private lateinit var binding: ActivityCommonQuestionsPageBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var faqAdapter: QuationsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityCommonQuestionsPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setContentView(R.layout.activity_common_questions_page)

        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        recyclerView = binding.faqRecyclerView

        binding.btnBack.setOnClickListener {
            val intent = Intent(this, ProfileFragment::class.java)
            startActivity(intent)
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
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = faqAdapter





    }
}