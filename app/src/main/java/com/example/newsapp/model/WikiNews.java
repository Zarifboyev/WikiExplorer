package com.example.newsapp.model;

public class WikiNews {
    private String title;
    private String article_text;
    private String link_article;

    private int mImageResourceId;


    public WikiNews(String title, String article_text, String link_article) {
        this.title = title;
        this.article_text = article_text;
        this.link_article = link_article;
        this.mImageResourceId = mImageResourceId;
    }

    public int getImageResourceId() {
        return mImageResourceId;
    }

    public void setImageResourceId(int mImageResourceId) {
        this.mImageResourceId = mImageResourceId;
    }

    public String getTitle() {
        return title;
    }

    public String getArticle_text() {
        return article_text;
    }

    public String getLink_article() {
        return link_article;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public void setArticle_text(String article_text) {
        this.article_text = article_text;
    }

    public void setLink_article(String link_article) {
        this.link_article = link_article;
    }



}
