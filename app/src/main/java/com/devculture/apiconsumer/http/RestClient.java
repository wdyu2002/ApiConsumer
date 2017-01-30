package com.devculture.apiconsumer.http;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

import cz.msebera.android.httpclient.Header;

/**
 * todo .. Convert to use RetroFit.
 */
@SuppressWarnings("unused")
public class RestClient {

    private final Context context;
    private static AsyncHttpClient client = new AsyncHttpClient();

    public RestClient(Context context) {
        this.context = context;
    }

    public void setBasicAuth(String username, String password) {
        client.setBasicAuth(username, password);
    }

    public void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(url, params, responseHandler);
    }

    public void get(String url, Header[] headers, RequestParams params, ResponseHandlerInterface responseHandler) {
        client.get(context, url, headers, params, responseHandler);
    }

    public void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(url, params, responseHandler);
    }

}
