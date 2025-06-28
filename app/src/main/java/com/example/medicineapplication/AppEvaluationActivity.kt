package com.example.medicineapplication

import android.R
import android.content.Intent
import android.os.Bundle
import android.widget.RatingBar
import android.widget.RatingBar.OnRatingBarChangeListener
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.medicineapplication.databinding.ActivityAppEvaluationBinding
import com.example.medicineapplication.fragment.ProfileFragment


class AppEvaluationActivity : AppCompatActivity() {
    lateinit var binding: ActivityAppEvaluationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppEvaluationBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)


        window.statusBarColor = ContextCompat.getColor(this, R.color.transparent)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        binding.header.titleText.text = ""
        // back arrow
        binding.header.backButton.setOnClickListener {
            val intent = Intent(this, ProfileFragment::class.java)
            startActivity(intent)
            finish()
        }

        val ratingBar = binding.ratingBar
        ratingBar.setOnRatingBarChangeListener(object : OnRatingBarChangeListener {
            override fun onRatingChanged(ratingBar: RatingBar?, rating: Float, fromUser: Boolean) {
                Toast.makeText(getApplicationContext(), "Rating: " + rating, Toast.LENGTH_SHORT)
                    .show()
            }
        })

    }
}