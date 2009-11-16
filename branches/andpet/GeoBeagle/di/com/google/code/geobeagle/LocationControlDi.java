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

package com.google.code.geobeagle;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.preference.PreferenceManager;

public class LocationControlDi {
    public static GeoFixProvider create(Activity activity) {
        if (false) {   //Set to true to use fake locations
            return new GeoFixProviderFake(GeoFixProviderFake.TOKYO);
            //return new GeoFixProviderFake(GeoFixProviderFake.YOKOHAMA);
        } else {
            final LocationManager locationManager = (LocationManager)activity
            .getSystemService(Context.LOCATION_SERVICE);
            final SensorManager sensorManager = (SensorManager)activity
            .getSystemService(Context.SENSOR_SERVICE);
            final SharedPreferences sharedPreferences = PreferenceManager
            .getDefaultSharedPreferences(activity);

            return new GeoFixProviderLive(locationManager, sensorManager,
                    sharedPreferences);
        }
    }
}
