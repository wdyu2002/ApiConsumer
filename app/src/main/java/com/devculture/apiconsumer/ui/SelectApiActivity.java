package com.devculture.apiconsumer.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.devculture.apiconsumer.R;
import com.devculture.apiconsumer.adapters.AvailableApiAdapter;
import com.devculture.apiconsumer.models.Api;
import butterknife.BindView;

public class SelectApiActivity extends BaseActivity {

    /**
     * Listener for recyclerView's click action.
     */
    AvailableApiAdapter.OnItemClickListener onItemClickListener = new AvailableApiAdapter.OnItemClickListener() {
        @Override
        public void onItemClicked(Api api) {
            pushActivity(api.getActivity(), null);
        }
    };

    @BindView(R.id.api_list) RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_api);

        // instantiation.
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new AvailableApiAdapter(this, onItemClickListener));
    }
}
