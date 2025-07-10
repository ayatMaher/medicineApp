package com.example.medicineapplication.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


//object mein Singleton
object ApiClient {
    private const val BASE_URL = "https://demoapplication.jawebhom.com"

    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiService by lazy {
        instance.create(ApiService::class.java)
    }

    val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS) // وقت الاتصال بالخادم
        .readTimeout(30, TimeUnit.SECONDS)    // وقت الانتظار لقراءة البيانات
        .writeTimeout(30, TimeUnit.SECONDS)   // وقت إرسال البيانات
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl("https://demoapplication.jawebhom.com")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

}