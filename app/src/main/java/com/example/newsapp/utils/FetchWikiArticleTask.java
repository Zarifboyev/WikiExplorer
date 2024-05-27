package com.example.newsapp.utils;

import android.os.AsyncTask;
import android.util.Log;

import io.github.fastily.jwiki.core.NS;
import io.github.fastily.jwiki.core.Wiki;
import io.github.fastily.jwiki.dwrap.ImageInfo;

import com.example.newsapp.model.WikiNews;

import org.apache.commons.codec.digest.DigestUtils;

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
        // Handle the fetched articles here
        if (listener != null) {
            listener.onWikiArticlesFetched(wikiNewsList);
        }
    }
    private static String getImageUrl(String fileName) {
        String baseUrl = "https://upload.wikimedia.org/wikipedia/commons/";
        String md5Hash = DigestUtils.md5Hex(fileName.replace("File:", ""));
        String urlPath = String.format("%s/%s/%s", md5Hash.substring(0, 1), md5Hash.substring(0, 2), fileName.replace("File:", "").replace(" ", "_"));
        return baseUrl + urlPath;
    }
    public static List<WikiNews> fetchWikiArticles() {
        List<WikiNews> wikiNewsList = new ArrayList<>();
        Wiki wiki = new Wiki.Builder().build();

        List<String> randomPages = wiki.getRandomPages(10, NS.MAIN); // Fetch 10 random pages
        for (String title : randomPages) {
            // Getting page text and image URL
            List<String> imageInfos = wiki.getImagesOnPage(title);
            if (!imageInfos.isEmpty()) {
                // Extract the first image file name from the page
                String fileName = FileNameExtractor.extractFileName(imageInfos.get(0));
                // Generate URL for the image
                String imageUrl = getImageUrl(fileName);

                // Fetch page text
                String pageText = wiki.getTextExtract(title).replaceAll("\\(.*?\\)", "");

                // Generate Wikipedia URL for the page
                String url = "https://en.wikipedia.org/wiki/" + title;

                // Add the news to the list
                wikiNewsList.add(new WikiNews(title, pageText, url, imageUrl));
            }
        }

        return wikiNewsList;
    }



}
