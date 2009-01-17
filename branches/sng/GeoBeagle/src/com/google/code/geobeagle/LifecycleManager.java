
package com.google.code.geobeagle;

import com.google.code.geobeagle.ui.ErrorDisplayer;
import com.google.code.geobeagle.ui.LocationSetter;

import android.content.SharedPreferences;

public class LifecycleManager {
    private final GpsLifecycleManager mGpsLifecycleManager;
    private final LocationSetter mLocationSetter;
    private final SharedPreferences mPreferences;
    public static final String PREFS_LOCATION = "Location";

    public LifecycleManager(GpsLifecycleManager gpsLifecycleManager, LocationSetter locationSetter,
            SharedPreferences preferences) {
        mGpsLifecycleManager = gpsLifecycleManager;
        mLocationSetter = locationSetter;
        mPreferences = preferences;
    }

    public void onPause() {
        mGpsLifecycleManager.onPause();
        mLocationSetter.save();
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(PREFS_LOCATION, mLocationSetter.getLocation().toString());
        editor.commit();
    }

    public void onResume(ErrorDisplayer errorDisplayer, String initialDestination) {
        mGpsLifecycleManager.onResume();
        mLocationSetter.load();
        mLocationSetter.setLocation(mPreferences.getString(PREFS_LOCATION, initialDestination),
                errorDisplayer);
    }
}
