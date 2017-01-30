package com.devculture.apiconsumer.http.reddit;

public interface RedditUsernameRequestListener extends RedditListener {
    void onUsernameRequestSuccess(String username);
}
