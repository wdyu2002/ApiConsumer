package com.devculture.apiconsumer.controllers.reddit;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.devculture.apiconsumer.R;
import com.devculture.apiconsumer.http.reddit.RedditClient;
import com.devculture.apiconsumer.http.reddit.RedditTokenRequestListener;
import com.devculture.apiconsumer.controllers.BaseActivity;

import butterknife.BindView;

/**
 * This activity is responsible for logging the user in & using oAuth to authenticate the user
 * and save their session token. Ideally, if the session token exists and is not expired, we would
 * skip over this activity & directly enter the next screen.
 */
public class RedditTopAuthActivity extends BaseActivity {

    @BindView(R.id.webview)
    WebView webView;
    private RedditClient reddit;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reddit_auth);

        // initialize reddit client.
        reddit = new RedditClient(RedditTopAuthActivity.this);

        // initialize the webview.
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(RedditClient.getAuthenticationUrl());
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
                    reddit.asyncGetToken(code, new RedditTokenRequestListener() {
                        @Override
                        public void onTokenRequestSuccess(String token) {
                            pushActivity(RedditTopListActivity.class, null);
                        }
                        @Override
                        public void onFail(int statusCode, String reason) {
                            Snackbar.make(webView, reason, Snackbar.LENGTH_LONG).setAction("Error", null).show();
                        }
                    });
                } else if (url.contains("error=access_denied")) {
                    Snackbar.make(webView, "oauth access denied", Snackbar.LENGTH_LONG).setAction("Error", null).show();
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
