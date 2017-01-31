package com.devculture.apiconsumer.controllers.reddit;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.devculture.apiconsumer.R;
import com.devculture.apiconsumer.controllers.BaseActivity;
import com.devculture.apiconsumer.controllers.BaseFragment;

import java.util.Map;

public class RedditTopDetailActivity extends BaseActivity implements BaseFragment.Listener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reddit_detail);

        // create detail fragment on initial launch of this activity only.
        // pass throught he 'reddit' bundle arg to the detail fragment.
        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putString("reddit", getIntent().getStringExtra("reddit"));

            RedditTopDetailFragment fragment = new RedditTopDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.reddit_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public void onFragmentEvent(Class<? extends Fragment> clss, String eventName, Map<String, Object> data) {

    }
}
