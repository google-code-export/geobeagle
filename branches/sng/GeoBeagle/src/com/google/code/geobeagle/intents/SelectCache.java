
package com.google.code.geobeagle.intents;

import com.google.code.geobeagle.ResourceProvider;
import com.google.code.geobeagle.ui.GetCoordsToast;
import com.google.code.geobeagle.ui.MyLocationProvider;

import android.content.Intent;
import android.location.Location;

public class SelectCache {
    private final ActivityStarter mActivityStarter;
    private final GetCoordsToast mGetCoordsToast;
    private final IntentFromActionUriFactory mIntentFromActionUriFactory;
    private final MyLocationProvider mMyLocationProvider;
    private final ResourceProvider mResourceProvider;
    private final int mUriId;

    public SelectCache(MyLocationProvider myLocationProvider,
            GetCoordsToast getCoordsToast, ActivityStarter activityStarter,
            IntentFromActionUriFactory intentFromActionUriFactory, ResourceProvider resourceProvider, int uriId) {
        mActivityStarter = activityStarter;
        mGetCoordsToast = getCoordsToast;
        mMyLocationProvider = myLocationProvider;
        mResourceProvider = resourceProvider;
        mIntentFromActionUriFactory = intentFromActionUriFactory;
        mUriId = uriId;
    }

    public void startIntent() {
        final Location location = mMyLocationProvider.getLocation();
        if (location == null)
            return;

        mGetCoordsToast.show();
        mActivityStarter.startActivity(mIntentFromActionUriFactory.createIntent(Intent.ACTION_VIEW,
                String.format(mResourceProvider.getString(mUriId), location.getLatitude(), location
                        .getLongitude())));
    }
}
