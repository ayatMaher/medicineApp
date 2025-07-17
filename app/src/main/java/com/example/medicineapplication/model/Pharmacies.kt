package com.example.medicineapplication.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class PharmacyResponse(
    val success: Boolean,
    val message: String,
    val data: List<Pharmacy>,
    val status: String
)

@Parcelize
data class Pharmacy(
    val id: Int,
    val name_pharmacy: String,
    val phone_number_pharmacy: String,
    val image_pharmacy: String,
    val description: String,
    val address: Address,
    var is_favorite: Boolean,
    val average_rating: Float,
    val rating: List<Rating>?,  // nullable لو البيانات ممكن تكون null
    val count_rating: Int?,
    val distance: String,
    var favorite_id: Int? = null,
    val isFeatured: Boolean
) : Parcelable

@Parcelize
data class Address(
    val id: Int,
    val latitude: String,
    val longitude: String,
    val formatted_address: String,
    val country: String,
    val region: String?,
    val city: String,
    val district: String,
    val postal_code: String,
    val location_type: String
) : Parcelable

@Parcelize
data class Rating(
    val id: Int,
    val user_id: Int,
    val pharmacy_id: Int,
    val rating: String,
    val comment: String?,
    val type: String,
    val deleted_at: String?,
    val created_at: String,
    val updated_at: String,
    val user: UserRating?  // هنا خليتها nullable علشان تمنع الكراش لو كانت null
) : Parcelable

@Parcelize
data class UserRating(
    val id: Int,
    val name: String,
    val email: String,
    val image: String?,  // ممكن تكون null
    val provider: String?,
    val provider_id: String?,
    val fcm_token: String?,
    val is_online: Int,
    val created_at: String,
    val updated_at: String,
    val deleted_at: String?,
    val phone: String
) : Parcelable

data class FavoritePharmacyRequest(
    val user_id: Int,
    val pharmacy_id: Int
)

data class FavoritePharmacyResponse(
    val success: Boolean,
    val message: String,
    val data: List<Any>,
    val status: String
)

data class FavoritePharmacyWrapper(
    val id: Int,
    val pharmacy: Pharmacy
)


data class FavoritePharmacyListResponse(
    val success: Boolean,
    val message: String,
    val data: List<FavoritePharmacyWrapper>
)


data class StoreRatingResponse(
    val success: Boolean,
    val message: String,
    val data: List<Any>,
    val status: String
)

data class StoreRatingRequest(
    val user_id: Int,
    val pharmacy_id: Int,
    val rating: String,
    val comment: String?,
)


data class MedicineResponse(
    val success: Boolean,
    val message: String,
    val data: List<Medicine>,
    val status: String
)

@Parcelize
data class Medicine(
    val id: Int,
    val image: String,
    val name: String,
    val description: String,
    val about_the_medicine: String,
    val how_to_use: String,
    val instructions: String,
    val side_effects: String,
    val is_favorite: Boolean,
    val pharmacy_stock: List<PharmacyStock>
) : Parcelable


@Parcelize
data class PharmacyStock(
    val id: Int,
    val price: String,
    val discount_rate: Int,
    val price_after_discount: String,
    val status: String,
    val quantity: Int,
    val expiration_date: String
) : Parcelable