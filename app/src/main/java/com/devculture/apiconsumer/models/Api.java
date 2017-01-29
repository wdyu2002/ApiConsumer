package com.devculture.apiconsumer.models;

@SuppressWarnings("unused")
public class Api {
    private String title;
    private String url;
    private String activity;
    private String rating;

    public Api(String title, String url, String activity, String rating) {
        this.title = title;
        this.url = url;
        this.activity = activity;
        this.rating = rating;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}
