
package com.google.code.geobeagle.data;

import com.google.code.geobeagle.LocationControl;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.data.GeocacheFactory.Source;
import com.google.code.geobeagle.ui.ErrorDisplayer;

import android.location.Location;

public class GeocacheFromMyLocationFactory {
    private final LocationControl mLocationControl;
    private final ErrorDisplayer mErrorDisplayer;
    private final GeocacheFactory mGeocacheFactory;

    public GeocacheFromMyLocationFactory(GeocacheFactory geocacheFactory,
            LocationControl locationControl, ErrorDisplayer errorDisplayer) {
        mGeocacheFactory = geocacheFactory;
        mLocationControl = locationControl;
        mErrorDisplayer = errorDisplayer;
    }

    public Geocache create() {
        Location location = mLocationControl.getLocation();
        if (location == null) {
            mErrorDisplayer.displayError(R.string.current_location_null);
            return null;
        }
        long time = location.getTime();
        return mGeocacheFactory.create(String.format("ML%1$tk%1$tM%1$tS", time), String.format(
                "[%1$tk:%1$tM] My Location", time), location.getLatitude(),
                location.getLongitude(), Source.MY_LOCATION, null);
    }
}
