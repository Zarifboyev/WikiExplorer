package com.example.newsapp.viewModel;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.newsapp.model.WikiNews;
import com.example.newsapp.utils.FetchWikiArticleTask;

import java.util.ArrayList;
import java.util.List;

public class WikiNewsViewModel extends AndroidViewModel {

    private boolean dataLoaded = false;
    private MutableLiveData<List<WikiNews>> wikiNewsLiveData = new MutableLiveData<>();

    public WikiNewsViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<WikiNews>> getWikiNewsLiveData() {
        return wikiNewsLiveData;
    }

    public void loadData() {
        if (isInternetAvailable()) {
            new FetchDataTask().execute("Android_(operating_system)");
        } else {
            wikiNewsLiveData.setValue(new ArrayList<>());
        }
    }

    public boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
        return false;
    }

    public boolean isDataLoaded() {
        return dataLoaded;
    }

    private class FetchDataTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            // Perform the network operation in the background
            return FetchWikiArticleTask.fetchWikiArticles().toString();
        }

        @Override
        protected void onPostExecute(String htmlContent) {
            // Update the LiveData on the main thread
            ArrayList<WikiNews> wikiNewsList = new ArrayList<>();
            if (htmlContent != null) {
                WikiNews wikiNews = new WikiNews("Android OS", htmlContent, "https://en.wikipedia.org/wiki/Android_(operating_system)");
                wikiNewsList.add(wikiNews);
                dataLoaded = true;
            }
            wikiNewsLiveData.setValue(wikiNewsList);
        }
    }
}
