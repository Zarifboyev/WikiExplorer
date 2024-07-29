package com.z99a.zarifboyevjavohir.vikipediya.data.model

import com.squareup.moshi.Json

// Place.kt
data class Place(
    @Json(name = "pageid") val pageId: Long,
    val ns: Int,
    val title: String,
    val index: Int,
    val coordinates: List<Coordinate>?,
    val thumbnail: Thumbnail?,
    val terms: Terms?,
    val fullUrl: String ?= null,
    val distance: String? = null,
    val isExisted: Boolean? = null,
    var isFavorite: Boolean = false
)

// Coordinate.kt
data class Coordinate(
    val lat: Double,
    val lon: Double,
    val primary: String?,
    val globe: String
)

// Thumbnail.kt
data class Thumbnail(
    val source: String,
    val width: Int,
    val height: Int
)

// Terms.kt
data class Terms(
    val label: List<String>?,
    val description: List<String>?
)

// ApiResponse.kt
data class ApiResponse(
    @Json(name = "batchcomplete") val batchComplete: String,
    val query: Query
)

// Query.kt
data class Query(
    val pages: Map<Long, Place>
)
