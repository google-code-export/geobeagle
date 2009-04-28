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

package com.google.code.geobeagle.data;

import com.google.code.geobeagle.LocationControlBuffered;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class GeocacheVector implements IGeocacheVector {
    public static class LocationComparator implements Comparator<IGeocacheVector> {
        public int compare(IGeocacheVector destination1, IGeocacheVector destination2) {
            final float d1 = destination1.getDistance();
            final float d2 = destination2.getDistance();
            if (d1 < d2)
                return -1;
            if (d1 > d2)
                return 1;
            return 0;
        }

        public void sort(ArrayList<IGeocacheVector> arrayList) {
            Collections.sort(arrayList, this);
        }
    }

    private final Geocache mGeocache;
    private final LocationControlBuffered mLocationControlBuffered;
    private final DistanceFormatter mDistanceFormatter;

    GeocacheVector(Geocache geocache, LocationControlBuffered locationControlBuffered,
            DistanceFormatter distanceFormatter) {
        mGeocache = geocache;
        mLocationControlBuffered = locationControlBuffered;
        mDistanceFormatter = distanceFormatter;
    }

    public float getDistance() {
        return mGeocache.calculateDistance(mLocationControlBuffered.getLocation());
    }

    public CharSequence getId() {
        return mGeocache.getId();
    }

    public CharSequence getFormattedDistance() {
        return mDistanceFormatter.format(getDistance());
    }

    public CharSequence getIdAndName() {
        return mGeocache.getIdAndName();
    }

    public Geocache getGeocache() {
        return mGeocache;
    }

}
