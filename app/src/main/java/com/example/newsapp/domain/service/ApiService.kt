package com.example.newsapp.domain.service

import com.example.newsapp.data.model.ApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("w/api.php")
    suspend fun fetchPlaces(
        @Query("ggscoord") ggscoord: String,
        @Query("action") action: String = "query",
        @Query("prop") prop: String = "coordinates|pageimages|description|info",
        @Query("inprop") inprop: String = "url",
        @Query("pithumbsize") pithumbsize: Int = 144,
        @Query("generator") generator: String = "geosearch",
        @Query("ggsradius") ggsradius: Int = 10000,
        @Query("ggslimit") ggslimit: Int = 20,
        @Query("format") format: String = "json"
    ): Response<ApiResponse>
}
