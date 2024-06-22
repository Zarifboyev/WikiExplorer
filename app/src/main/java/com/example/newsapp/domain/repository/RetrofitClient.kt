package com.example.newsapp.domain.repository

import com.example.newsapp.domain.service.YouTubeServiceReady
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "https://www.googleapis.com/"

    fun create(): YouTubeServiceReady {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(YouTubeServiceReady::class.java)
    }
}
