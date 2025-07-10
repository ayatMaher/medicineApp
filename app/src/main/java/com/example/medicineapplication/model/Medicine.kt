package com.example.medicineapplication.model
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MedicinesWithCategoryResponse(
    val success: Boolean,
    val message: String,
    val data: List<Treatment>,
    val status: String
): Parcelable


@Parcelize
data class Treatment(
    val id: Int,
    val image: String,
    val name: String,
    val description: String,
    val category: Category,
    val about_the_medicine: String,
    val how_to_use: String,
    val instructions: String,
    val side_effects: String,
    var isFeatured: Boolean?,
    var is_favorite: Boolean?,  // يمكن أن يكون null
    val pharmacy_count_available: Int?  // يمكن أن يكون null
) : Parcelable



@Parcelize
data class Category(
    val id: Int,
    val image: String,
    val name: String,
    val description: String,
    var isFeatured: Boolean?
): Parcelable


data class FavoriteMedicineRequest(
    val user_id: Int,
    val treatment_id: Int
)

data class FavoriteMedicineResponse(
    val success: Boolean,
    val message: String,
    val data: List<Any>,
    val status: String
)



@Parcelize
data class FavoriteTreatmentResponse(
    val success: Boolean,
    val message: String,
    val data: List<FavoriteTreatmentItem>,
    val status: String
) : Parcelable

@Parcelize
data class FavoriteTreatmentItem(
    val id: Int,
    val treatment: Treatment
) : Parcelable


data class TreatmentsSearchResponse(
    val success: Boolean,
    val message: String,
    val data: List<Treatment>,
    val status: String
)


data class ViewCategoriesResponse(
    val success: Boolean,
    val message: String,
    val data: List<Category>,
    val status: String
)


data class storeTreatmentAvailbilteRequest(
    val user_id: Int,
    val treatment_name: String,
    val pharmacy_id: Int
)

data class GeneralResponse(
    val success: Boolean,
    val message: String,
    val data: List<Any>,
    val status: String
)