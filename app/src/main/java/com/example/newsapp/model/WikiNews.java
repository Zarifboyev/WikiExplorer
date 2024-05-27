package com.example.newsapp.model;

public class WikiNews {
    private String title;
    private String article_text;
    private String link_article;

    private String imageUrl; // New field for image URL



    public WikiNews(String title, String article_text, String link_article, String imageUrl) {
        this.title = title;
        this.article_text = article_text;
        this.link_article = link_article;
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }



    public void setImageResourceId(String mImageUrl) {
        this.imageUrl = mImageUrl;
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
