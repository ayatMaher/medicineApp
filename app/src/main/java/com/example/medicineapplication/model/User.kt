package com.example.medicineapplication.model

import com.google.gson.annotations.SerializedName


data class LoginResponse(
    val success: Boolean,
    val message: String,
    val data: Data?
)

data class RegisterResponse(
    val success: Boolean,
    val message: String,
    val data: Data?,
    val status: String
)


data class Data(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String,
    val user: User
)

data class GenericResponse(
    val success: Boolean,
    val message: String
)

data class UserResponse(
    val success: Boolean,
    val message: String,
    val data: User,
    val status: String
)

data class User(
    val id: Int,
    val image: String?,
    val name: String,
    val phone: String,
    val email: String,
    val location: String?,
    val rateDate: String = "",
    val userComment: String = "",
)
