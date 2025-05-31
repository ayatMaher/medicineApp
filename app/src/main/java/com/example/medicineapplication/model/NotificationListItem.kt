package com.example.medicineapplication.model

sealed class NotificationListItem {
    data class SectionHeader(val title: String) : NotificationListItem()
    data class NotificationItem(val message: String, val time: String, val imageResId: Int) : NotificationListItem()
}