package com.example.medicineapplication.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class NotificationResponse(
    val success: Boolean,
    val message: String,
    val data: List<NotificationItem>,
    val status: String
)


@Serializable
data class NotificationItem(
    val id: Int,
    val title: String,
    val body: String,
    val date: String // يمكن تغييره إلى LocalDateTime إذا كنت تنوي استخدام تنسيق التاريخ
)

