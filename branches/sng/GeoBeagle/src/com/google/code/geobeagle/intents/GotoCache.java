
package com.google.code.geobeagle.intents;

import com.google.code.geobeagle.Destination;

import android.content.Intent;

public abstract class GotoCache {
    private final ActivityStarter mActivityStarter;

    public GotoCache(ActivityStarter activityStarter) {
        mActivityStarter = activityStarter;
    }

    protected abstract Intent createIntent(Destination destination);

    public void startIntent(Destination destination) {
        mActivityStarter.startActivity(createIntent(destination));
    }
}
