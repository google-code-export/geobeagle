
package com.google.code.geobeagle.ui;

import com.google.code.geobeagle.LifecycleManager;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class LocationSetterLifecycleManager implements LifecycleManager {
    public static final String PREFS_LOCATION = "Location";
    private final String mInitialDestination;
    private final LocationSetter mLocationSetter;

    public LocationSetterLifecycleManager(LocationSetter locationSetter, String initialDestination) {
        mLocationSetter = locationSetter;
        mInitialDestination = initialDestination;
    }

    public void onPause(Editor editor) {
        mLocationSetter.saveBookmarks();
        editor.putString(PREFS_LOCATION, mLocationSetter.getLocation().toString());
    }

    public void onResume(SharedPreferences preferences, ErrorDisplayer errorDisplayer) {
        mLocationSetter.readBookmarks();
        mLocationSetter.setLocation(preferences.getString(PREFS_LOCATION, mInitialDestination),
                errorDisplayer);
    }

}
