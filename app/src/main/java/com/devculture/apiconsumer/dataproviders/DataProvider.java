package com.devculture.apiconsumer.dataproviders;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Provides data. Caller can either load this DataProvider from xml resource, or
 * by passing in object data in the constructor.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class DataProvider<T> {

    /**
     * Listener interface that is notified when data represented by this data
     * provider is changed.
     */
    public interface Listener {
        void onDataSetChanged(DataProvider dataProvider);
    }

    private final List<T> dataObjects;
    private Listener listener;

    /**
     * Create a DataProvider from xml resource.
     *
     * @param context       Context
     * @param strArrayResId String-array resource of valid JSON to be converted
     *                      into Data objects.
     * @param dataClass     EJB class equivalent of the valid JSON.
     */
    public DataProvider(Context context, int strArrayResId, Class<T> dataClass) {
        this.dataObjects = new ArrayList<>();

        // load dataObjects from resource file.
        String[] jsonStringItems = context.getResources().getStringArray(strArrayResId);
        for (String jsonStringItem : jsonStringItems) {
            try {
                Gson gson = new GsonBuilder().serializeNulls().create();
                dataObjects.add(gson.fromJson(jsonStringItem, dataClass));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Create a DataProvider from provided data objects.
     *
     * @param dataObjects Data objects.
     */
    @SafeVarargs
    public DataProvider(T... dataObjects) {
        this.dataObjects = new ArrayList<>(Arrays.asList(dataObjects));
    }

    /**
     * Add additional data objects to this data provider.
     *
     * @param dataObjects Data objects.
     */
    @SafeVarargs
    public final void addAll(T... dataObjects) {
        if (dataObjects != null) {
            this.dataObjects.addAll(Arrays.asList(dataObjects));

            // notify data set changed.
            if (listener != null) {
                listener.onDataSetChanged(this);
            }
        }
    }

    public T get(int index) {
        return dataObjects.get(index);
    }

    public T getData(int index) {
        return dataObjects.get(index);
    }

    public Listener getListener() {
        return listener;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public int size() {
        return dataObjects.size();
    }
}
