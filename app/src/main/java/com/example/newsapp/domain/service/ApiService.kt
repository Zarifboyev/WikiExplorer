package com.example.newsapp.domain.service

import com.example.newsapp.data.model.ApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

// ApiService.kt
interface ApiService {
    @GET("w/api.php")
    suspend fun fetchPlaces(
        @Query("action") action: String,
        @Query("prop") prop: String,
        @Query("inprop") inprop: String,
        @Query("pithumbsize") pithumbsize: Int,
        @Query("generator") generator: String,
        @Query("ggsradius") ggsradius: Int,
        @Query("ggslimit") ggslimit: Int,
        @Query("ggscoord") ggscoord: String,
        @Query("format") format: String
    ): Response<ApiResponse>
}
