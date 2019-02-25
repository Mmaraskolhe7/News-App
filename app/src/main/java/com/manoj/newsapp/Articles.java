package com.manoj.newsapp;

import org.json.JSONException;
import org.json.JSONObject;

public class Articles {
private JSONObject objectSource;
private Source source;
private String author;
private String title;
private String description;
private  String url;
private String urlToImage;
private String publishedAt;

    public Articles(JSONObject objectSource, String author, String title, String description, String url, String urlToImage, String publishedAt) {
        this.objectSource = objectSource;
        this.author = author;
        this.title = title;
        this.description = description;
        this.url = url;
        this.urlToImage = urlToImage;
        this.publishedAt = publishedAt;
    }

    public void makeSource(){
        try {
            source = new Source(objectSource.getString("id"),objectSource.getString("name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public String getUrlToImage() {
        return urlToImage;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public Source getSource() {
        return source;
    }
}
