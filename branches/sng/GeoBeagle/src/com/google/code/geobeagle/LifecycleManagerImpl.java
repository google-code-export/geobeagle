
package com.google.code.geobeagle;

import android.content.SharedPreferences;

public class LifecycleManagerImpl implements LifecycleManager {
    private final GpsControl mGpsControl;
    private final LocationSetter mLocationSetter;
    private final SharedPreferences mPreferences;
    public static final String PREFS_LOCATION = "Location";

    public LifecycleManagerImpl(GpsControl gpsControl, LocationSetter locationSetter,
            SharedPreferences preferences) {
        mGpsControl = gpsControl;
        mLocationSetter = locationSetter;
        mPreferences = preferences;
    }

    public void onPause() {
        mGpsControl.onPause();
        mLocationSetter.save();
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(PREFS_LOCATION, mLocationSetter.getLocation().toString());
        editor.commit();
    }

    public void onResume(ErrorDisplayer errorDisplayer, String initialDestination) {
        mGpsControl.onResume();
        mLocationSetter.load();
        mLocationSetter.setLocation(mPreferences.getString(PREFS_LOCATION, initialDestination),
                errorDisplayer);
    }
}
