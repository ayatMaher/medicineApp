package com.example.medicineapplication

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.example.medicineapplication.R

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM_TOKEN", "FCM Token: $token")
        // أرسل التوكن إلى السيرفر إذا لزم الأمر
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        var title: String? = null
        var body: String? = null

        // إشعار من نوع Notification
        remoteMessage.notification?.let {
            title = it.title
            body = it.body
        }

        // إشعار من نوع Data
        if (remoteMessage.data.isNotEmpty()) {
            Log.d("FCM_DATA", "Data Payload: ${remoteMessage.data}")
            title = remoteMessage.data["title"] ?: title
            body = remoteMessage.data["body"] ?: body
        }

        Log.d("FCM_MESSAGE", "Title: $title")
        Log.d("FCM_MESSAGE", "Body: $body")

        // عرض الإشعار
        if (!title.isNullOrEmpty() && !body.isNullOrEmpty()) {
            showNotification(title!!, body!!)
        }
    }

    private fun showNotification(title: String, message: String) {
        val channelId = "medicine_channel"

        // إنشاء قناة الإشعارات
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "إشعارات التطبيق",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.medicine_img) // أيقونة مناسبة
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(1, builder.build())
    }
}
