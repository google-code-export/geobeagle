
package com.google.code.geobeagle.intents;

import com.google.code.geobeagle.ResourceProvider;
import com.google.code.geobeagle.ui.ContentSelector;
import com.google.code.geobeagle.ui.GetCoordsToast;
import com.google.code.geobeagle.ui.MyLocationProvider;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;

public class IntentStarterLocation implements IntentStarter {
    private final Activity mActivity;
    private final GetCoordsToast mGetCoordsToast;
    private final IntentFactory mIntentFactory;
    private final MyLocationProvider mMyLocationProvider;
    private final ResourceProvider mResourceProvider;
    private final int mUriId;
    private ContentSelector mContentSelector;

    public IntentStarterLocation(Activity activity, ResourceProvider resourceProvider,
            IntentFactory intentFactory, MyLocationProvider myLocationProvider,
            ContentSelector contentSelector, int uriId, GetCoordsToast getCoordsToast) {
        mActivity = activity;
        mGetCoordsToast = getCoordsToast;
        mIntentFactory = intentFactory;
        mMyLocationProvider = myLocationProvider;
        mResourceProvider = resourceProvider;
        mContentSelector = contentSelector;
        mUriId = uriId;
    }

    public void startIntent() {
        final Location location = mMyLocationProvider.getLocation();
        if (location == null)
            return;

        mGetCoordsToast.show();
        mActivity.startActivity(mIntentFactory.createIntent(Intent.ACTION_VIEW, String.format(
                mResourceProvider.getStringArray(mUriId)[mContentSelector.getIndex()], location
                        .getLatitude(), location.getLongitude())));
    }
}
