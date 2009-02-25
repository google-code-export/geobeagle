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
import com.google.code.geobeagle.data.Destination;
import com.google.code.geobeagle.io.LocationBookmarksSql;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.view.View;
import android.view.View.OnFocusChangeListener;

import java.util.regex.Pattern;

public class LocationSetter implements LifecycleManager {
    public static final class EditTextFocusChangeListener implements OnFocusChangeListener {
        private final MockableEditText mEditText;
        private final LocationBookmarksSql mLocationBookmarks;

        public EditTextFocusChangeListener(LocationBookmarksSql locationBookmarksSql,
                MockableEditText editText) {
            mLocationBookmarks = locationBookmarksSql;
            mEditText = editText;
        }

        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                mLocationBookmarks.saveLocation(mEditText.getText());
            }
        }
    }

    public static final String FNAME_RECENT_LOCATIONS = "RECENT_LOCATIONS";
    public static final String PREFS_LOCATION = "Location";
    private final Pattern[] mDestinationPatterns;
    private final LocationControl mGpsControl;
    private final String mInitialDestination;
    private final LocationBookmarksSql mLocationBookmarks;
    private final MockableEditText mTxtLocation;
    private final ErrorDisplayer mErrorDisplayer;

    public LocationSetter(Context context, MockableEditText txtLocation,
            LocationControl locationControl, Pattern destinationPatterns[],
            LocationBookmarksSql locationBookmarksTextFile, String initialDestination, ErrorDisplayer errorDisplayer) {
        mTxtLocation = txtLocation;
        mDestinationPatterns = destinationPatterns;
        mGpsControl = locationControl;
        mLocationBookmarks = locationBookmarksTextFile;
        mInitialDestination = initialDestination;
        mErrorDisplayer = errorDisplayer;
    }

    /*
     * (non-Javadoc)
     * @see com.google.code.geobeagle.ui.DestinationProvider#getDestination()
     */
    public Destination getDestination() {
        return new Destination(mTxtLocation.getText(), mDestinationPatterns);
    }

    // TODO: test.
    public CharSequence getId() {
        return getDestination().getFullId();
    }
    
    public void onPause(Editor editor) {
        editor.putString(PREFS_LOCATION, mTxtLocation.getText().toString());
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
        mLocationBookmarks.saveLocation(c);
        mTxtLocation.setText(c);
    }

    public CharSequence setLocation(double lat, double lon, CharSequence description) {
        final CharSequence latLonText = Util.formatDegreesAsDecimalDegreesString(lat) + ", "
                + Util.formatDegreesAsDecimalDegreesString(lon) + " (" + description + ")";
        mTxtLocation.setText(latLonText);
        mLocationBookmarks.saveLocation(latLonText);
        return latLonText;
    }

}
