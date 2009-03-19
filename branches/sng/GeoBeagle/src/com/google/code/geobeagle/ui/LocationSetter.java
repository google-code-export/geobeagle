/*
 ** Licensed under the Apache License, Version 2.0 (the "License");
 ** you may not use this file except in compliance with the License.
 ** You may obtain a copy of the License at
 **
 **     http://www.apache.org/licenses/LICENSE-2.0
 **
 ** Unless required by applicable law or agreed to in writing, software
 ** distributed under the License is distributed on an "AS IS" BASIS,
 ** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ** See the License for the specific language governing permissions and
 ** limitations under the License.
 */

package com.google.code.geobeagle.ui;

import com.google.code.geobeagle.LifecycleManager;
import com.google.code.geobeagle.LocationControl;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.Util;
import com.google.code.geobeagle.data.Geocache;
import com.google.code.geobeagle.data.di.GeocacheFactory;
import com.google.code.geobeagle.io.LocationSaver;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;

public class LocationSetter implements LifecycleManager {

    public static final String FNAME_RECENT_LOCATIONS = "RECENT_LOCATIONS";
    public static final String PREFS_LOCATION = "Location";
    private final GeocacheFactory mDestinationFactory;
    private final ErrorDisplayer mErrorDisplayer;
    private final LocationControl mGpsControl;
    private final String mInitialDestination;
    private final LocationSaver mLocationSaver;
    private final MockableEditText mTxtLocation;

    public LocationSetter(Context context, MockableEditText txtLocation,
            LocationControl locationControl, GeocacheFactory geocacheFactory,
            String initialDestination, ErrorDisplayer errorDisplayer, LocationSaver locationSaver) {
        mTxtLocation = txtLocation;
        mDestinationFactory = geocacheFactory;
        mGpsControl = locationControl;
        mInitialDestination = initialDestination;
        mErrorDisplayer = errorDisplayer;
        mLocationSaver = locationSaver;
    }

    /*
     * (non-Javadoc)
     * @see com.google.code.geobeagle.ui.DestinationProvider#getDestination()
     */
    public Geocache getGeocache() {
        return mDestinationFactory.create(mTxtLocation.getText());
    }

    // TODO: test.
    public CharSequence getId() {
        return getGeocache().getId();
    }

    public void onPause(Editor editor) {
        final CharSequence text = mTxtLocation.getText();
        editor.putString(PREFS_LOCATION, text.toString());
        mLocationSaver.saveLocation(text);
    }

    public void onResume(SharedPreferences preferences) {
        setLocation(preferences.getString(PREFS_LOCATION, mInitialDestination));
    }

    public void setLocation(CharSequence c) {
        if (c == null) {
            Location location = mGpsControl.getLocation();
            if (location == null) {
                mErrorDisplayer.displayError(R.string.current_location_null);
                return;
            }
            setLocation(location.getLatitude(), location.getLongitude(), String.format(
                    "[%1$tk:%1$tM] My Location", location.getTime()));
            return;
        }
        mTxtLocation.setText(c);
    }

    public CharSequence setLocation(double lat, double lon, CharSequence description) {
        final CharSequence latLonText = Util.formatDegreesAsDecimalDegreesString(lat) + ", "
                + Util.formatDegreesAsDecimalDegreesString(lon) + " (" + description + ")";
        mTxtLocation.setText(latLonText);
        mLocationSaver.saveLocation(latLonText);
        return latLonText;
    }

}
