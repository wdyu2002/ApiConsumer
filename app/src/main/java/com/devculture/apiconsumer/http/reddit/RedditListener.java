package com.devculture.apiconsumer.http.reddit;

public interface RedditListener {
    void onFail(int statusCode, String reason);
}
