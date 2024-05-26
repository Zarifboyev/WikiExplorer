package com.example.newsapp.utils;

import android.os.AsyncTask;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public  class FetchWikiArticleTask extends AsyncTask<String, Void, String> {

    private FetchWikiArticleListener listener;

    public interface FetchWikiArticleListener {
        void onWikiArticleFetched(String htmlContent);
    }

    public FetchWikiArticleTask(FetchWikiArticleListener listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... params) {
        String title = params[0];
        String url = "https://en.wikipedia.org/api/rest_v1/page/html/" + title;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String htmlContent) {
        super.onPostExecute(htmlContent);
        if (listener != null) {
            listener.onWikiArticleFetched(htmlContent);
        }
    }

}

