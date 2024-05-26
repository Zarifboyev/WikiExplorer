package com.example.newsapp.viewModel;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

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
    private MutableLiveData<List<WikiNews>> wikiNewsLiveData;

    private final Application application; // Store the Application context

    public WikiNewsViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
    }

    public LiveData<List<WikiNews>> getWikiNewsLiveData() {
        if (wikiNewsLiveData == null) {
            wikiNewsLiveData = new MutableLiveData<>();
            loadData(); // You can load initial data here
        }
        return wikiNewsLiveData;
    }

    public void loadData() {
        if (isInternetAvailable()) {
            new FetchWikiArticleTask(new FetchWikiArticleTask.FetchWikiArticleListener() {
                @Override
                public void onWikiArticleFetched(String htmlContent) {
                    ArrayList<WikiNews> wikiNewsList = new ArrayList<>();
                    if (htmlContent != null) {
                        dataLoaded = true;
                        WikiNews wikiNews = new WikiNews("Android OS", htmlContent, "https://en.wikipedia.org/wiki/Android_(operating_system)");
                        wikiNewsList.add(wikiNews);
                    }
                    wikiNewsLiveData.setValue(wikiNewsList);
                }
            }).execute("Android_(operating_system)");
        } else {
            wikiNewsLiveData.setValue(new ArrayList<>());
        }
    }

    public boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
        return false;
    }

    public boolean isDataLoaded() {
        return dataLoaded;
    }
}
