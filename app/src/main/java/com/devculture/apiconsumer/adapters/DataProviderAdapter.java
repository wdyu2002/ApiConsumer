package com.devculture.apiconsumer.adapters;

import android.support.v7.widget.RecyclerView;

import com.devculture.apiconsumer.dataproviders.DataProvider;

/**
 * A RecyclerView adapter that maps the DataProvider (model) to the ViewHolder (view).
 *
 * @param <DP> A DataProvider type. This allows the sub-class to access the specific data struct.
 * @param <VH> A ViewHolder type. This simply mirrors RecyclerView.Adapter's generic interface.
 */
abstract class DataProviderAdapter<DP extends DataProvider, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private final DP dataProvider;

    DataProviderAdapter(DP dataProvider) {
        this.dataProvider = dataProvider;
    }

    final DP getDataProvider() {
        return dataProvider;
    }

    @Override
    public final int getItemCount() {
        return dataProvider.size();
    }
}
