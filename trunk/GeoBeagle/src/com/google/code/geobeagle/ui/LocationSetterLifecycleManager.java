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
