
package com.google.code.geobeagle;

import android.content.Intent;

class IntentStarterRadar implements IntentStarterGotoCache {
    public void startIntent(ActivityStarter activityStarter, IntentFactory intentFactory,
            Destination destination) {
        final Intent intent = intentFactory.createIntent("com.google.android.radar.SHOW_RADAR");
        intent.putExtra("latitude", (float)destination.getLatitude());
        intent.putExtra("longitude", (float)destination.getLongitude());
        activityStarter.startActivity(intent);
    }
}
