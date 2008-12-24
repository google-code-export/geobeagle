
package com.google.code.geobeagle.ui;

import com.google.code.geobeagle.Destination;
import com.google.code.geobeagle.intents.ActivityStarter;
import com.google.code.geobeagle.intents.IntentFactory;
import com.google.code.geobeagle.intents.IntentStarterGotoCache;

import android.content.ActivityNotFoundException;
import android.view.View;
import android.view.View.OnClickListener;

public class OnGotoCacheClickListener implements OnClickListener {
    private final ActivityStarter mActivityStarter;
    private final IntentStarterGotoCache mIntentStarter;
    private final LocationProvider mLocationProvider;
    private final ErrorDialog mErrorDialog;
    private final IntentFactory mIntentFactory;

    public OnGotoCacheClickListener(IntentFactory intentFactory,
            IntentStarterGotoCache intentStarter, ActivityStarter activityStarter,
            LocationProvider locationProvider, ErrorDialog errorDialog) {
        this.mIntentFactory = intentFactory;
        this.mIntentStarter = intentStarter;
        this.mActivityStarter = activityStarter;
        this.mLocationProvider = locationProvider;
        this.mErrorDialog = errorDialog;
    }

    public void onClick(final View view) {
        try {
            mIntentStarter.startIntent(mActivityStarter, mIntentFactory, new Destination(
                    mLocationProvider.getLocation()));
        } catch (final ActivityNotFoundException e) {
            mErrorDialog.show("Error: " + e.getMessage()
                    + "\nPlease install the Radar application to use Radar.");
        } catch (final Exception e) {
            mErrorDialog.show("Error: " + e.getMessage());
        }
    }
}
