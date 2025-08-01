package com.example.medicineapplication.api

import com.example.medicineapplication.model.DeleteResponse
import com.example.medicineapplication.model.FavoriteMedicineRequest
import com.example.medicineapplication.model.FavoriteMedicineResponse
import com.example.medicineapplication.model.FavoritePharmacyListResponse
import com.example.medicineapplication.model.FavoritePharmacyRequest
import com.example.medicineapplication.model.FavoritePharmacyResponse
import com.example.medicineapplication.model.FavoriteTreatmentResponse
import com.example.medicineapplication.model.GeneralResponse
import com.example.medicineapplication.model.GenericResponse
import com.example.medicineapplication.model.LoginResponse
import com.example.medicineapplication.model.MedicineResponse
import com.example.medicineapplication.model.MedicinesWithCategoryResponse
import com.example.medicineapplication.model.NotificationResponse
import com.example.medicineapplication.model.PharmacyResponse
import com.example.medicineapplication.model.RegisterResponse
import com.example.medicineapplication.model.StoreAppRatingResponse
import com.example.medicineapplication.model.StoreLocationRequest
import com.example.medicineapplication.model.StoreLocationResponse
import com.example.medicineapplication.model.StoreRatingAppRequest
import com.example.medicineapplication.model.StoreRatingRequest
import com.example.medicineapplication.model.StoreRatingResponse
import com.example.medicineapplication.model.TreatmentsSearchResponse
import com.example.medicineapplication.model.UserResponse
import com.example.medicineapplication.model.ViewCategoriesResponse
import com.example.medicineapplication.model.storeTreatmentAvailbilteRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiService {

    @FormUrlEncoded
    @POST("api/auth/login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @Multipart
    @POST("api/auth/social-callback")
    fun loginWithGoogle(
        @Part("provider") provider: RequestBody,
        @Part("id_token") idToken: RequestBody
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

    @GET("api/user/current-user")
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

    @POST("api/user/update-location")
    fun updateLocation(
        @Header("Authorization") token: String,
        @Body location: StoreLocationRequest
    ): Call<GeneralResponse>

    @GET("api/user/delete-account-user/{id}")
    fun deleteAccount(
        @Header("Authorization") token: String,
        @Path("id") userId: Int
    ): Call<DeleteResponse>

    @DELETE("api/user/favorite-delete/{favorite_id}")
    fun removeMedicineFavorite(
        @Header("Authorization") token: String,
        @Path("favorite_id") favoriteId: Int
    ): Call<GenericResponse>

    @DELETE("api/user/favorite-delete/{favorite_id}")
    fun removePharmacyFavorite(
        @Header("Authorization") token: String,
        @Path("favorite_id") favoriteId: Int
    ): Call<GenericResponse>

    @POST("api/user/store-rating")
    fun ratingApp(
        @Header("Authorization") token: String,
        @Body request: StoreRatingAppRequest
    ): Call<StoreAppRatingResponse>

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

    @POST("api/pharmacies/store-rating")
    fun ratingPharmacy(
        @Header("Authorization") token: String,
        @Body request: StoreRatingRequest
    ): Call<StoreRatingResponse>

    @GET("api/pharmacies/search-treatment")
    fun searchTreatmentOfStock(
        @Header("Authorization") token: String,
        @Query("pharmacy_id") pharmacyId: String,
        @Query("treatment_search") treatmentName: String
    ): Call<MedicineResponse>


    @GET("api/treatments/most-searched-treatment")
    fun getTopTreatment(
        @Header("Authorization") token: String
    ): Call<MedicinesWithCategoryResponse>


    @POST("api/treatments/store-favorite")
    fun storFavoriteMedicine(
        @Header("Authorization") token: String,
        @Body request: FavoriteMedicineRequest
    ): Call<FavoriteMedicineResponse>


    @GET("api/treatments/favorite")
    fun getFavoriteMedicines(
        @Header("Authorization") token: String
    ): Call<FavoriteTreatmentResponse>


    @GET("api/treatments/search")
    fun treatmentsSearch(
        @Header("Authorization") token: String,
        @Query("category_id") categoryId: String,
        @Query("treatment_search") treatmentName: String
    ): Call<TreatmentsSearchResponse>


    @GET("api/categories")
    fun viewCategories(
        @Header("Authorization") token: String
    ): Call<ViewCategoriesResponse>


    @POST("api/treatments/store-search-treatment")
    fun storeSearchTreatment(
        @Header("Authorization") token: String,
        @Query("user_id") userId: String,
        @Query("treatment_id") treatmentId: String
    ): Call<GeneralResponse>


    @POST("api/treatments/store-request-treatment-available")
    fun storeRequestTreatmentAvailbilte(
        @Header("Authorization") token: String,
        @Body request: storeTreatmentAvailbilteRequest
    ): Call<GeneralResponse>


    @POST("api/user/update-device-token")
    fun updateDeviceToken(
        @Header("Authorization") token: String,
        @Body body: Map<String, String>
    ): Call<GeneralResponse>


    @GET("api/notifications/current-user")
    fun notificationorForCurrentUser(
        @Header("Authorization") token: String,
    ): Call<NotificationResponse>

}