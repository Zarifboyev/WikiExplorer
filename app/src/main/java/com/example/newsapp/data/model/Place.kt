package com.example.newsapp.data.model
// SavedPlaces.kt
data class Place(
    val title: String,
    val description: String?,
    val distance: Double,
    val articleUrl: String,
    val thumbnail: String?,
    val isPlaceholder: Boolean = false, // Flag for placeholder
    var isFavorite: Boolean = false,
    var isArticleExistedInUzWiki: Boolean  = true // Flag for favorite
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
