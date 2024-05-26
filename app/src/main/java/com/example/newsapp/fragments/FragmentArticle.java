package com.example.newsapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.newsapp.utils.FetchWikiArticleTask;
import com.example.newsapp.R;

import okhttp3.OkHttpClient;


public class FragmentArticle extends Fragment implements FetchWikiArticleTask.FetchWikiArticleListener {

    private  WebView webView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_article, container, false);

        webView = view.findViewById(R.id.txtDescription);

        // Enable JavaScript in WebView
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Load Wikipedia article content
        new FetchWikiArticleTask((FetchWikiArticleTask.FetchWikiArticleListener) this).execute("Android_(operating_system)");

        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        webView.setWebViewClient(new WebViewClient()); // Ensure links are opened within the WebView
        webView.getSettings().setJavaScriptEnabled(true); // Enable JavaScript if needed

        // Initialize OkHttpClient
        OkHttpClient client = new OkHttpClient();
    }

    @Override
    public void onWikiArticleFetched(String htmlContent) {
        if (htmlContent != null) {
            // Load HTML content into WebView
            webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        webView = null;
    }

}
