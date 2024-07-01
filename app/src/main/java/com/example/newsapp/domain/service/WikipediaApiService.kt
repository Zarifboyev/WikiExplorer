package com.example.newsapp.domain.service

import com.example.newsapp.data.model.CONST.DOMAIN
import com.example.newsapp.data.model.Page
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.BufferedReader
import java.io.InputStreamReader

interface WikipediaApiService {

    @GET("https://uz.wikipedia.org/w/api.php?action=query&cmlimit=20&cmtitle=Turkum:Vikipediya:Ko%CA%BBrsatmalar&format=json&list=categorymembers")
    suspend fun getCategoryMembers(
        @Query("action") action: String = "query",
        @Query("list") list: String = "categorymembers",
        @Query("cmtitle") category:String ="Turkum:Vikipediya:Koʻrsatmalar",
        @Query("cmlimit") limit: String = "20",
        @Query("format") format: String = "json"
    ): Response<CategoryResponse>

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
    ): Response<WikipediaResponse>


}

// Represents each member of the category
data class CategoryMember(
    val pageid: Int,
    val ns: Int,
     val title: String
)

// Represents the response from the Wikipedia API
data class CategoryResponse(
    val query: com.example.newsapp.domain.service.Query
)

// Represents the query part of the response, which contains the category members
data class Query(
    val categorymembers: List<CategoryMember>
)




data class Query2(
    val pages: Map<String, Page>?
)

data class WikipediaStats(
    val articles: Int,
    val activeUsers: Int,
    val edits: Int
)

data class WikipediaResponse(
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
