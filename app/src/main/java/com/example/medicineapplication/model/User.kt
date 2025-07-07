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



data class StoreLocationResponse(
    val success: Boolean,
    val message: String,
    val status: String,
    val data: LocationData
)


data class LocationData(
    val id: Int,
    val user: LocationUser,
    val latitude: Double,
    val longitude: Double,
    val formatted_address: String,
    val country: String,
    val region: String,
    val city: String,
    val district: String,
    val postal_code: String,
    val location_type: String
)


data class LocationUser(
    val id: Int,
    val image: String?,  // nullable
    val name: String,
    val phone: String,
    val email: String,
    val location: LocationDetails
)


data class LocationDetails(
    val id: Int,
    val latitude: String, // String في الرد
    val longitude: String,
    val formatted_address: String,
    val country: String,
    val region: String,
    val city: String,
    val district: String,
    val postal_code: String,
    val location_type: String
)




data class StoreLocationRequest(
    val user_id: Int,
    val latitude: Double,
    val longitude: Double,
    val formatted_address: String,
    val country: String,
    val region: String,
    val city: String,
    val district: String,
    val postal_code: String,
    val location_type: String
)

