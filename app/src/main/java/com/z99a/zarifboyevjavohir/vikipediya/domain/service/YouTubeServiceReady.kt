package com.z99a.zarifboyevjavohir.vikipediya.domain.service
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface YouTubeServiceReady {

    @GET("youtube/v3/playlists")
    suspend fun getPlaylists(
        @Query("part") part: String = "snippet",
        @Query("channelId") channelId: String,
        @Query("key") apiKey: String
    ): YouTubePlaylistResponse

    @GET("youtube/v3/playlistItems")
    suspend fun getPlaylistItems(
        @Query("part") part: String = "snippet,contentDetails",
        @Query("playlistId") playlistId: String,
        @Query("maxResults") maxResults: Int = 10,
        @Query("key") apiKey: String
    ): PlaylistItemListResponse

    @GET("youtube/v3/videos")
    suspend fun getVideoStatistics(
        @Query("part") part: String = "snippet,contentDetails,statistics",
        @Query("id") videoId: String,
        @Query("key") apiKey: String
    ): YouTubeApiResponse

}

data class PlaylistItemListResponse(
    val kind: String,
    val etag: String,
    val nextPageToken: String?,
    val prevPageToken: String?,
    val pageInfo: PageInfo,
    val items: List<PlaylistItem>
)

data class PlaylistItem(
    val kind: String,
    val etag: String,
    val id: String,
    val snippet: PlaylistSnippet,
    val contentDetails: PlaylistItemContentDetails
)

data class PlaylistSnippet(
    val publishedAt: String,
    val channelId: String,
    val title: String,
    val description: String,
    val thumbnails: Thumbnails,
    val channelTitle: String,
    val localized: LocalizedSnippet
)

data class PlaylistItemContentDetails(
    val videoId: String,
    val videoPublishedAt: String
)

data class Thumbnails(
    val default: Thumbnail?,
    val medium: Thumbnail?,
    val high: Thumbnail?
)

data class Thumbnail(
    val url: String,
    val width: Int,
    val height: Int
)

data class LocalizedSnippet(
    val title: String,
    val description: String
)

data class PageInfo(
    val totalResults: Int,
    val resultsPerPage: Int
)

data class YouTubePlaylistResponse(
    val items: List<YouTubePlaylistItem>
)

data class YouTubePlaylistItem(
    val id: String,
    val snippet: Snippet
)

data class Snippet(
    val title: String,
    val thumbnails: Thumbnails
)

data class ThumbnailDetails(
    val url: String,
    val width: Int,
    val height: Int
)

data class YouTubeApiResponse(
    val items: List<YouTubeVideoItem>
)

data class YouTubeVideoItem(
    val id: String,
    val snippet: VideoSnippet,
    val contentDetails: ContentDetails,
    val statistics: VideoStatistics
)

data class VideoSnippet(
    val title: String,
    val publishedAt: String,
    val thumbnails: Thumbnails
)

data class ContentDetails(
    val duration: String
)

data class VideoStatistics(
    val viewCount: String?,
    val likeCount: String?,
    val dislikeCount: String?,
    val favoriteCount: String?,
    val commentCount: String?
)
