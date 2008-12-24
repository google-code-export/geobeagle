
package com.google.code.geobeagle;

import com.google.code.geobeagle.intents.ActivityStarter;
import com.google.code.geobeagle.intents.IntentFactory;
import com.google.code.geobeagle.intents.IntentStarterGotoCache;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.view.View;
import android.view.View.OnClickListener;

class OnGotoCacheClickListener implements OnClickListener {
    private final ActivityStarter mActivityStarter;
    private final IntentStarterGotoCache mIntentStarter;
    private final LocationProvider mLocationProvider;
    private final AlertDialog mDlgError;
    private final IntentFactory mIntentFactory;

    public OnGotoCacheClickListener(IntentFactory intentFactory,
            IntentStarterGotoCache intentStarter, ActivityStarter activityStarter,
            LocationProvider locationProvider, AlertDialog dlgError) {
        this.mIntentFactory = intentFactory;
        this.mIntentStarter = intentStarter;
        this.mActivityStarter = activityStarter;
        this.mLocationProvider = locationProvider;
        this.mDlgError = dlgError;
    }

    public void onClick(final View view) {
        try {
            mIntentStarter.startIntent(mActivityStarter, mIntentFactory, new Destination(
                    mLocationProvider.getLocation()));
        } catch (final ActivityNotFoundException e) {
            mDlgError.setMessage("Error: " + e.getMessage()
                    + "\nPlease install the Radar application to use Radar.");
            mDlgError.show();
        } catch (final Exception e) {
            mDlgError.setMessage("Error: " + e.getMessage());
            mDlgError.show();
        }
    }
}
