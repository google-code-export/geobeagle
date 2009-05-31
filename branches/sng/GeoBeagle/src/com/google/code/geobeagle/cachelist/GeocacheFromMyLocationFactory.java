
package com.google.code.geobeagle.cachelist;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.location.LocationControlBuffered;
import com.google.code.geobeagle.mainactivity.GeocacheFactory;
import com.google.code.geobeagle.mainactivity.GeocacheFactory.Source;

import android.location.Location;

public class GeocacheFromMyLocationFactory {
    private final GeocacheFactory mGeocacheFactory;
    private final LocationControlBuffered mLocationControl;

    public GeocacheFromMyLocationFactory(GeocacheFactory geocacheFactory,
            LocationControlBuffered locationControl) {
        mGeocacheFactory = geocacheFactory;
        mLocationControl = locationControl;
    }

    public Geocache create() {
        Location location = mLocationControl.getLocation();
        if (location == null) {
            return null;
        }
        long time = location.getTime();
        return mGeocacheFactory.create(String.format("ML%1$tk%1$tM%1$tS", time), String.format(
                "[%1$tk:%1$tM] My Location", time), location.getLatitude(),
                location.getLongitude(), Source.MY_LOCATION, null);
    }
}
