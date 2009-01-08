
package com.google.code.geobeagle.intents;

import com.google.code.geobeagle.ui.LocationSetter;

import android.content.Context;
import android.content.Intent;

public class IntentStarterViewUri implements IntentStarter {
    private final Context mContext;
    private final DestinationToUri mDestinationToUri;
    private final IntentFactory mIntentFactory;
    private final LocationSetter mLocationSetter;

    public IntentStarterViewUri(Context context, IntentFactory intentFactory,
            LocationSetter locationSetter, DestinationToUri destinationToUri) {
        mContext = context;
        mDestinationToUri = destinationToUri;
        mIntentFactory = intentFactory;
        mLocationSetter = locationSetter;
    }

    public void startIntent() {
        mContext.startActivity(mIntentFactory.createIntent(Intent.ACTION_VIEW, mDestinationToUri
                .convert(mLocationSetter.getDestination())));
    }
}
