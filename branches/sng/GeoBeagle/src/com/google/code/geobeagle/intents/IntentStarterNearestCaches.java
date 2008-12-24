
package com.google.code.geobeagle.intents;

import com.google.code.geobeagle.GetCoordsToast;
import com.google.code.geobeagle.MyLocationProvider;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.ResourceProvider;

import android.content.Intent;
import android.location.Location;

public class IntentStarterNearestCaches implements IntentStarterSelectCache {
    private final GetCoordsToast mGetCoordsToast;
    private final ResourceProvider mResourceProvider;
    private final MyLocationProvider mMyLocationProvider;

    public IntentStarterNearestCaches(GetCoordsToast getCoordsToast,
            ResourceProvider resourceProvider, MyLocationProvider myLocationProvider) {
        mGetCoordsToast = getCoordsToast;
        mResourceProvider = resourceProvider;
        mMyLocationProvider = myLocationProvider;
    }

    public void startIntent(ActivityStarter activityStarter, IntentFactory intentFactory) {
        Location location = mMyLocationProvider.getLocation();
        if (location == null)
            return;
        mGetCoordsToast.show();
        activityStarter.startActivity(intentFactory.createIntent(Intent.ACTION_VIEW, String.format(
                mResourceProvider.getString(R.string.nearest_caches_url), location.getLatitude(),
                location.getLongitude())));
    }
}
