package com.devculture.apiconsumer.http.reddit;

import org.json.JSONObject;

public interface RedditTopRequestListener extends RedditListener {
    void onTopRequestSuccess(JSONObject response);
}
