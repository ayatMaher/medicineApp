package com.example.medicineapplication.model

data class Medicine(
    val id: String,
    val medicineName: String,
    val medicineImage: Int,
    val price: Double = 0.0,
    val description: String ="",
    val priceAfterDiscount: Double = 0.0,
    val isFeatured: Boolean = true
)
