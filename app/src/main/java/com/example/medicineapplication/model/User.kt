package com.example.medicineapplication.model

import java.sql.Date

data class User(
    val id: String,
    val userName: String,
    val userImage: Int,
    val userComment: String,
//    val rating: Double,
    val rateDate: String
)
