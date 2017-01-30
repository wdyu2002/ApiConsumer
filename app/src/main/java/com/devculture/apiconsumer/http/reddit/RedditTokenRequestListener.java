package com.devculture.apiconsumer.http.reddit;

public interface RedditTokenRequestListener extends RedditListener {
    void onTokenRequestSuccess(String token);
}
