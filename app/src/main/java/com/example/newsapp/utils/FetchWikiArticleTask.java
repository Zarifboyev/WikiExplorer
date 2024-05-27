package com.example.newsapp.utils;

import android.os.AsyncTask;

import io.github.fastily.jwiki.core.NS;
import io.github.fastily.jwiki.core.Wiki;
import com.example.newsapp.model.WikiNews;

import java.util.ArrayList;
import java.util.List;

public class FetchWikiArticleTask extends AsyncTask<Void, Void, List<WikiNews>> {

    private FetchWikiArticleListener listener;

    public interface FetchWikiArticleListener {
        void onWikiArticleFetched(String htmlContent);

        void onWikiArticlesFetched(List<WikiNews> wikiNewsList);
    }

    public FetchWikiArticleTask(FetchWikiArticleListener listener) {
        this.listener = listener;
    }

    @Override
    protected List<WikiNews> doInBackground(Void... voids) {
        return fetchWikiArticles();
    }

    @Override
    protected void onPostExecute(List<WikiNews> wikiNewsList) {
        super.onPostExecute(wikiNewsList);
        if (listener != null) {
            listener.onWikiArticlesFetched(wikiNewsList);
        }
    }

    public static List<WikiNews> fetchWikiArticles() {
        List<WikiNews> wikiNewsList = new ArrayList<>();
        Wiki wiki = new Wiki.Builder().build();

        List<String> randomPages = wiki.getRandomPages(10, NS.MAIN); // Fetch 10 random pages
        for (String title : randomPages) {
            String pageText = wiki.getPageText(title);
            if (pageText != null) {
                String url = "https://en.wikipedia.org/wiki/" + title;
                wikiNewsList.add(new WikiNews(title, pageText, url));
            }
        }

        return wikiNewsList;
    }
}
