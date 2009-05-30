
package com.google.code.geobeagle.mainactivity;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.mainactivity.GeocacheFactory.Source;

import android.content.SharedPreferences;

public class GeocacheFromPreferencesFactory {
    private final GeocacheFactory mGeocacheFactory;

    public GeocacheFromPreferencesFactory(GeocacheFactory geocacheFactory) {
        mGeocacheFactory = geocacheFactory;
    }

    public Geocache create(SharedPreferences preferences) {
        Source source = Source.MY_LOCATION;
        int iSource = preferences.getInt("sourceType", -1);
        if (iSource != -1)
            source = mGeocacheFactory.sourceFromInt(iSource);
        return mGeocacheFactory.create(preferences.getString("id", "GCMEY7"), preferences
                .getString("name", "Google Falls"), preferences.getFloat("latitude", 37.42235f),
                preferences.getFloat("longitude", -122.082217f), source, preferences.getString(
                        "sourceName", null));
    }
}
