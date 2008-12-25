
package com.google.code.geobeagle.intents;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.ResourceProvider;
import com.google.code.geobeagle.ui.GetCoordsToast;
import com.google.code.geobeagle.ui.MyLocationProvider;

public class SelectCacheFromNearestCaches extends SelectCacheBase {

    public SelectCacheFromNearestCaches(GetCoordsToast getCoordsToast,
            ResourceProvider resourceProvider, MyLocationProvider myLocationProvider) {
        super(getCoordsToast, resourceProvider, myLocationProvider);
    }

    public void startIntent(ActivityStarter activityStarter, IntentFactory intentFactory) {
        startIntent(activityStarter, intentFactory, R.string.nearest_caches_url);
    }
}
