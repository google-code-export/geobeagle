
package com.google.code.geobeagle;

import android.content.Intent;

class NearestCachesIntentStarter implements IntentStarter {
    private final GetCoordsToast mGetCoordsToast;
    private final ResourceProvider mResourceProvider;

    public NearestCachesIntentStarter(GetCoordsToast getCoordsToast,
            ResourceProvider resourceProvider) {
        mGetCoordsToast = getCoordsToast;
        mResourceProvider = resourceProvider;
    }

    public void startIntent(ActivityStarter activityStarter, IntentFactory intentFactory,
            Destination destination) {
        mGetCoordsToast.show();

        activityStarter.startActivity(intentFactory.createIntent(Intent.ACTION_VIEW, String.format(
                mResourceProvider.getString(R.string.nearest_caches_url),
                destination.getLatitude(), destination.getLongitude())));
    }
}
