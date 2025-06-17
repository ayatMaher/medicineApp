package com.example.medicineapplication.model

data class Pharmacy(
    val id: String,
    val pharmacyImage: Int,
    val pharmacyName: String,
    val rate: Double,
    val pharmacyAddress: String,
    val distance : Double=0.0,
    val time: String="",
    val isFeatured:Boolean= true
)
