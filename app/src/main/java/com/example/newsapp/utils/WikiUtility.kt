package com.example.newsapp.utils

import com.example.newsapp.data.model.WikidataResponse
import com.example.newsapp.data.model.WikipediaResponse
import com.example.newsapp.domain.RetrofitClient.wikidataApiService
import com.example.newsapp.domain.service.ApiWikiDataItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object WikiUtility {
    fun getWikidataItemId(langCode: String, articleTitle: String, callback: (String?) -> Unit) {
        val wikipediaApi = Retrofit.Builder()
            .baseUrl("https://$langCode.wikipedia.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiWikiDataItem::class.java)

        wikipediaApi.getWikidataItemId("query", "json", "pageprops", articleTitle).enqueue(object :
            Callback<WikipediaResponse> {
            override fun onResponse(call: Call<WikipediaResponse>, response: Response<WikipediaResponse>) {
                if (response.isSuccessful) {
                    val pages = response.body()?.query?.pages
                    pages?.values?.forEach { page ->
                        page.pageprops?.wikibaseItem?.let {
                            callback(it)
                            return
                        }
                    }
                }
                callback(null)
            }

            override fun onFailure(call: Call<WikipediaResponse>, t: Throwable) {
                callback(null)
            }
        })
    }

    fun doesWikidataItemExistInUzwiki(wikidataItemId: String, callback: (Boolean) -> Unit) {
        wikidataApiService.doesWikidataItemExist("wbgetentities", "json", wikidataItemId, "sitelinks").enqueue(object : Callback<WikidataResponse> {
            override fun onResponse(call: Call<WikidataResponse>, response: Response<WikidataResponse>) {
                if (response.isSuccessful) {
                    val entities = response.body()?.entities
                    entities?.values?.forEach { entity ->
                        if (entity.sitelinks.containsKey("uzwiki")) {
                            callback(true)
                            return
                        }
                    }
                }
                callback(false)
            }

            override fun onFailure(call: Call<WikidataResponse>, t: Throwable) {
                callback(false)
            }
        })
    }

    fun isArticleInBothWikis(langCode: String, articleTitle: String, callback: (Boolean) -> Unit) {
        getWikidataItemId(langCode, articleTitle) { wikidataItemId ->
            if (wikidataItemId != null) {
                doesWikidataItemExistInUzwiki(wikidataItemId) { exists ->
                    callback(exists)
                }
            } else {
                callback(false)
            }
        }
    }

}