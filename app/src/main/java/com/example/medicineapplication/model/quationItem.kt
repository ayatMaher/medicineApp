package com.example.medicineapplication.model

data class quationItem (
    val question: String,
    val answer: String,
    var isExpanded: Boolean = false
)