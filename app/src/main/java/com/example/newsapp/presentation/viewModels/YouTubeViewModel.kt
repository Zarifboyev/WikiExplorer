package com.example.newsapp.presentation.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.domain.repository.RetrofitClient
import com.example.newsapp.domain.service.*
import kotlinx.coroutines.launch

class YouTubeViewModel : ViewModel() {

    private val apiService: YouTubeServiceReady = RetrofitClient.create()

    private val _playlists = MutableLiveData<List<YouTubePlaylistItem>>()
    val playlists: LiveData<List<YouTubePlaylistItem>> = _playlists

    private val _playlistItems = MutableLiveData<List<PlaylistItem>>()
    val playlistItems: LiveData<List<PlaylistItem>> = _playlistItems

    private val _videoStatistics = MutableLiveData<Map<String, VideoDetails>>()
    val videoStatistics: LiveData<Map<String, VideoDetails>> = _videoStatistics

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun fetchPlaylists(apiKey: String, channelId: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getPlaylists(apiKey = apiKey, channelId = channelId)
                _playlists.value = response.items
                Log.d("TAG", "fetchPlaylists: ${response.items}")
            } catch (e: Exception) {
                _error.value = "Failed to fetch playlists: ${e.message}"
            }
        }
    }

    fun fetchPlaylistItems(apiKey: String, playlistId: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getPlaylistItems(playlistId = playlistId, apiKey = apiKey)
                _playlistItems.value = response.items
                fetchVideoDetails(response.items.map { it.contentDetails.videoId }, apiKey)
                Log.d("TAG", "fetchPlaylistItems: ${response.items}")
            } catch (e: Exception) {
                _error.value = "Failed to fetch playlist items: ${e.message}"
            }
        }
    }

    private fun fetchVideoDetails(videoIds: List<String>, apiKey: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getVideoStatistics(videoId = videoIds.joinToString(","), apiKey = apiKey)
                val videoDetailsMap = response.items.associateBy({ it.id }, { VideoDetails(it.snippet, it.contentDetails, it.statistics) })
                _videoStatistics.value = videoDetailsMap
                Log.d("TAG", "fetchVideoDetails: ${response.items}")
            } catch (e: Exception) {
                _error.value = "Failed to fetch video details: ${e.message}"
            }
        }
    }
}

data class VideoDetails(
    val snippet: VideoSnippet,
    val contentDetails: ContentDetails,
    val statistics: VideoStatistics
)
