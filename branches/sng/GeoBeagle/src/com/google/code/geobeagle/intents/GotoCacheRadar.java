
package com.google.code.geobeagle.intents;

import com.google.code.geobeagle.Destination;

import android.content.Intent;

public class GotoCacheRadar implements GotoCache {
    public void startIntent(ActivityStarter activityStarter, IntentFactory intentFactory,
            Destination destination) {
        final Intent intent = intentFactory.createIntent("com.google.android.radar.SHOW_RADAR");
        intent.putExtra("latitude", (float)destination.getLatitude());
        intent.putExtra("longitude", (float)destination.getLongitude());
        activityStarter.startActivity(intent);
    }
}
