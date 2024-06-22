package com.example.newsapp.di

import com.example.newsapp.domain.service.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// RetrofitInstance.kt
object RetrofitInstance {
    private const val BASE_URL = "https://uz.wikipedia.org/"

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
