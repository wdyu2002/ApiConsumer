package com.devculture.apiconsumer.models;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.devculture.apiconsumer.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Per entry payload from reddit.com/top api endpoint.
 */
@SuppressWarnings("unused")
public class RedditTop {

    // ie. i.redd.it
    private int postId;
    private String domain;
    private String id;
    private String author;
    private String name;
    private int score;
    private boolean over18;
    private String thumbnail;
    private String subredditId;
    private String postHint;
    private boolean isSelf;
    private boolean hideScore;
    private String permalink;
    private long created;
    private String url;
    private String title;
    private long createdUtc;
    private String linkFlairText;
    private int numComments;
    private String ups;

    public RedditTop(JSONObject jsonObject) throws JSONException {
        JSONObject data = jsonObject.getJSONObject("data");
        domain = data.getString("domain");
        id = data.getString("id");
        author = data.getString("author");
        name = data.getString("name");
        score = data.getInt("score");
        over18 = data.getBoolean("over_18");
        thumbnail = data.getString("thumbnail");
        subredditId = data.getString("subreddit_id");
        postHint = data.getString("post_hint");
        isSelf = data.getBoolean("is_self");
        hideScore = data.getBoolean("hide_score");
        permalink = data.getString("permalink").replace("\\", "");
        created = data.getLong("created") * 1000;
        url = data.getString("url");
        title = data.getString("title");
        createdUtc = data.getLong("created_utc") * 1000;
        linkFlairText = data.getString("link_flair_text");
        numComments = data.getInt("num_comments");
        // convert ups to string.
        int ups = data.getInt("ups");
        if (ups > 1000000) {
            this.ups = Integer.toString(ups / 1000000) + "." + Integer.toString((ups / 100000) % 10) + "m";
        } else if (ups > 1000) {
            this.ups = Integer.toString(ups / 1000) + "." + Integer.toString((ups / 100) % 10) + "k";
        } else {
            this.ups = Integer.toString(ups);
        }
    }

    public String getDomain() {
        return domain;
    }

    public String getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public boolean isOver18() {
        return over18;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getSubredditId() {
        return subredditId;
    }

    public String getPostHint() {
        return postHint;
    }

    public boolean isSelf() {
        return isSelf;
    }

    public boolean isHideScore() {
        return hideScore;
    }

    public String getPermalink() {
        return permalink;
    }

    public long getCreated() {
        return created;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public long getCreatedUtc() {
        return createdUtc;
    }

    public String getLinkFlairText() {
        return linkFlairText;
    }

    public int getNumComments() {
        return numComments;
    }

    public String getUps() {
        return ups;
    }

    public String getPostedDate() {
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
        return dt.format(new Date(createdUtc));
    }

    /**
     * Returns number of hours ago this post was created.
     */
    public String getPostedHoursAgo() {
        return Integer.toString((int) (System.currentTimeMillis() - createdUtc) / 3600000);
    }

    /**
     * Binds to app:thumbnail=@{reddit.thumbnail}
     * Passes reddit.thumbnail into this method as the 2nd parameter.
     *
     * @param view      The imageview that specifies the app:thumbnail attribute.
     * @param thumbnail The thumbnail url.
     */
    @BindingAdapter({"bind:reddit_thumbnail"})
    public static void loadImage(ImageView view, String thumbnail) {
        Picasso picasso = Picasso.with(view.getContext());
        // picasso.setIndicatorsEnabled(true);
        picasso.load(thumbnail)
                .placeholder(R.mipmap.ic_launcher)
                .into(view);
    }
}
