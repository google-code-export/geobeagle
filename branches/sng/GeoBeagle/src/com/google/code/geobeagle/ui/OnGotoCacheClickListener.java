
package com.google.code.geobeagle.ui;

import com.google.code.geobeagle.Destination;
import com.google.code.geobeagle.intents.ActivityStarter;
import com.google.code.geobeagle.intents.GotoCache;

import android.content.ActivityNotFoundException;
import android.view.View;
import android.view.View.OnClickListener;

public class OnGotoCacheClickListener implements OnClickListener {
    private final GotoCache mGotoCache;
    private final LocationProvider mLocationProvider;
    private final ErrorDialog mErrorDialog;

    public OnGotoCacheClickListener(GotoCache intentStarter, ActivityStarter activityStarter,
            LocationProvider locationProvider, ErrorDialog errorDialog) {
        this.mGotoCache = intentStarter;
        this.mLocationProvider = locationProvider;
        this.mErrorDialog = errorDialog;
    }

    public void onClick(final View view) {
        try {
            mGotoCache.startIntent(new Destination(mLocationProvider.getLocation()));
        } catch (final ActivityNotFoundException e) {
            mErrorDialog.show("Error: " + e.getMessage()
                    + "\nPlease install the Radar application to use Radar.");
        } catch (final Exception e) {
            mErrorDialog.show("Error: " + e.getMessage());
        }
    }
}
