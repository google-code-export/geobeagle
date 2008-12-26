
package com.google.code.geobeagle.intents;

import com.google.code.geobeagle.Destination;

import android.content.Intent;

public class GotoCacheRadar extends GotoCache {
    private final IntentFromActionFactory mIntentFactory;

    public GotoCacheRadar(ActivityStarter activityStarter,
            IntentFromActionFactory intentFromActionFactory) {
        super(activityStarter);
        mIntentFactory = intentFromActionFactory;
    }

    protected Intent createIntent(Destination destination) {
        final Intent intent = mIntentFactory.createIntent("com.google.android.radar.SHOW_RADAR");
        intent.putExtra("latitude", (float)destination.getLatitude());
        intent.putExtra("longitude", (float)destination.getLongitude());
        return intent;
    }
}
