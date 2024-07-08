package com.example.newsapp.data.model

import com.google.gson.annotations.SerializedName

data class WikipediaResponse(
    @SerializedName("query") val query: Query5?
)

data class Query5(
    @SerializedName("pages") val pages: Map<String, Page5>
)

data class Page5(
    @SerializedName("pageprops") val pageprops: PageProps?
)

data class PageProps(
    @SerializedName("wikibase_item") val wikibaseItem: String?
)

data class WikidataResponse(
    @SerializedName("entities") val entities: Map<String, Entity>
)

data class Entity(
    @SerializedName("sitelinks") val sitelinks: Map<String, Sitelink>
)

data class Sitelink(
    @SerializedName("site") val site: String
)
