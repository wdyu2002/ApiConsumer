package com.devculture.apiconsumer.ui.reddit;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import android.support.v7.app.ActionBar;

import com.devculture.apiconsumer.R;

import com.devculture.apiconsumer.adapters.RedditTopAdapter;
import com.devculture.apiconsumer.http.reddit.RedditClient;
import com.devculture.apiconsumer.http.reddit.RedditTopRequestListener;
import com.devculture.apiconsumer.models.RedditTop;
import com.devculture.apiconsumer.ui.BaseActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Activity in charge of retrieving list of top reddits. When appropriate, sends just enough
 * data to the detail fragment/activity such that it can display the reddit details.
 */
public class RedditTopListActivity extends BaseActivity {

    RedditTopAdapter.OnItemClickListener listener = new RedditTopAdapter.OnItemClickListener() {
        @Override
        public void onItemClicked(RedditTop reddit) {
            Log.e("RedditTop", "item clicked");
        }
    };

    /**
     * Butter-knife.
     */
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.reddit_list) RecyclerView recyclerView;

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    /**
     * The reddit client.
     */
    private RedditClient reddit;
    private RedditTopAdapter redditAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reddit_list);

        // hook up the toolbar.
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // initialize reddit client.
        reddit = new RedditClient(this);

        // hook up the recyclerView.
        redditAdapter = new RedditTopAdapter(this, listener);
        recyclerView.setAdapter(redditAdapter);

        // check for device configuration.
        if (findViewById(R.id.reddit_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    int count;
    String before;
    String after;

    @Override
    public void onResume() {
        super.onResume();

        // make a call to fetch top reddits.
        reddit.asyncGetTopReddits(0, 25, new RedditTopRequestListener() {
            @Override
            public void onTopRequestSuccess(JSONObject response) {
                RedditTop[] list = null;
                try {
                    JSONArray array = response.getJSONObject("data").getJSONArray("children");
                    list = new RedditTop[array.length()];
                    for (int i=0; i<array.length(); i++) {
                        list[i] = new RedditTop(array.getJSONObject(i));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    redditAdapter.getDataProvider().addAll(list);
                }
            }

            @Override
            public void onFail(String reason) {

            }
        });
    }

// passing data to detail view.
//    if (mTwoPane) {
//        Bundle arguments = new Bundle();
//        arguments.putString(RedditTopDetailFragment.ARG_ITEM_ID, holder.mItem.id);
//        RedditTopDetailFragment fragment = new RedditTopDetailFragment();
//        fragment.setArguments(arguments);
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.reddit_detail_container, fragment)
//                .commit();
//    } else {
//        Context context = v.getContext();
//        Intent intent = new Intent(context, RedditTopDetailActivity.class);
//        intent.putExtra(RedditTopDetailFragment.ARG_ITEM_ID, holder.mItem.id);
//
//        context.startActivity(intent);
//    }
//
}
