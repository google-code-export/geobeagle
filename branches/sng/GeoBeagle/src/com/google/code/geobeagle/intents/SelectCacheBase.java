
package com.google.code.geobeagle.intents;

import com.google.code.geobeagle.ResourceProvider;
import com.google.code.geobeagle.ui.GetCoordsToast;
import com.google.code.geobeagle.ui.MyLocationProvider;

import android.content.Intent;
import android.location.Location;

public abstract class SelectCacheBase implements SelectCache {
    protected final GetCoordsToast mGetCoordsToast;
    protected final MyLocationProvider mMyLocationProvider;
    protected final ResourceProvider mResourceProvider;

    public SelectCacheBase(GetCoordsToast getCoordsToast, ResourceProvider resourceProvider,
            MyLocationProvider myLocationProvider) {
        mGetCoordsToast = getCoordsToast;
        mMyLocationProvider = myLocationProvider;
        mResourceProvider = resourceProvider;
    }

    public void startIntent(ActivityStarter activityStarter, IntentFactory intentFactory, int uriId) {
        Location location = mMyLocationProvider.getLocation();
        if (location == null)
            return;

        mGetCoordsToast.show();
        activityStarter.startActivity(intentFactory.createIntent(Intent.ACTION_VIEW, String
                .format(mResourceProvider.getString(uriId), location.getLatitude(), location
                        .getLongitude())));
    }
}
