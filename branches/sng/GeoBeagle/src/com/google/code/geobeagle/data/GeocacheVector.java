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

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.ResourceProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

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

    public static class MyLocation implements IGeocacheVector {
        private final ResourceProvider mResourceProvider;

        public MyLocation(ResourceProvider resourceProvider) {
            mResourceProvider = resourceProvider;
        }

        public CharSequence getCoordinatesIdAndName() {
            return null;
        }

        public Geocache getDestination() {
            return null;
        }

        public float getDistance() {
            return -1;
        }

        public CharSequence getId() {
            return mResourceProvider.getString(R.string.my_current_location);
        }

        public Map<String, Object> getViewMap() {
            Map<String, Object> map = new HashMap<String, Object>(1);
            map.put("cache", mResourceProvider.getString(R.string.my_current_location));
            return map;
        }

    }

    private final Geocache mDestination;
    private final float mDistance;
    private final DistanceFormatter mDistanceFormatter;

    public GeocacheVector(Geocache geocache, float distance, DistanceFormatter distanceFormatter) {
        mDestination = geocache;
        mDistance = distance;
        mDistanceFormatter = distanceFormatter;
    }

    public CharSequence getCoordinatesIdAndName() {
        return mDestination.getCoordinatesIdAndName();
    }

    public float getDistance() {
        return mDistance;
    }

    public CharSequence getId() {
        return mDestination.getId();
    }

    public Map<String, Object> getViewMap() {
        Map<String, Object> map = new HashMap<String, Object>(1);
        map.put("cache", mDestination.getIdAndName());
        map.put("distance", mDistanceFormatter.format(mDistance));
        return map;
    }

}
