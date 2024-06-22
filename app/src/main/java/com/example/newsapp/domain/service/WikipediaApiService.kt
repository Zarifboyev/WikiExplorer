package com.example.newsapp.domain.service

import com.example.newsapp.data.model.CONST.DOMAIN
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.BufferedReader
import java.io.InputStreamReader

interface WikipediaApiService {
    @GET("w/api.php")
    suspend fun getPageSections(
        @Query("action") action: String = "parse",
        @Query("page") page: String,
        @Query("prop") prop: String = "sections",
        @Query("format") format: String = "json"
    ): Response<SectionsResponse>

    @GET("w/api.php")
    suspend fun getRedLinks(
        @Query("action") action: String = "query",
        @Query("titles") titles: String,
        @Query("generator") generator: String = "links",
        @Query("gpllimit") gpllimit: Int = 20,
        @Query("format") format: String = "json"
    ): Response<RedLinksResponse>

    @GET("w/api.php")
    suspend fun getUserInfo(
        @Query("action") action: String = "query",
        @Query("meta") meta: String = "userinfo",
        @Query("uiprop") uiprop: String = "blockinfo|groups|rights|editcount|registrationdate",
        @Query("format") format: String = "json"
    ): Response<UserInfoResponse>
    fun fetchUserInfo(): String {
        val processBuilder = ProcessBuilder("python3", "app/src/main/java/com/example/newsapp/domain/service/fetch_user_info.py")
        val process = processBuilder.start()

        val reader = BufferedReader(InputStreamReader(process.inputStream))
        val result = StringBuilder()
        var line: String?

        while (reader.readLine().also { line = it } != null) {
            result.append(line)
        }

        reader.close()
        process.waitFor()

        return result.toString()
    }
    companion object {
        private const val BASE_URL = DOMAIN

        fun create(): WikipediaApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
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

// Data classes for responses
data class SectionsResponse(
    val parse: Parse
)



data class Parse(
    val sections: List<Section>
)

data class Section(
    val toclevel: Int,
    val line: String
)

data class RedLinksResponse(
    val query: com.example.newsapp.domain.service.Query
)

data class Query(
    val pages: Map<String, Page>?
)

data class Page(
    val title: String,
    val missing: Boolean?
)

data class UserInfoResponse(
    @SerializedName("query") val query: UserInfoQuery
)

data class UserInfoQuery(
    @SerializedName("userinfo") val userinfo: UserInfo
)

data class UserInfo(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("groups") val groups: List<String>,
    @SerializedName("rights") val rights: List<String>,
    @SerializedName("editcount") val editcount: Int,
    @SerializedName("registrationdate") val registrationdate: String
)
data class WikipediaStats(
    val articles: Int,
    val activeUsers: Int,
    val edits: Int
)


data class WikipediaResponse(
    val query: Query2
)

data class Query2(
    val statistics: Statistics
)

data class Statistics(
    val articles: Int,
    val activeusers: Int,
    val edits: Int
)