package com.example.medicineapplication.api

import android.R
import com.example.medicineapplication.model.FavoritePharmacyListResponse
import com.example.medicineapplication.model.FavoritePharmacyRequest
import com.example.medicineapplication.model.FavoritePharmacyResponse
import com.example.medicineapplication.model.GenericResponse
import com.example.medicineapplication.model.LoginResponse
import com.example.medicineapplication.model.PharmacyResponse
import com.example.medicineapplication.model.RegisterResponse
import com.example.medicineapplication.model.StoreLocationRequest
import com.example.medicineapplication.model.StoreLocationResponse
import com.example.medicineapplication.model.User
import com.example.medicineapplication.model.UserResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST

import retrofit2.http.Part

import retrofit2.http.Query


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

    @GET("api/user/current-user")  // أو المسار الصحيح حسب Postman
    fun getCurrentUser(@Header("Authorization") token: String): Call<UserResponse>


    @Multipart
    @POST("api/user/update-profile")
    fun updateUser(
        @Header("Authorization") token: String,
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part("password") password: RequestBody,
        @Part image: MultipartBody.Part?
    ): Call<UserResponse>

    @GET("api/pharmacies/pharmacies-nearest")
    fun nearbyPharmacies(
        @Header("Authorization") token: String
    ): Call<PharmacyResponse>


    @POST("api/user/store-location")
    fun storeUserLocation(
        @Header("Authorization") token: String,
        @Body requestBody: StoreLocationRequest
    ): Call<StoreLocationResponse>

    @GET("api/pharmacies/search-of-treatment-pharmacies-nearest")
    fun searchTreatmentPharmacyNearby(
        @Header("Authorization") token: String,
        @Query("treatment_search") treatmentName: String
    ): Call<PharmacyResponse>

    @POST("api/pharmacies/store-favorite")
    fun storeFavorite(
        @Header("Authorization") token: String,
        @Body request: FavoritePharmacyRequest
    ): Call<FavoritePharmacyResponse>

    @GET("api/pharmacies/favorite")
    fun getFavoritePharmacies(
        @Header("Authorization") token: String
    ): Call<FavoritePharmacyListResponse>




}