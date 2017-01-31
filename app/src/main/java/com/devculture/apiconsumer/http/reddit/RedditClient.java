package com.devculture.apiconsumer.http.reddit;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import org.json.JSONException;
import org.json.JSONObject;

import com.devculture.apiconsumer.http.RestClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.Locale;
import java.util.UUID;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.message.BasicHeader;

@SuppressWarnings({"unused", "WeakerAccess", "FieldCanBeLocal"})
public class RedditClient {

    private static final String BASE_URL = "https://www.reddit.com/api/v1/";
    private static final String OAUTH_URL = "https://www.reddit.com/api/v1/authorize";

    private static String CLIENT_ID = "NztNyDVQ_utRUQ";
    private static String CLIENT_SECRET = "";
    private static String REDIRECT_URI = "http://danny-yu.com";
    private static String GRANT_TYPE = "https://oauth.reddit.com/grants/installed_client";
    private static String GRANT_TYPE2 = "authorization_code";
    private static String TOKEN_URL = "access_token";
    private static String OAUTH_SCOPE = "read";
    private static String DURATION = "permanent";
    private static String DEVICE_ID = UUID.randomUUID().toString();

    private final Context context;
    private final Prefs prefs;

    public RedditClient(Context context) {
        this.context = context;
        prefs = new Prefs(context);
    }

    public Prefs getPrefs() {
        return prefs;
    }

    public static String getAuthenticationUrl() {
        return OAUTH_URL + "?client_id=" + CLIENT_ID + "&response_type=code&state=TEST&redirect_uri=" + REDIRECT_URI + "&scope=" + OAUTH_SCOPE;
    }

    /**
     * Async http call to get the reddit token from the api server.
     *
     * @param code Authcode returned from oauth. Code is saved upon successful token retrieval.
     */
    public void asyncGetToken(final String code, final RedditTokenRequestListener listener) {
        // prep url.
        RequestParams requestParams = new RequestParams();
        requestParams.put("code", code);
        requestParams.put("grant_type", "authorization_code");
        requestParams.put("redirect_uri", REDIRECT_URI);

        // make request for token.
        RestClient restClient = new RestClient(context);
        restClient.setBasicAuth(CLIENT_ID, CLIENT_SECRET);
        restClient.post(BASE_URL + "access_token", requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    final String token = response.getString("access_token");
                    final int expires = response.getInt("expires_in");
                    prefs.saveCode(code);
                    prefs.saveToken(token);
                    prefs.saveExpiration(System.currentTimeMillis() + expires * 1000);

                    if (listener != null) {
                        listener.onTokenRequestSuccess(token);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                    if (listener != null) {
                        listener.onFail(500, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (listener != null) {
                    listener.onFail(statusCode, "Reddit token fail: " + statusCode);
                }
            }
        });
    }

    /**
     * Async http call to revoke the reddit token for oauth.
     */
    public void asyncRevokeToken(final RedditListener listener) {
        final String token = prefs.getToken();
        if (token.length() > 0) {
            RequestParams requestParams = new RequestParams();
            requestParams.put("token", token);
            requestParams.put("token_type_hint", "access_token");

            // make request for token revocation.
            RestClient restClient = new RestClient(context);
            restClient.setBasicAuth(CLIENT_ID, CLIENT_SECRET);
            restClient.post(BASE_URL + "revoke_token", requestParams, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    prefs.removeToken();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    if (listener != null) {
                        listener.onFail(statusCode, "Reddit revoke token fail: " + statusCode);
                    }
                }
            });
        }
    }

    /**
     * Async http call to get the reddit username.
     */
    public void asyncGetUsername(final RedditUsernameRequestListener listener) {
        final String token = prefs.getToken();
        if (token.length() > 0) {
            RestClient restClient = new RestClient(context);
            restClient.get("https://oauth.reddit.com/api/v1/me", getRequestHeaders(), null, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        final String username = response.getString("name");
                        prefs.saveUsername(username);

                        if (listener != null) {
                            listener.onUsernameRequestSuccess(username);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();

                        if (listener != null) {
                            listener.onFail(500, "Reddit username fail: " + e.getMessage());
                        }
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    if (listener != null) {
                        listener.onFail(statusCode, "Reddit username fail: " + statusCode);
                    }
                }
            });
        } else {
            // fail. missing valid token.
            if (listener != null) {
                listener.onFail(401, "Reddit username fail: no token");
            }
        }
    }

    /**
     * Async http call to get the top posts on reddit by count & limit.
     *
     * @param before Start offset. Use in conjunction with count to retrieve the previous sequence of posts.
     * @param after After post id. Use in conjunction with count to retrieve the next sequence of posts.
     * @param count Start offset.
     * @param limit Result limit, defaults to 25 usually. Max is 100.
     */
    public void asyncGetTopReddits(String before, String after, int count, int limit, final RedditTopRequestListener listener) {
        final String token = prefs.getToken();
        if (token.length() > 0) {
            String url;
            if (before != null) {
                url = String.format(Locale.getDefault(), "https://oauth.reddit.com/top?before=%s&count=%d&limit=%d", before, count, limit);
            } else if (after != null) {
                url = String.format(Locale.getDefault(), "https://oauth.reddit.com/top?after=%s&count=%d&limit=%d", after, count, limit);
            } else {
                url = String.format(Locale.getDefault(), "https://oauth.reddit.com/top?count=%d&limit=%d", count, limit);
            }

            RestClient restClient = new RestClient(context);
            restClient.get(url, getRequestHeaders(), null, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    if (listener != null) {
                        listener.onTopRequestSuccess(response);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    if (listener != null) {
                        listener.onFail(statusCode, "Reddit top posts fail: " + statusCode);
                    }
                }
            });
        } else {
            // fail. missing valid token.
            if (listener != null) {
                listener.onFail(401, "Reddit top posts fail: no token");
            }
        }
    }

    private Header[] getRequestHeaders() {
        final String token = prefs.getToken();
        if (token.length() > 0) {
            String userAgent = "DefaultUserAgent/1.0 by dyu";
            try {
                PackageManager packageManager = context.getPackageManager();
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getApplicationInfo().packageName, 0);
                String appVersion = packageManager.getPackageInfo(context.getPackageName(), 0).versionName;
                String appName = applicationInfo != null ? packageManager.getApplicationLabel(applicationInfo).toString() : "NamelessApp";
                userAgent = String.format("%s/%s by %s", appName, appVersion, "dyu");
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            // build headers.
            Header[] headers = new Header[2];
            headers[0] = new BasicHeader("User-Agent", userAgent);
            headers[1] = new BasicHeader("Authorization", "bearer " + token);
            return headers;
        } else {
            // note: no token = no headers.
            return null;
        }
    }

    /**
     * SharedPreference helper.
     */

    public static class Prefs {

        private final SharedPreferences prefs;

        public Prefs(Context context) {
            prefs = context.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
        }

        public void saveToken(String token) {
            prefs.edit().putString("token", token).apply();
        }

        public String getToken() {
            return prefs.getString("token", "");
        }

        public void removeToken() {
            prefs.edit().remove("token").apply();
        }

        public void saveExpiration(long expiration) {
            prefs.edit().putLong("expiration", expiration).apply();
        }

        public long getExpiration() {
            return prefs.getLong("expiration", 0L);
        }

        public void saveCode(String code) {
            prefs.edit().putString("code", code).apply();
        }

        public String getCode() {
            return prefs.getString("code", "");
        }

        public void saveUsername(String username) {
            prefs.edit().putString("username", username).apply();
        }

        public String getUsername() {
            return prefs.getString("username", "");
        }
    }

}
