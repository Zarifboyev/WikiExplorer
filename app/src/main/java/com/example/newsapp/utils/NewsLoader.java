package com.example.newsapp.utils;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.example.newsapp.model.News;

import java.util.List;

public class NewsLoader extends AsyncTaskLoader<List<News>> {
    private final String mUrl;

    public NewsLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<News> loadInBackground() {
        if (mUrl.length() < 1) {
            return null;
        }
        return QueryUtils.fetchNewsData(mUrl);
    }
}
