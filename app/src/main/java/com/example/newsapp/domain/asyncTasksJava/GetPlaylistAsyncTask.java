package com.example.newsapp.domain.asyncTasksJava;


import android.os.AsyncTask;
import android.util.Pair;

import com.example.newsapp.data.model.CONST;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import timber.log.Timber;


public abstract class GetPlaylistAsyncTask extends AsyncTask<String, Void, Pair<String, List<Video>>> {
    private static final String TAG = "GetPlaylistAsyncTask";
    private static final Long YOUTUBE_PLAYLIST_MAX_RESULTS = 10L;

    //see: https://developers.google.com/youtube/v3/docs/playlistItems/list
    private static final String YOUTUBE_PLAYLIST_PART = "snippet";
    private static final String YOUTUBE_PLAYLIST_FIELDS = "pageInfo,nextPageToken,items(id,snippet(resourceId/videoId))";
    //see: https://developers.google.com/youtube/v3/docs/videos/list
    private static final String YOUTUBE_VIDEOS_PART = "snippet,contentDetails,statistics"; // video resource properties that the response will include.
    private static final String YOUTUBE_VIDEOS_FIELDS = "items(id,snippet(title,description,thumbnails/high),contentDetails/duration,statistics)"; // selector specifying which fields to include in a partial response.

    private final YouTube mYouTubeDataApi;

    public GetPlaylistAsyncTask(YouTube api) {
        mYouTubeDataApi = api;
    }

    @Override
    protected Pair<String, List<Video>> doInBackground(String... params) {
        final String playlistId = params[0];
        final String nextPageToken;

        if (params.length == 2) {
            nextPageToken = params[1];
        } else {
            nextPageToken = null;
        }

        PlaylistItemListResponse playlistItemListResponse;
        try {
            playlistItemListResponse = mYouTubeDataApi.playlistItems()
                    .list(Collections.singletonList(YOUTUBE_PLAYLIST_PART))
                    .setPlaylistId(playlistId)
                    .setPageToken(nextPageToken)
                    .setFields(YOUTUBE_PLAYLIST_FIELDS)
                    .setMaxResults(YOUTUBE_PLAYLIST_MAX_RESULTS)
                    .setKey(CONST.YOUTUBE_API_KEY)
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        if (playlistItemListResponse == null) {
            Timber.tag(TAG).e("Failed to get playlist");
            return null;
        }

        List<String> videoIds = new ArrayList<>();

        // pull out the video id's from the playlist page
        for (PlaylistItem item : playlistItemListResponse.getItems()) {
            videoIds.add(item.getSnippet().getResourceId().getVideoId());
        }

        // get details of the videos on this playlist page
        VideoListResponse videoListResponse = null;
        try {
            videoListResponse = mYouTubeDataApi.videos()
                    .list(Collections.singletonList(YOUTUBE_VIDEOS_PART))
                    .setFields(YOUTUBE_VIDEOS_FIELDS)
                    .setKey(CONST.YOUTUBE_API_KEY)
                    .setId(videoIds).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert videoListResponse != null;
        return new Pair<>(playlistItemListResponse.getNextPageToken(), videoListResponse.getItems());
    }

}
