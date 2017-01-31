package com.devculture.apiconsumer.ui.reddit;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.devculture.apiconsumer.R;
import com.devculture.apiconsumer.adapters.RedditTopAdapter;
import com.devculture.apiconsumer.http.reddit.RedditClient;
import com.devculture.apiconsumer.http.reddit.RedditTopRequestListener;
import com.devculture.apiconsumer.models.RedditTop;
import com.devculture.apiconsumer.ui.BaseActivity;
import com.devculture.widget.EndlessScrollListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

/**
 * Activity in charge of retrieving list of top reddits. When appropriate, sends just enough
 * data to the detail fragment/activity such that it can display the reddit details.
 */
public class RedditTopListActivity extends BaseActivity implements RedditTopAdapter.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeLayout;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.reddit_list)
    RecyclerView recyclerView;

    private EndlessScrollListener scroller;

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean isTwoPane;

    /**
     * Map holding onto the previous (before) & next (after) hashes for pagination.
     */
    private Map<String, String> pagination = new HashMap<>();

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
        toolbar.setTitle(getTitle());

        // initialize reddit client.
        reddit = new RedditClient(this);

        // set up the swipe refresher.
        swipeLayout.setEnabled(true);
        swipeLayout.setOnRefreshListener(this);

        // hook up scroller.
        scroller = new EndlessScrollListener((LinearLayoutManager) recyclerView.getLayoutManager()) {
            @Override
            public void onLoadMore(int page, int totalRedditCount, RecyclerView view) {
                loadMoreReddits(null, pagination.get("after"), page, totalRedditCount);
            }
        };

        // hook up the recyclerView.
        redditAdapter = new RedditTopAdapter(this, this);
        recyclerView.setAdapter(redditAdapter);
        recyclerView.addOnScrollListener(scroller);

        // check for device configuration.
        if (findViewById(R.id.reddit_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            isTwoPane = true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // make initial call to fetch top reddits.
        loadMoreReddits(null, null, 0, 0);
    }

    /**
     * Loads more reddits from the api.
     *
     * @param before           A hash provided to the server to request the previous set (page) of reddits.
     * @param after            A hash provided to the server to request the following set (page) of reddits.
     * @param page             Unused page.
     * @param totalRedditCount Number of reddits that have actually been loaded from the server.
     */
    private void loadMoreReddits(String before, String after, int page, int totalRedditCount) {
        reddit.asyncGetTopReddits(before, after, totalRedditCount, 25, new RedditTopRequestListener() {
            @Override
            public void onTopRequestSuccess(JSONObject response) {
                RedditTop[] list = null;
                try {
                    JSONObject data = response.getJSONObject("data");
                    pagination.put("before", data.getString("before"));
                    pagination.put("after", data.getString("after"));
                    JSONArray array = data.getJSONArray("children");
                    list = new RedditTop[array.length()];
                    for (int i = 0; i < array.length(); i++) {
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

    @Override
    public void onRefresh() {
        scroller.resetState();
        redditAdapter.getDataProvider().removeAll();
        loadMoreReddits(null, null, 0, 0);
    }

    @Override
    public void onItemClicked(RedditTop reddit) {

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
