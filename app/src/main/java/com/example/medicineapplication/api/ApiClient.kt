package com.example.medicineapplication.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


//object mein Singleton
object ApiClient {
    private const val BASE_URL = "https://demoapplication.jawebhom.com"
//    private const val BASE_URL = "http://127.0.0.1:8000"

    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}