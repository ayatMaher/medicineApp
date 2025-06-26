package com.example.medicineapplication

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medicineapplication.adapter.NotificationAdapter
import com.example.medicineapplication.model.NotificationListItem

class NotificationsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_notifications_page)

        val titleText = findViewById<TextView>(R.id.titleText)
        titleText.text = "الإشعارات"


        val recyclerView = findViewById<RecyclerView>(R.id.recycler_notifications)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val items = listOf(
            NotificationListItem.SectionHeader("اليوم"),
            NotificationListItem.NotificationItem("تم وصول دوائك بانادول", "10:00 اليوم", R.drawable.panadol),
            NotificationListItem.NotificationItem("تم وصول دوائك بانادول", "10:00 اليوم", R.drawable.panadol),

            NotificationListItem.SectionHeader("الأحد"),
            NotificationListItem.NotificationItem("تم وصول دوائك بانادول", "10:00 الأحد", R.drawable.panadol),
            NotificationListItem.NotificationItem("تم وصول دوائك بانادول", "10:00 الأحد", R.drawable.panadol)
        )

        recyclerView.adapter = NotificationAdapter(items)
    }
}