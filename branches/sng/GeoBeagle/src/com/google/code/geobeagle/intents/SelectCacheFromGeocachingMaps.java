
package com.google.code.geobeagle.intents;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.ResourceProvider;
import com.google.code.geobeagle.ui.GetCoordsToast;
import com.google.code.geobeagle.ui.MyLocationProvider;

public class SelectCacheFromGeocachingMaps extends SelectCacheBase {

    public SelectCacheFromGeocachingMaps(GetCoordsToast getCoordsToast,
            ResourceProvider resourceProvider, MyLocationProvider myLocationProvider) {
        super(getCoordsToast, resourceProvider, myLocationProvider);
    }

    public void startIntent(ActivityStarter activityStarter, IntentFactory intentFactory) {
        startIntent(activityStarter, intentFactory, R.string.geocaching_maps_url);
    }

}
