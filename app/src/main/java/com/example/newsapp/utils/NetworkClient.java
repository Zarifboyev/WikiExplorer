package com.example.newsapp.utils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NetworkClient {
    public static NetworkClient.NetworkCallback NetworkCallback;
    private OkHttpClient client;

    public NetworkClient() {
        client = new OkHttpClient();
    }

    public void fetchWikiArticle(String title, NetworkCallback callback) {
        String url = "https://en.wikipedia.org/api/rest_v1/page/html/" + title;
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String htmlContent = response.body().string();
                    callback.onSuccess(htmlContent);
                } else {
                    callback.onFailure(new IOException("Unexpected response code: " + response.code()));
                }
            }
        });
    }

    public interface NetworkCallback {
        void onSuccess(String data);
        void onFailure(IOException e);
    }
}