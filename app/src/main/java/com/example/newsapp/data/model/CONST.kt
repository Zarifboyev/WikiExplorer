package com.example.newsapp.data.model

object CONST {
    const val YOUTUBE_API_KEY: String = "AIzaSyArU-2UAoQx3Fva42tLfhg0c7x950rdY4Y"

    const val TAG = "GetPlaylistAsyncTask"
    const val YOUTUBE_PLAYLIST_MAX_RESULTS = 10L

    //see: https://developers.google.com/youtube/v3/docs/playlistItems/list
    const val YOUTUBE_PLAYLIST_PART = "snippet"
    const val YOUTUBE_PLAYLIST_FIELDS =
        "pageInfo,nextPageToken,items(id,snippet(resourceId/videoId))"
    private const val YOUTUBE_PLAYLIST_FIELDS_FOR_TITLE = "items(id,snippet(title))"
    //see: https://developers.google.com/youtube/v3/docs/videos/list
    const val YOUTUBE_VIDEOS_PART =
        "snippet,contentDetails,statistics" // video resource properties that the response will include.
    const val YOUTUBE_VIDEOS_FIELDS =
        "items(id,snippet(title,description,thumbnails/high),contentDetails/duration,statistics)"
    // selector specifying which fields to include in a partial response.


}