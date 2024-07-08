package com.example.newsapp.domain.service

import com.example.newsapp.data.model.CONST.DOMAIN
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface WikipediaApiService {

    companion object {
        private const val BASE_URL = DOMAIN

        fun create(): WikipediaApiService {

            val client = OkHttpClient.Builder()
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(WikipediaApiService::class.java)
        }
    }

    @GET("w/api.php")
    suspend fun getStats(
        @Query("action") action: String = "query",
        @Query("format") format: String = "json",
        @Query("meta") meta: String = "siteinfo",
        @Query("siprop") siprop: String = "statistics"
    ): Response<WikipediaResponse2>
}

// Represents each member of the category
data class CategoryMember(
    val pageid: Int,
    val ns: Int,
     val title: String
)


data class WikipediaStats(
    val articles: Int,
    val activeUsers: Int,
    val edits: Int
)

data class WikipediaResponse2(
    val query: Query3
)

data class Query3(
    val statistics: Statistics
)

data class Statistics(
    val articles: Int,
    val activeusers: Int,
    val edits: Int
)
