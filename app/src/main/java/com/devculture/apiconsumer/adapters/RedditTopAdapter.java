package com.devculture.apiconsumer.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.devculture.apiconsumer.R;
import com.devculture.apiconsumer.databinding.ViewRedditSummaryCardBinding;
import com.devculture.apiconsumer.dataproviders.DataProvider;
import com.devculture.apiconsumer.models.RedditTop;

/**
 * RecyclerView adapter that maps the Api DataProvider (model) to a standard
 * RecyclerView.ViewHolder (view).
 */
public final class RedditTopAdapter extends DataProviderAdapter<DataProvider<RedditTop>, RecyclerView.ViewHolder> {

    /**
     * Listener interface for card item click action.
     */
    @SuppressWarnings("WeakerAccess")
    public interface OnItemClickListener {
        void onItemClicked(RedditTop reddit);
    }

    /**
     * (#UseDataBinding) Handler class for card item click action. Uses data-binding to notify any
     * potential OnItemClickListeners assigned to this Adapter.
     */
    public class EventHandler {
        public void onItemClick(RedditTop reddit) {
            if (listener != null) {
                listener.onItemClicked(reddit);
            }
        }
    }

    private ViewRedditSummaryCardBinding binding;
    private OnItemClickListener listener;
    private EventHandler handler = new EventHandler();

    @SuppressWarnings("unused")
    public RedditTopAdapter(Context context) {
        super(new DataProvider<RedditTop>());
        getDataProvider().setListener(new DataProvider.Listener() {
            @Override
            public void onDataSetChanged(DataProvider dataProvider) {
                notifyDataSetChanged();
            }
        });
    }

    public RedditTopAdapter(Context context, OnItemClickListener listener) {
        this(context);
        setListener(listener);
    }

    @SuppressWarnings("WeakerAccess")
    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.view_reddit_summary_card, parent, false);
        binding.setHandler(handler);
        return new RecyclerView.ViewHolder(binding.groupView) {
        };
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        binding.setReddit(getDataProvider().get(position));
    }
}
