package com.example.medicineapplication.api

import com.example.medicineapplication.model.GenericResponse
import com.example.medicineapplication.model.LoginResponse
import com.example.medicineapplication.model.RegisterResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {

    @FormUrlEncoded
    @POST("api/auth/login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("api/auth/register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("phone") phone: String,
        @Field("password") password: String,
//        @Field("password_confirmation") passwordConfirmation: String
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("api/auth/forgot-password")
    fun forgotPassword(
        @Field("verification_method") method: String = "email",
        @Field("email") email: String
    ): Call<GenericResponse>

    @FormUrlEncoded
    @POST("api/auth/verifyToken")
    fun verifyToken(
        @Field("verification_method") method: String = "email",
        @Field("email") email: String,
        @Field("token") token: String
    ): Call<GenericResponse>

    @FormUrlEncoded
    @POST("api/auth/reset-password")
    fun resetPassword(
        @Field("verification_method") method: String = "email", // ← هذا هو الجديد
        @Field("email") email: String,
        @Field("token") token: String,
        @Field("password") password: String,
        @Field("password_confirmation") passwordConfirmation: String
    ): Call<GenericResponse>
    @POST("api/auth/logout")
    fun logout(@Header("Authorization") token: String): Call<GenericResponse>
}