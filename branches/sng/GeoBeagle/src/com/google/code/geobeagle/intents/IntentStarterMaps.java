
package com.google.code.geobeagle.intents;

import com.google.code.geobeagle.Destination;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.ResourceProvider;

import android.content.Intent;

public class IntentStarterMaps implements IntentStarterGotoCache {
    private final ResourceProvider mResourceProvider;

    public IntentStarterMaps(ResourceProvider resourceProvider) {
        mResourceProvider = resourceProvider;
    }

    public void startIntent(ActivityStarter activityStarter, IntentFactory intentFactory,
            Destination destination) {
        // "geo:%1$.5f,%2$.5f?name=cachename"
        activityStarter.startActivity(intentFactory.createIntent(Intent.ACTION_VIEW, String.format(
                mResourceProvider.getString(R.string.map_intent), destination.getLatitude(),
                destination.getLongitude(), destination.getDescription())));
    }
}
