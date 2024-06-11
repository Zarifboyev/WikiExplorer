package com.example.newsapp.presentation.ui.components;

import android.content.res.Resources;
import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.newsapp.R;
import com.example.newsapp.data.model.YouTubePlaylistVideos;
import com.example.newsapp.domain.asyncTasksJava.GetPlaylistAsyncTask;
import com.example.newsapp.domain.asyncTasksJava.GetPlaylistTitlesAsyncTask;
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
    // the fragment initialization parameter
    private static final String ARG_YOUTUBE_PLAYLIST_IDS = "YOUTUBE_PLAYLIST_IDS";
    private static final int SPINNER_ITEM_LAYOUT_ID = android.R.layout.simple_spinner_item;
    private static final int SPINNER_ITEM_DROPDOWN_LAYOUT_ID = android.R.layout.simple_spinner_dropdown_item;

    private String[] mPlaylistIds;
    private ArrayList<String> mPlaylistTitles;
    private RecyclerView mRecyclerView;
    private YouTubePlaylistVideos mPlaylistVideos;
    private RecyclerView.LayoutManager mLayoutManager;
    private Spinner mPlaylistSpinner;
    private PlaylistCardAdapter mPlaylistCardAdapter;
    private YouTube mYouTubeDataApi;
    private ProgressDialog mProgressDialog;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param youTubeDataApi
     * @param playlistIds A String array of YouTube Playlist IDs
     * @return A new instance of fragment YouTubeRecyclerViewFragment.
     */
    public static YouTubeRecyclerViewFragment newInstance(YouTube youTubeDataApi, String[] playlistIds) {
        YouTubeRecyclerViewFragment fragment = new YouTubeRecyclerViewFragment();
        Bundle args = new Bundle();
        args.putStringArray(ARG_YOUTUBE_PLAYLIST_IDS, playlistIds);
        fragment.setArguments(args);
        fragment.setYouTubeDataApi(youTubeDataApi);
        return fragment;
    }

    public YouTubeRecyclerViewFragment() {
        // Required empty public constructor
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

        // start fetching the playlist titles
        new GetPlaylistTitlesAsyncTask(mYouTubeDataApi) {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (mProgressDialog != null && isAdded()) {
                    mProgressDialog.show();
                }
            }

            @Override
            protected void onPostExecute(PlaylistListResponse playlistListResponse) {
                if (!isAdded()) {
                    return;
                }

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

                if (mProgressDialog != null) {
                    mProgressDialog.hide();
                }
            }
        }.execute(mPlaylistIds);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // set the Picasso debug indicator only for debug builds
        Picasso.get().setIndicatorsEnabled(BuildConfig.DEBUG);

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.placeholder_youtube_recycler_view, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.youtube_recycler_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        Resources resources = getResources();
        if (resources.getBoolean(R.bool.isTablet)) {
            // use a staggered grid layout if we're on a large screen device
            mLayoutManager = new StaggeredGridLayoutManager(resources.getInteger(R.integer.columns), StaggeredGridLayoutManager.VERTICAL);
        } else {
            // use a linear layout on phone devices
            mLayoutManager = new LinearLayoutManager(getActivity());
        }

        mRecyclerView.setLayoutManager(mLayoutManager);

        mPlaylistSpinner = (Spinner)rootView.findViewById(R.id.youtube_playlist_spinner);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (mPlaylistVideos != null) {
            reloadUi(mPlaylistVideos, false);
        } else if (mPlaylistIds != null && mPlaylistIds.length > 0) {
            mPlaylistVideos = new YouTubePlaylistVideos(mPlaylistIds[0]);
            reloadUi(mPlaylistVideos, true);
        }

        ArrayAdapter<String> spinnerAdapter;
        if (mPlaylistTitles == null || mPlaylistTitles.isEmpty()) {
            spinnerAdapter = new ArrayAdapter<>(getContext(), SPINNER_ITEM_LAYOUT_ID, Arrays.asList(mPlaylistIds));
        } else {
            spinnerAdapter = new ArrayAdapter<>(getContext(), SPINNER_ITEM_LAYOUT_ID, mPlaylistTitles);
        }

        spinnerAdapter.setDropDownViewResource(SPINNER_ITEM_DROPDOWN_LAYOUT_ID);
        if (isAdded()) {
            mPlaylistSpinner.setAdapter(spinnerAdapter);
        }

        mPlaylistSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isAdded()) {
                    mPlaylistVideos = new YouTubePlaylistVideos(mPlaylistIds[position]);
                    reloadUi(mPlaylistVideos, true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }


    private void reloadUi(final YouTubePlaylistVideos playlistVideos, boolean fetchPlaylist) {
        initCardAdapter(playlistVideos);

        if (fetchPlaylist) {
            new GetPlaylistAsyncTask(mYouTubeDataApi) {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    if (mProgressDialog != null)
                        if (isAdded()) {
                            mProgressDialog.show();
                        }
                }

                @Override
                public void onPostExecute(Pair<String, List<Video>> result) {
                    if (isAdded()) {
                        handleGetPlaylistResult(playlistVideos, result);
                        if (mProgressDialog != null) {
                            mProgressDialog.dismiss();
                        }
                    }
                }
            }.execute(playlistVideos.getPlaylistId(), playlistVideos.getNextPageToken());
        }
    }


    private void initCardAdapter(final YouTubePlaylistVideos playlistVideos) {
        // create the adapter with our playlistVideos and a callback to handle when we reached the last item
        mPlaylistCardAdapter = new PlaylistCardAdapter(playlistVideos, new LastItemReachedListener() {
            @Override
            public void onLastItem(int position, String nextPageToken) {
                new GetPlaylistAsyncTask(mYouTubeDataApi) {
                    @Override
                    public void onPostExecute(Pair<String, List<Video>> result) {
                        handleGetPlaylistResult(playlistVideos, result);
                    }
                }.execute(playlistVideos.getPlaylistId(), playlistVideos.getNextPageToken());
            }
        });
        mRecyclerView.setAdapter(mPlaylistCardAdapter);
    }

    private void handleGetPlaylistResult(YouTubePlaylistVideos playlistVideos, Pair<String, List<Video>> result) {
        if (result == null) return;
        final int positionStart = playlistVideos.size();
        playlistVideos.setNextPageToken(result.first);
        playlistVideos.addAll(result.second);
        mPlaylistCardAdapter.notifyItemRangeInserted(positionStart, result.second.size());
    }


    /**
     * Interface used by the {@link } to inform us that we reached the last item in the list.
     */
    public interface LastItemReachedListener {
        void onLastItem(int position, String nextPageToken);
    }
}
