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

package com.google.code.geobeagle.data.di;

import com.google.code.geobeagle.ResourceProvider;
import com.google.code.geobeagle.data.DistanceFormatter;
import com.google.code.geobeagle.data.Geocache;
import com.google.code.geobeagle.data.GeocacheFromMyLocationFactory;
import com.google.code.geobeagle.data.GeocacheVector;
import com.google.code.geobeagle.data.IGeocacheVector;

import android.location.Location;

public class GeocacheVectorFactory {
    private final DistanceFormatter mDistanceFormatter;
    private final ResourceProvider mResourceProvider;
    private final GeocacheFromMyLocationFactory mGeocacheFromMyLocationFactory;

    public GeocacheVectorFactory(GeocacheFromMyLocationFactory geocacheFromMyLocationFactory,
            DistanceFormatter distanceFormatter, ResourceProvider resourceProvider) {
        mDistanceFormatter = distanceFormatter;
        mGeocacheFromMyLocationFactory = geocacheFromMyLocationFactory;
        mResourceProvider = resourceProvider;
    }

    private float calculateDistance(Location here, Geocache geocache) {
        if (here != null) {
            float[] results = new float[1];
            Location.distanceBetween(here.getLatitude(), here.getLongitude(), geocache
                    .getLatitude(), geocache.getLongitude(), results);

            return results[0];
        }
        return -1;
    }

    public GeocacheVector create(Geocache location, Location here) {
        return new GeocacheVector(location, calculateDistance(here, location), mDistanceFormatter);
    }

    public IGeocacheVector createMyLocation() {
        return new GeocacheVector.MyLocation(mResourceProvider, mGeocacheFromMyLocationFactory);
    }
}
