package com.devculture.apiconsumer.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.devculture.apiconsumer.R;
import com.devculture.apiconsumer.databinding.CardAvailableApiBinding;
import com.devculture.apiconsumer.dataproviders.DataProvider;
import com.devculture.apiconsumer.models.Api;

/**
 * RecyclerView adapter that maps the Api DataProvider (model) to a standard
 * RecyclerView.ViewHolder (view).
 */
public final class AvailableApiAdapter extends DataProviderAdapter<DataProvider<Api>, RecyclerView.ViewHolder> {

    /**
     * Listener interface for card item click action.
     */
    public interface OnItemClickListener {
        void onItemClicked(Api api);
    }

    /**
     * (#UseDataBinding) Handler class for card item click action. Uses data-binding to notify any
     * potential OnItemClickListeners assigned to this Adapter.
     */
    public class EventHandler {
        public void onItemClick(Api api) {
            if (listener != null) {
                listener.onItemClicked(api);
            }
        }
    }

    private CardAvailableApiBinding binding;
    private OnItemClickListener listener;
    private EventHandler handler = new EventHandler();

    @SuppressWarnings("unused")
    public AvailableApiAdapter(Context context) {
        super(new DataProvider<>(context, R.array.available_api, Api.class));
    }

    public AvailableApiAdapter(Context context, OnItemClickListener listener) {
        super(new DataProvider<>(context, R.array.available_api, Api.class));
        setListener(listener);
    }

    @SuppressWarnings("WeakerAccess")
    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.card_available_api, parent, false);
        binding.setHandler(handler);
        return new RecyclerView.ViewHolder(binding.groupView) {
        };
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        binding.setApi(getDataProvider().get(position));
    }
}
