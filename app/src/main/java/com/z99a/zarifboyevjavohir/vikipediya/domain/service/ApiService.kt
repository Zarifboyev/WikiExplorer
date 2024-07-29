package com.z99a.zarifboyevjavohir.vikipediya.domain.service

import com.z99a.zarifboyevjavohir.vikipediya.data.model.ApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("api.php?action=query&format=json&prop=coordinates|pageimages|pageterms&generator=geosearch&ggsradius=10000&ggslimit=10")
    suspend fun fetchPlaces(@Query("ggscoord") ggscoord: String): Response<ApiResponse>
}
