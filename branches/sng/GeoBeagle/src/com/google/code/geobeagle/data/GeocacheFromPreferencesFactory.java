
package com.google.code.geobeagle.data;

import com.google.code.geobeagle.data.di.GeocacheFactory;

import android.content.SharedPreferences;

public class GeocacheFromPreferencesFactory {
    private final GeocacheFactory mGeocacheFactory;

    public GeocacheFromPreferencesFactory(GeocacheFactory geocacheFactory) {
        mGeocacheFactory = geocacheFactory;
    }

    public Geocache create(SharedPreferences preferences) {
        return mGeocacheFactory.create(preferences.getInt("contentSelectorIndex", 1), preferences
                .getString("id", "GCMEY7"), preferences.getString("name", "Google Falls"),
                preferences.getFloat("latitude", 37.42235f), preferences.getFloat("longitude",
                        -122.082217f));
    }
}
