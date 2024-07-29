package com.z99a.zarifboyevjavohir.vikipediya.data.model

import com.squareup.moshi.Json

data class WikidataResponse(
    @Json(name = "entities")
    val entities: Map<String, Entity>?
) {
    data class Entity(
        @Json(name = "type")
        val type: String?,
        @Json(name = "id")
        val id: String?,
        @Json(name = "sitelinks")
        val sitelinks: Map<String, SiteLink>?
    ) {
        data class SiteLink(
            @Json(name = "site")
            val site: String?,
            @Json(name = "title")
            val title: String?,
            @Json(name = "badges")
            val badges: List<String>?
        )
    }
}