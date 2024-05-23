package com.example.newsapp.activity;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newsapp.adapter.NewsAdapter;
import com.example.newsapp.utils.NewsLoader;
import com.example.newsapp.R;
import com.example.newsapp.model.News;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, LoaderManager.LoaderCallbacks<List<News>> {
    private NewsAdapter mAdapter;
    private String mSearchQuery;
    RecyclerView mRecyclerView;
    TextView mEmptyStateTextView;
    ImageView mNoInternet;
    View mLoadingIndicator;

    private static final int NEWS_LOADER_ID = 1;

    private static final String REQUEST_URL = "https://content.guardianapis.com/search?&show-tags=contributor";

    @SuppressLint("QueryPermissionsNeeded")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_activity);
        mRecyclerView = findViewById(R.id.recycler_view);
        mEmptyStateTextView = findViewById(R.id.empty_view);
        mLoadingIndicator = findViewById(R.id.loading_indicator);
        mNoInternet = findViewById(R.id.no_internet);

        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new NewsAdapter(new ArrayList<>(), news -> {
            String url = news.getUrl();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }

        });

        mRecyclerView.setAdapter(mAdapter);

        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        } else {
            mLoadingIndicator.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.GONE);
            mNoInternet.setVisibility(View.VISIBLE);
            mNoInternet.setImageResource(R.drawable.no_internet);
            mEmptyStateTextView.setVisibility(View.VISIBLE);
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {
        mLoadingIndicator.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);

        Uri uri = Uri.parse(REQUEST_URL);
        Uri.Builder uriBuilder = uri.buildUpon();
        uriBuilder.appendQueryParameter("api-key", "bb08b092-98ce-4103-b9a0-51f805c699d8");
        if (mSearchQuery != null)
            uriBuilder.appendQueryParameter("q", mSearchQuery);
        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> newsList) {
        mLoadingIndicator.setVisibility(View.GONE);
        mNoInternet.setVisibility(View.GONE);
        mEmptyStateTextView.setVisibility(View.VISIBLE);
        mEmptyStateTextView.setText(R.string.no_news);

        mAdapter.clear();

        if (newsList != null && !newsList.isEmpty()) {
            mRecyclerView.setVisibility(View.VISIBLE);
            mNoInternet.setVisibility(View.GONE);
            mEmptyStateTextView.setVisibility(View.GONE);
            mAdapter.addAll(newsList);
        }
    }


    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        mAdapter.clear();
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mSearchQuery = query;
        getLoaderManager().restartLoader(NEWS_LOADER_ID, null, this);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search_button));
        searchView.setOnQueryTextListener(this);
        return true;
    }
}
