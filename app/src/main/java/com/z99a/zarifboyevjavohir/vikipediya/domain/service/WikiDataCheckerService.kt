package com.z99a.zarifboyevjavohir.vikipediya.domain.service

import com.z99a.zarifboyevjavohir.vikipediya.data.model.WikidataResponse
import com.z99a.zarifboyevjavohir.vikipediya.data.model.WikipediaResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiWikiDataItem {
    @GET("api.php")
    fun getWikidataItemId(
        @Query("action") action: String,
        @Query("format") format: String,
        @Query("prop") prop: String,
        @Query("titles") titles: String
    ): Call<WikipediaResponse>
}

interface ApiIsExistedInWikiData {
    @GET("api.php")
    fun doesWikidataItemExist(
        @Query("action") action: String,
        @Query("format") format: String,
        @Query("ids") ids: String,
        @Query("props") props: String
    ): Call<WikidataResponse>
}