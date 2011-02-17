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

package com.google.code.geobeagle.activity.cachelist.model;

import com.google.code.geobeagle.CacheType;
import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.GeocacheFactory;
import com.google.code.geobeagle.LocationControlBuffered;
import com.google.code.geobeagle.GeocacheFactory.Source;
import com.google.inject.Inject;

import android.location.Location;

public class GeocacheFromMyLocationFactory {
    private final GeocacheFactory mGeocacheFactory;
    private final LocationControlBuffered mLocationControl;

    @Inject
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
                location.getLongitude(), Source.MY_LOCATION, null, CacheType.MY_LOCATION, 0, 0, 0,
                true, false);
    }
}
