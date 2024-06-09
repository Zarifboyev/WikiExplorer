package com.example.newsapp.presentation.screen;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.newsapp.R;
import com.example.newsapp.data.model.PlaylistVideos;
import com.example.newsapp.domain.repository.GetPlaylistTitlesAsyncTask;
import com.example.newsapp.domain.repository.GetPlaylistAsyncTask;
import com.example.newsapp.presentation.adapters.PlaylistCardAdapter;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.PlaylistListResponse;
import com.google.api.services.youtube.model.Video;
import com.squareup.picasso.BuildConfig;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class YouTubeRecyclerViewFragment extends Fragment {

    private static final String ARG_YOUTUBE_PLAYLIST_IDS = "YOUTUBE_PLAYLIST_IDS";
    private static final int SPINNER_ITEM_LAYOUT_ID = android.R.layout.simple_spinner_item;
    private static final int SPINNER_ITEM_DROPDOWN_LAYOUT_ID = android.R.layout.simple_spinner_dropdown_item;

    private String[] mPlaylistIds;
    private ArrayList<String> mPlaylistTitles;
    private RecyclerView mRecyclerView;
    private PlaylistVideos mPlaylistVideos;
    private Spinner mPlaylistSpinner;
    private PlaylistCardAdapter mPlaylistCardAdapter;
    private YouTube mYouTubeDataApi;
    private ProgressDialog mProgressDialog;

    public static YouTubeRecyclerViewFragment newInstance(YouTube youTubeDataApi, String[] playlistIds) {
        YouTubeRecyclerViewFragment fragment = new YouTubeRecyclerViewFragment();
        Bundle args = new Bundle();
        args.putStringArray(ARG_YOUTUBE_PLAYLIST_IDS, playlistIds);
        fragment.setArguments(args);
        fragment.setYouTubeDataApi(youTubeDataApi);
        return fragment;
    }

    public void setYouTubeDataApi(YouTube api) {
        mYouTubeDataApi = api;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            mPlaylistIds = getArguments().getStringArray(ARG_YOUTUBE_PLAYLIST_IDS);
        }

        mProgressDialog = new ProgressDialog(getContext());

        new GetPlaylistTitlesAsyncTask(mYouTubeDataApi) {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (mProgressDialog != null) {
                    mProgressDialog.setMessage(getString(R.string.loading_playlists));
                    mProgressDialog.show();
                }
            }

            @Override
            protected void onPostExecute(PlaylistListResponse playlistListResponse) {
                if (playlistListResponse == null) {
                    return;
                }

                mPlaylistTitles = new ArrayList<>();
                for (com.google.api.services.youtube.model.Playlist playlist : playlistListResponse.getItems()) {
                    mPlaylistTitles.add(playlist.getSnippet().getTitle());
                }

                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), SPINNER_ITEM_LAYOUT_ID, mPlaylistTitles);
                spinnerAdapter.setDropDownViewResource(SPINNER_ITEM_DROPDOWN_LAYOUT_ID);
                mPlaylistSpinner.setAdapter(spinnerAdapter);
                mProgressDialog.hide();
            }
        }.execute(mPlaylistIds);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Picasso.get().setIndicatorsEnabled(BuildConfig.DEBUG);
        View rootView = inflater.inflate(R.layout.youtube_recycler_view_fragment, container, false);

        mRecyclerView = rootView.findViewById(R.id.youtube_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        Resources resources = getResources();
        RecyclerView.LayoutManager mLayoutManager;
        if (resources.getBoolean(R.bool.isTablet)) {
            mLayoutManager = new StaggeredGridLayoutManager(resources.getInteger(R.integer.columns), StaggeredGridLayoutManager.VERTICAL);
        } else {
            mLayoutManager = new LinearLayoutManager(getActivity());
        }

        mRecyclerView.setLayoutManager(mLayoutManager);
        mPlaylistSpinner = rootView.findViewById(R.id.youtube_playlist_spinner);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (mPlaylistVideos != null) {
            reloadUi(mPlaylistVideos, false);
        } else {
            mPlaylistVideos = new PlaylistVideos(mPlaylistIds[0]);
            reloadUi(mPlaylistVideos, true);
        }

        ArrayAdapter<String> spinnerAdapter;
        if (mPlaylistTitles == null || mPlaylistTitles.isEmpty()) {
            spinnerAdapter = new ArrayAdapter<>(requireContext(), SPINNER_ITEM_LAYOUT_ID, Arrays.asList(mPlaylistIds));
        } else {
            spinnerAdapter = new ArrayAdapter<>(requireContext(), SPINNER_ITEM_LAYOUT_ID, mPlaylistTitles);
        }

        spinnerAdapter.setDropDownViewResource(SPINNER_ITEM_DROPDOWN_LAYOUT_ID);
        mPlaylistSpinner.setAdapter(spinnerAdapter);

        mPlaylistSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mPlaylistVideos = new PlaylistVideos(mPlaylistIds[position]);
                reloadUi(mPlaylistVideos, true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });

    }

    private void reloadUi(final PlaylistVideos playlistVideos, boolean fetchPlaylist) {
        initCardAdapter(playlistVideos);

        if (fetchPlaylist) {
            new GetPlaylistAsyncTask(mYouTubeDataApi) {
                @Override
                public void onTaskCompleted(@Nullable String nextPageToken, @NonNull List<Video> videos) {

                }

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    if (mProgressDialog != null) {
                        mProgressDialog.show();
                    }
                }

                @Override
                public void onPostExecute(Pair<String, List<Video>> result) {
                    handleGetPlaylistResult(playlistVideos, result);
                    mProgressDialog.dismiss();
                }
            }.execute(playlistVideos.getPlaylistId(), playlistVideos.getNextPageToken());
        }
    }

    private void initCardAdapter(final PlaylistVideos playlistVideos) {
        mPlaylistCardAdapter = new PlaylistCardAdapter(playlistVideos, new LastItemReachedListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onLastItem(int position, String nextPageToken) {
                new GetPlaylistAsyncTask(mYouTubeDataApi) {
                    @Override
                    public void onTaskCompleted(@Nullable String nextPageToken, @NonNull List<Video> videos) {

                    }

                    @Override
                    protected void onPostExecute(Pair<String, List<Video>> result) {
                        handleGetPlaylistResult(playlistVideos, result);
                    }
                }.execute(playlistVideos.getPlaylistId(), playlistVideos.getNextPageToken());
            }
        });
        mRecyclerView.setAdapter(mPlaylistCardAdapter);
    }

    private void handleGetPlaylistResult(PlaylistVideos playlistVideos, Pair<String, List<Video>> result) {
        if (result == null) return;
        final int positionStart = playlistVideos.size();
        playlistVideos.setNextPageToken(result.first);
        playlistVideos.addAll(result.second);
        mPlaylistCardAdapter.notifyItemRangeInserted(positionStart, result.second.size());
    }

    public interface LastItemReachedListener {
        void onLastItem(int position, String nextPageToken);
    }
    }
