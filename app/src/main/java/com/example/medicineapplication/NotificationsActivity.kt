package com.example.medicineapplication

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medicineapplication.adapter.NotificationAdapter
import com.example.medicineapplication.api.ApiClient
import com.example.medicineapplication.databinding.ActivityAppEvaluationBinding
import com.example.medicineapplication.databinding.ActivityNotificationsPageBinding
import com.example.medicineapplication.model.NotificationItem
import com.example.medicineapplication.model.NotificationListItem
import com.example.medicineapplication.model.NotificationResponse
import com.example.medicineapplication.model.Treatment
import com.example.medicineapplication.model.TreatmentsSearchResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationsActivity : AppCompatActivity() {
    private var token: String = " "
    private lateinit var binding: ActivityNotificationsPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationsPageBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)


        val sharedPref =
            getSharedPreferences("MyAppPrefs", AppCompatActivity.MODE_PRIVATE)
        token = sharedPref.getString("ACCESS_TOKEN", "") ?: ""

        val titleText = findViewById<TextView>(R.id.titleText)
        titleText.text = "الإشعارات"


        binding.recyclerNotifications.layoutManager = LinearLayoutManager(this)
        fetchNotification(token)


        binding.header.backButton.setOnClickListener {
            finish()
        }






//        val recyclerView = findViewById<RecyclerView>(R.id.recycler_notifications)
//        recyclerView.layoutManager = LinearLayoutManager(this)
//
//        val items = listOf(
//            NotificationListItem.SectionHeader("اليوم"),
//            NotificationListItem.NotificationItem("تم وصول دوائك بانادول", "10:00 اليوم", R.drawable.panadol),
//            NotificationListItem.NotificationItem("تم وصول دوائك بانادول", "10:00 اليوم", R.drawable.panadol),
//
//            NotificationListItem.SectionHeader("الأحد"),
//            NotificationListItem.NotificationItem("تم وصول دوائك بانادول", "10:00 الأحد", R.drawable.panadol),
//            NotificationListItem.NotificationItem("تم وصول دوائك بانادول", "10:00 الأحد", R.drawable.panadol)
//        )
//
//        recyclerView.adapter = NotificationAdapter(items)
    }


    private fun fetchNotification(token: String) {
        ApiClient.apiService.notificationorForCurrentUser(token)
            .enqueue(object : Callback<NotificationResponse> {
                override fun onResponse(
                    call: Call<NotificationResponse>,
                    response: Response<NotificationResponse>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        val notifications: List<NotificationItem> = response.body()?.data ?: emptyList()
                        val adapter = NotificationAdapter(notifications)
                        binding.recyclerNotifications.adapter = adapter

                        Toast.makeText(this@NotificationsActivity, "تم العثور على إشعارات", Toast.LENGTH_SHORT).show()
                    } else {

                        Toast.makeText(this@NotificationsActivity, "لم يتم العثور على إشعارات", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<NotificationResponse>, t: Throwable) {
                    Toast.makeText(this@NotificationsActivity, "فشل الاتصال: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }
}