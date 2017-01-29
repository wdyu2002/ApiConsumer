package com.devculture.apiconsumer.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import butterknife.ButterKnife;

public class BaseActivity extends AppCompatActivity {

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
    }

    /**
     * Push an activity onto the current activity stack with className.
     *
     * @param className The fully qualified className of the activity class to instantiate.
     * @param bundle Bundle data to be passed to the new activity.
     */
    public void pushActivity(String className, Bundle bundle) {
        try {
            // start up the next activity.
            pushActivity(Class.forName(className), null);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Push an activity onto the current activity stack.
     *
     * @param clss The activity class to instantiate.
     * @param bundle Bundle data to be passed to the new activity.
     */
    public void pushActivity(Class<?> clss, Bundle bundle) {
        Intent intent = new Intent(this, clss);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }
}
