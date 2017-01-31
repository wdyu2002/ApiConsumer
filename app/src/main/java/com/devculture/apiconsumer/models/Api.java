package com.devculture.apiconsumer.models;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.devculture.apiconsumer.R;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;

@SuppressWarnings("unused")
public class Api {
    private String title;
    private String url;
    private String thumbnail;
    private String activity;
    private String rating;

    public Api(String title, String url, String thumbnail, String activity, String rating) {
        this.title = title;
        this.url = url;
        this.thumbnail = thumbnail;
        this.activity = activity;
        this.rating = rating;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getActivity() {
        return activity;
    }

    public String getRating() {
        return rating;
    }

    /**
     * Binds to app:thumbnail=@{api.thumbnail}
     * Passes api.thumbnail into this method as the 2nd parameter.
     *
     * @param view      The imageview that specifies the app:thumbnail attribute.
     * @param thumbnail The thumbnail local resource file name.
     */
    @BindingAdapter({"bind:api_thumbnail"})
    public static void loadImage(ImageView view, String thumbnail) {
        // retrieve resourceId from thumbnail (local resource file name).
        try {
            Field idField = R.drawable.class.getDeclaredField(thumbnail);
            Picasso.with(view.getContext())
                    .load(idField.getInt(idField))
                    .placeholder(R.mipmap.ic_launcher)
                    .into(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
