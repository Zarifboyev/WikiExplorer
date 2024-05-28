package com.example.newsapp.model

data class WikiNews(
    val title: String,
    val articleText: String,
    val linkArticle: String,
    var imageUrl: String
) {
    fun setImageResourceId(newImageUrl: String) {
        imageUrl = newImageUrl
    }
}
