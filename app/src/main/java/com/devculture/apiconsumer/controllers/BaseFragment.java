package com.devculture.apiconsumer.controllers;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.Map;

import butterknife.ButterKnife;

@SuppressWarnings("unused")
public abstract class BaseFragment extends Fragment {

    public interface Listener {

        /**
         * Allows the fragment to notify its Activity Listener that an event occurred, and pass any
         * necessary data back to the Activity to be consumed or passed onto the next fragment.
         *
         * @param clss      Indicator for which fragment fired the event.
         * @param eventName Fragment event that occurred.
         * @param data      Data passed between fragments, or from fragment to the activity as part of
         *                  the notification.
         */
        void onFragmentEvent(Class<? extends Fragment> clss, String eventName, Map<String, Object> data);
    }

    private Listener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // attach listener.
        if (context instanceof Listener) {
            listener = (Listener) context;
        } else {
            // optional throw.
            throw new ClassCastException(String.format("The activity class %s is expected to implement BaseFragment.Listener", context.getClass().getSimpleName()));
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // detach listener.
        listener = null;
    }

    /**
     * Pass-through method allowing concrete sub-classes of this BaseFragment to notify its Activity
     * Listener that an event occurred, and pass any necessary data back to the Activity to be
     * consumed or passed onto the next fragment.
     *
     * @param eventName Fragment event that occurred.
     * @param data      Data passed between fragments, or from fragment to the activity as part of the
     *                  notification.
     */
    protected void onFragmentEvent(String eventName, Map<String, Object> data) {
        if (listener != null) {
            listener.onFragmentEvent(getClass(), eventName, data);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }
}
