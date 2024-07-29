package com.z99a.zarifboyevjavohir.vikipediya.presentation.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.z99a.zarifboyevjavohir.vikipediya.domain.service.*
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class YouTubeViewModel : ViewModel() {

    private val apiService: YouTubeServiceReady = Retrofit.Builder()
        .baseUrl("https://www.googleapis.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build().create(YouTubeServiceReady::class.java)


    private val _playlists = MutableLiveData<List<YouTubePlaylistItem>>()
    val playlists: LiveData<List<YouTubePlaylistItem>> = _playlists

    private val _playlistItems = MutableLiveData<List<PlaylistItem>>()
    val playlistItems: LiveData<List<PlaylistItem>> = _playlistItems

    private val _videoStatistics = MutableLiveData<Map<String, VideoDetails>>()
    val videoStatistics: LiveData<Map<String, VideoDetails>> = _videoStatistics

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun fetchPlaylists(apiKey: String, channelId: String) {
        viewModelScope.launch {
            try {
                setLoading(true)
                val response = apiService.getPlaylists(apiKey = apiKey, channelId = channelId)
                _playlists.value = response.items
                Log.d("YouTubeViewModel", "fetchPlaylists: ${response.items}")
            } catch (e: Exception) {
                handleError("Failed to fetch playlists", e)
            } finally {
                setLoading(false)
            }
        }
    }

    fun fetchPlaylistItems(apiKey: String, playlistId: String) {
        viewModelScope.launch {
            try {
                setLoading(true)
                val response = apiService.getPlaylistItems(playlistId = playlistId, apiKey = apiKey)
                _playlistItems.value = response.items
                fetchVideoDetails(response.items.map { it.contentDetails.videoId }, apiKey)
                Log.d("YouTubeViewModel", "fetchPlaylistItems: ${response.items}")
            } catch (e: Exception) {
                handleError("Failed to fetch playlist items", e)
            } finally {
                setLoading(false)
            }
        }
    }

    private fun fetchVideoDetails(videoIds: List<String>, apiKey: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getVideoStatistics(videoId = videoIds.joinToString(","), apiKey = apiKey)
                val videoDetailsMap = response.items.associateBy({ it.id }, { VideoDetails(it.snippet, it.contentDetails, it.statistics) })
                _videoStatistics.value = videoDetailsMap
                Log.d("YouTubeViewModel", "fetchVideoDetails: ${response.items}")
            } catch (e: Exception) {
                handleError("Failed to fetch video details", e)
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
    }

    private fun handleError(message: String, e: Exception) {
        _error.value = "$message: ${e.message}"
        Log.e("YouTubeViewModel", message, e)
    }
}

data class VideoDetails(
    val snippet: VideoSnippet,
    val contentDetails: ContentDetails,
    val statistics: VideoStatistics
)
