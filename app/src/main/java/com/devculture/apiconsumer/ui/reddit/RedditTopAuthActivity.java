package com.devculture.apiconsumer.ui.reddit;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.devculture.apiconsumer.R;
import com.devculture.apiconsumer.biz.Reddit;
import com.devculture.apiconsumer.ui.BaseActivity;

import butterknife.BindView;

/**
 * This activity is responsible for logging the user in & using oAuth to authenticate the user
 * and save their session token. Ideally, if the session token exists and is not expired, we would
 * skip over this activity & directly enter the next screen.
 */
public class RedditTopAuthActivity extends BaseActivity {

    /**
     * Reddit connection event listener.
     */
    private Reddit.Listener listener = new Reddit.Listener() {
        @Override
        public void onRedditEvent(Reddit.Event event, String reasoning) {
            switch (event) {
                case TOKEN_RECEIVED:
                    pushActivity(RedditTopListActivity.class, null);
                    break;
                case TOKEN_FAIL:
                    // display reason for token request failure.
                    Snackbar.make(webView, reasoning, Snackbar.LENGTH_LONG).setAction("Error", null).show();
                    break;
            }
        }
    };

    @BindView(R.id.webview)
    WebView webView;
    private Reddit reddit;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reddit_top_auth);

        // initialize reddit client.
        reddit = new Reddit(RedditTopAuthActivity.this, listener);

        // initialize the webview.
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(Reddit.getAuthenticationUrl());
        webView.setWebViewClient(new WebViewClient() {

            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Uri uri = Uri.parse(url);
                // only load and display the oauth page.
                // do not load or display the redirect-url.
                if (uri.getHost().contains("reddit.com")) {
                    view.loadUrl(url);
                }
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                // Note: The initial request will be to the oauth page, asking user to grant
                // permission to the application. The subsequent request will be made when the
                // user clicks on 'accept', and its url will contain the auth-code.
                String code = getCodeParameter(Uri.parse(url));
                if (code != null) {
                    reddit.asyncGetToken(code);
                } else if (url.contains("error=access_denied")) {
                    Log.e("Reddit", "Error occurred during oAuth process");
                }
            }

            /**
             * Helper method to parse the authCode from the input uri.
             */
            private String getCodeParameter(Uri uri) {
                try {
                    return uri.getQueryParameter("code");
                } catch (Exception ex) {
                    // safe to ignore.
                }
                return null;
            }
        });
    }
}
