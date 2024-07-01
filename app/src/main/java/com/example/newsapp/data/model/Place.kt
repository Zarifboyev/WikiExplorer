package com.example.newsapp.data.model
// Place.kt
data class Place(
    val title: String,
    val description: String?,
    val distance: Double,
    val articleUrl: String,
    val thumbnail: String?
)
data class ApiResponse(
    val query: Query
)
data class Query(
    val pages: Map<String, Page>
)
data class Page(
    val title: String,
    val description: String?,
    val coordinates: List<Coordinate>?,
    val thumbnail: Thumbnail?,
    val fullurl: String
)
data class Coordinate(
    val lat: Double,
    val lon: Double,
    val dist: Double
)
data class Thumbnail(
    val source: String
)
