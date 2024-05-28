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
            new FetchDataTask().execute();
        } else {
            wikiNewsLiveData.setValue(null);
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

    private class FetchDataTask extends AsyncTask<Void, Void, List<WikiNews>> {
        @Override
        protected List<WikiNews> doInBackground(Void... voids) {
            // Perform the network operation in the background
            return FetchWikiArticleTask.fetchWikiArticles();
        }

        @Override
        protected void onPostExecute(List<WikiNews> wikiNewsList) {
            // Update the LiveData on the main thread
            if (wikiNewsList != null && !wikiNewsList.isEmpty()) {
                dataLoaded = true;
            }
            wikiNewsLiveData.setValue(wikiNewsList);
        }
    }
}
