package com.example.newsapp.data.model

import com.google.api.services.youtube.model.Video

data class YouTubePlaylistVideos(val playlistId: String) : ArrayList<Video?>() {
    var nextPageToken: String? = null
}
