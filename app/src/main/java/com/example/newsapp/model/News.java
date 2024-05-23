package com.example.newsapp.model;

public class News {
    private final String mTitle;
    private final String mType;
    private final String mDate;
    private final String mSection;
    private final String mUrl;
    private final String author;


    public News(String title, String type, String date, String section, String url, String author) {
        this.mTitle = title;
        this.mType = type;
        this.mDate = date;
        this.mSection = section;
        this.mUrl = url;
        this.author = author;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getType() {
        return mType;
    }

    public String getDate() {
        return mDate;
    }

    public String getSection() {
        return mSection;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getAuthor() {
        return author;
    }
}
