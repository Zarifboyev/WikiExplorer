package com.z99a.zarifboyevjavohir.vikipediya.utils

import com.z99a.zarifboyevjavohir.vikipediya.data.model.WikidataResponse
import com.z99a.zarifboyevjavohir.vikipediya.data.model.WikipediaResponse
import com.z99a.zarifboyevjavohir.vikipediya.di.NetworkModule
import com.z99a.zarifboyevjavohir.vikipediya.domain.service.ApiIsExistedInWikiData
import com.z99a.zarifboyevjavohir.vikipediya.domain.service.ApiWikiDataItem
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class WikiUtility @Inject constructor(
    private val wikidataApiService: ApiIsExistedInWikiData,
    @NetworkModule.UzbekRetrofit private val uzbekRetrofit: Retrofit,
    @NetworkModule.RussianRetrofit private val russianRetrofit: Retrofit,
    @NetworkModule.EnglishRetrofit private val englishRetrofit: Retrofit
) {

    private fun getWikipediaApi(langCode: String): ApiWikiDataItem {
        return when (langCode) {
            "en" -> englishRetrofit.create(ApiWikiDataItem::class.java)
            "ru" -> russianRetrofit.create(ApiWikiDataItem::class.java)
            else -> uzbekRetrofit.create(ApiWikiDataItem::class.java)
        }
    }

    suspend fun getWikidataItemId(langCode: String, articleTitle: String): String? =
        suspendCancellableCoroutine { continuation ->
            val wikipediaApi = getWikipediaApi(langCode)
            wikipediaApi.getWikidataItemId("query", "json", "pageprops", articleTitle).enqueue(object : Callback<WikipediaResponse> {
                override fun onResponse(call: Call<WikipediaResponse>, response: Response<WikipediaResponse>) {
                    if (response.isSuccessful) {
                        val pages = response.body()?.query?.pages
                        val pageProps = pages?.values?.firstOrNull()?.pageprops
                        if (pageProps != null) {
                            val wikidataItemId = pageProps.wikibase_item
                            Timber.d("Wikidata item ID: $wikidataItemId")
                            continuation.resume(wikidataItemId)
                        } else {
                            Timber.d("Page properties are null")
                            continuation.resume(null)
                        }
                    } else {
                        Timber.e("Failed response: ${response.errorBody()?.string()}")
                        continuation.resume(null)
                    }
                }

                override fun onFailure(call: Call<WikipediaResponse>, t: Throwable) {
                    Timber.e(t, "API call failed")
                    continuation.resumeWithException(t)
                }
            })
        }

    private suspend fun doesWikidataItemExistInUzwiki(wikidataItemId: String): Boolean =
        suspendCancellableCoroutine { continuation ->
            wikidataApiService.doesWikidataItemExist("wbgetentities", "json", wikidataItemId, "sitelinks")
                .enqueue(object : Callback<WikidataResponse> {
                    override fun onResponse(call: Call<WikidataResponse>, response: Response<WikidataResponse>) {
                        if (response.isSuccessful) {
                            val entities = response.body()?.entities
                            val exists = entities?.values?.firstOrNull()?.sitelinks?.containsKey("uzwiki") ?: false
                            continuation.resume(exists)
                        } else {
                            continuation.resume(false)
                        }
                    }

                    override fun onFailure(call: Call<WikidataResponse>, t: Throwable) {
                        continuation.resumeWithException(t)
                    }
                })
        }

    suspend fun isArticleInBothWikis(langCode: String, articleTitle: String): Boolean {
        val wikidataItemIdDeferred = CoroutineScope(Dispatchers.IO).async(start = CoroutineStart.LAZY) {
            getWikidataItemId(langCode, articleTitle)
        }

        val wikidataItemId = wikidataItemIdDeferred.await() ?: return false
        return doesWikidataItemExistInUzwiki(wikidataItemId)
    }
}
