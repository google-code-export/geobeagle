
package com.google.code.geobeagle.intents;

import com.google.code.geobeagle.Destination;
import com.google.code.geobeagle.ui.LocationSetter;

import android.content.Context;
import android.content.Intent;

public class IntentStarterRadar implements IntentStarter {
    private final Context mContext;
    private final IntentFactory mIntentFactory;
    private final LocationSetter mLocationSetter;

    public IntentStarterRadar(Context context, IntentFactory intentFactory,
            LocationSetter locationSetter) {
        mIntentFactory = intentFactory;
        mContext = context;
        mLocationSetter = locationSetter;
    }


    public void startIntent() {
        final Destination destination = mLocationSetter.getDestination();
        final Intent intent = mIntentFactory
                .createIntent("com.google.android.radar.SHOW_RADAR");
        intent.putExtra("latitude", (float)destination.getLatitude());
        intent.putExtra("longitude", (float)destination.getLongitude());
        mContext.startActivity(intent);
    }
}
