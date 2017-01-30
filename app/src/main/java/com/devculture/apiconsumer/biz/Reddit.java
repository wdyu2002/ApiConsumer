package com.devculture.apiconsumer.biz;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import org.json.JSONException;
import org.json.JSONObject;

import com.devculture.apiconsumer.http.RestClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.UUID;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.message.BasicHeader;

@SuppressWarnings({"unused", "WeakerAccess", "FieldCanBeLocal"})
public class Reddit {

    public enum Event {
        TOKEN_RECEIVED,
        TOKEN_FAIL,
        TOKEN_REVOKED,
        REVOKE_TOKEN_FAIL,
        USERNAME_RECEIVED,
        USERNAME_FAIL,
    }

    public interface Listener {
        void onRedditEvent(Event event, String reasoning);
    }

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
    private Listener listener;
    // private String token;

    public Reddit(Context context) {
        this.context = context;
        prefs = new Prefs(context);
    }

    public Reddit(Context context, Listener listener) {
        this.context = context;
        this.listener = listener;
        prefs = new Prefs(context);
    }

    public Prefs getPrefs() {
        return prefs;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    private void notifyListener(Event event, String reason) {
        if (listener != null) {
            listener.onRedditEvent(event, reason);
        }
    }

    public static String getAuthenticationUrl() {
        return OAUTH_URL + "?client_id=" + CLIENT_ID + "&response_type=code&state=TEST&redirect_uri=" + REDIRECT_URI + "&scope=" + OAUTH_SCOPE;
    }

    /**
     * Async http call to get the reddit token from the api server.
     *
     * @param code Authcode returned from OAuth. Code is saved upon successful token retrieval.
     */
    public void asyncGetToken(final String code) {
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
                    prefs.saveCode(code);
                    prefs.saveToken(token);
                    prefs.saveExpiration(0L); // todo ...
                    notifyListener(Event.TOKEN_RECEIVED, "Reddit token received: " + token);
                } catch (JSONException e) {
                    e.printStackTrace();
                    notifyListener(Event.TOKEN_FAIL, e.getMessage());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                notifyListener(Event.TOKEN_FAIL, "Reddit token fail: " + statusCode);
            }
        });
    }

    /**
     * Async http call to revoke the reddit token for oauth.
     */
    public void asyncRevokeToken() {
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
                    notifyListener(Event.TOKEN_REVOKED, "Reddit token revoked: " + token);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    notifyListener(Event.REVOKE_TOKEN_FAIL, "Reddit revoke token fail: " + statusCode);
                }
            });
        }
    }

    /**
     * Async http call to get the reddit username.
     */
    public void asyncGetUsername() {
        final String token = prefs.getToken();
        if (token.length() > 0) {
            // build user-agent.
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

            // make request for username.
            RestClient restClient = new RestClient(context);
            // restClient.setBasicAuth(CLIENT_ID, CLIENT_SECRET);
            restClient.get("https://oauth.reddit.com/api/v1/me", headers, null, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        final String username = response.getString("name");
                        prefs.saveUsername(username);
                        notifyListener(Event.USERNAME_RECEIVED, "Reddit username received: " + username);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        notifyListener(Event.USERNAME_FAIL, e.getMessage());
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    notifyListener(Event.USERNAME_FAIL, "Reddit username fail: " + statusCode);
                }
            });
        } else {
            // fail. missing valid token.
            notifyListener(Event.USERNAME_FAIL, "Reddit username fail: no token");
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
