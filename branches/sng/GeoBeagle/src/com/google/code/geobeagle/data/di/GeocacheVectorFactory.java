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
import com.google.code.geobeagle.data.GeocacheVector;
import com.google.code.geobeagle.data.IGeocacheVector;

import android.location.Location;

public class GeocacheVectorFactory {
    private final GeocacheFactory mDestinationFactory;
    private final DistanceFormatter mDistanceFormatter;
    private final ResourceProvider mResourceProvider;

    public GeocacheVectorFactory(GeocacheFactory geocacheFactory,
            DistanceFormatter distanceFormatter, ResourceProvider resourceProvider) {
        mDestinationFactory = geocacheFactory;
        mDistanceFormatter = distanceFormatter;
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

    public GeocacheVector create(CharSequence location, Location here) {
        final Geocache destinationHere = mDestinationFactory.create(location);
        return new GeocacheVector(destinationHere, calculateDistance(here, destinationHere),
                mDistanceFormatter);
    }

    public IGeocacheVector createMyLocation() {
        return new GeocacheVector.MyLocation(mResourceProvider);
    }
}
