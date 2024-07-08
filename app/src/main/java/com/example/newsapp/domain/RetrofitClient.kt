package com.example.newsapp.domain

import com.example.newsapp.domain.service.ApiIsExistedInWikiData
import com.example.newsapp.domain.service.ApiWikiDataItem
import com.example.newsapp.domain.service.YouTubeServiceReady
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "https://www.googleapis.com/"
    const val WIKIDATA_BASE_URL = "https://www.wikidata.org/w/"

    fun create(): YouTubeServiceReady {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(YouTubeServiceReady::class.java)
    }

    fun getWikidataClient(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(WIKIDATA_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val wikipediaRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://uz.wikipedia.org/") // Change the base URL dynamically
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val wikidataRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://www.wikidata.org/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val wikipediaApiService: ApiWikiDataItem = wikipediaRetrofit.create(ApiWikiDataItem::class.java)
    val wikidataApiService: ApiIsExistedInWikiData = wikidataRetrofit.create(ApiIsExistedInWikiData::class.java)

}
