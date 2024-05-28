package com.example.newsapp.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.example.newsapp.databinding.FragmentArticleBinding
import com.example.newsapp.model.WikiNews
import com.example.newsapp.utils.FetchWikiArticleTask.FetchWikiArticleListener

public class FragmentArticle : Fragment(), FetchWikiArticleListener {

    private lateinit var _binding: FragmentArticleBinding;
    private val binding get() = _binding
    private var webView: WebView? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArticleBinding.inflate(inflater, container, false)
        val view = binding.root

        webView = binding.txtDescription

        // Enable JavaScript in WebView
        val webSettings = webView!!.settings
        webSettings.javaScriptEnabled = true

        // Load Wikipedia article content
        // FetchWikiArticleTask(this).execute("Android_(operating_system)")

        return view
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        webView?.apply {
            webViewClient = WebViewClient() // Ensure links are opened within the WebView
            settings.javaScriptEnabled = true // Enable JavaScript if needed
        }

        // Initialize OkHttpClient
    }

    override fun onWikiArticleFetched(htmlContent: String?) {
        if (htmlContent != null) {
            // Load HTML content into WebView
            webView?.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
        }
    }

    override fun onWikiArticlesFetched(wikiNewsList: List<WikiNews>?) {
        TODO("Not yet implemented")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //TODO: Destroy the binding here
        webView = null
    }


}
