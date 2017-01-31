package com.devculture.apiconsumer.controllers.reddit;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.devculture.apiconsumer.R;
import com.devculture.apiconsumer.models.RedditTop;
import com.devculture.apiconsumer.controllers.BaseFragment;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RedditTopDetailFragment extends BaseFragment {

    @BindView(R.id.webview)
    WebView webView;

    private RedditTop reddit;

    public RedditTopDetailFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey("reddit")) {
            Gson gson = new GsonBuilder().serializeNulls().create();
            reddit = gson.fromJson(getArguments().getString("reddit"), RedditTop.class);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.reddit_detail, container, false);
        ButterKnife.bind(this, rootView);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("http://reddit.com" + reddit.getPermalink());
        return rootView;
    }
}
