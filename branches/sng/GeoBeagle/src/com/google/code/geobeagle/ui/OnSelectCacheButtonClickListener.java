
package com.google.code.geobeagle.ui;

import com.google.code.geobeagle.intents.ActivityStarter;
import com.google.code.geobeagle.intents.IntentFactory;
import com.google.code.geobeagle.intents.SelectCache;

import android.view.View;
import android.view.View.OnClickListener;

public class OnSelectCacheButtonClickListener implements OnClickListener {

    private final ActivityStarter mActivityStarter;
    private final IntentFactory mIntentFactory;
    private final SelectCache mIntentStarterSelectCache;

    public OnSelectCacheButtonClickListener(IntentFactory intentFactory,
            SelectCache selectCache, ActivityStarter activityStarter,
            LocationSetter locationSetter) {
        mActivityStarter = activityStarter;
        mIntentFactory = intentFactory;
        mIntentStarterSelectCache = selectCache;
    }

    public void onClick(View v) {
        mIntentStarterSelectCache.startIntent(mActivityStarter, mIntentFactory);
    }

}
