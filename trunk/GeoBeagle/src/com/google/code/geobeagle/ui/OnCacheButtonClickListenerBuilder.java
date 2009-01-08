
package com.google.code.geobeagle.ui;

import com.google.code.geobeagle.intents.IntentStarter;

import android.app.Activity;
import android.widget.Button;

public class OnCacheButtonClickListenerBuilder {
    private final ErrorDisplayer mErrorDisplayer;
    private final Activity mContext;

    public OnCacheButtonClickListenerBuilder(Activity context, ErrorDisplayer errorDisplayer) {
        mErrorDisplayer = errorDisplayer;
        mContext = context;
    }

    public void set(int id, IntentStarter intentStarter, String errorString) {
        ((Button)mContext.findViewById(id)).setOnClickListener(new CacheButtonOnClickListener(
                intentStarter, mErrorDisplayer, errorString));
    }
}
