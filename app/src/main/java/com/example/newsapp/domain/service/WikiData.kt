package com.example.newsapp.domain.service
import com.example.newsapp.data.model.WikidataResponse
import com.example.newsapp.data.model.WikipediaResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiWikiDataItem {
    @GET("w/api.php")
    fun getWikidataItemId(
        @Query("action") action: String,
        @Query("format") format: String,
        @Query("prop") prop: String,
        @Query("titles") titles: String
    ): Call<WikipediaResponse>
}

interface ApiIsExistedInWikiData {
    @GET("w/api.php")
    fun doesWikidataItemExist(
        @Query("action") action: String,
        @Query("format") format: String,
        @Query("ids") ids: String,
        @Query("props") props: String
    ): Call<WikidataResponse>
}
