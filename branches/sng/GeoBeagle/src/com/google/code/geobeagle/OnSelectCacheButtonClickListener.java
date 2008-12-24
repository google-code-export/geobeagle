
package com.google.code.geobeagle;

import com.google.code.geobeagle.intents.ActivityStarter;
import com.google.code.geobeagle.intents.IntentFactory;
import com.google.code.geobeagle.intents.IntentFactoryImpl;
import com.google.code.geobeagle.intents.IntentStarterSelectCache;

import android.view.View;
import android.view.View.OnClickListener;

public class OnSelectCacheButtonClickListener implements OnClickListener {

    private final ActivityStarter mActivityStarter;
    private final IntentFactory mIntentFactory;
    private final IntentStarterSelectCache mIntentStarterSelectCache;

    public OnSelectCacheButtonClickListener(IntentFactoryImpl intentFactory,
            IntentStarterSelectCache intentStarterSelectCache, ActivityStarter activityStarter,
            LocationSetter locationSetter) {
        mActivityStarter = activityStarter;
        mIntentFactory = intentFactory;
        mIntentStarterSelectCache = intentStarterSelectCache;
    }

    public void onClick(View v) {
        mIntentStarterSelectCache.startIntent(mActivityStarter, mIntentFactory);
    }

}
