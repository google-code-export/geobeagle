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

import com.google.code.geobeagle.data.GeocacheVector.LocationComparator;

import android.location.Location;

import java.util.ArrayList;

public class GeocacheVectors {
    private final GeocacheVectorFactory mGeocacheVectorFactory;
    private final ArrayList<IGeocacheVector> mGeocacheVectorsList;
    private final LocationComparator mLocationComparator;

    public GeocacheVectors(LocationComparator locationComparator,
            GeocacheVectorFactory geocacheVectorFactory) {
        mGeocacheVectorsList = new ArrayList<IGeocacheVector>(0);
        mLocationComparator = locationComparator;
        mGeocacheVectorFactory = geocacheVectorFactory;
    }

    public void add(IGeocacheVector destinationVector) {
        mGeocacheVectorsList.add(0, destinationVector);
    }

    public void addLocations(ArrayList<Geocache> geocaches, Location here) {
        for (Geocache geocache : geocaches) {
            add(mGeocacheVectorFactory.create(geocache, here));
        }
    }

    public IGeocacheVector get(int position) {
        return mGeocacheVectorsList.get(position);
    }

    public void remove(int position) {
        mGeocacheVectorsList.remove(position);
    }

    public void reset(int size) {
        mGeocacheVectorsList.clear();
        mGeocacheVectorsList.ensureCapacity(size);
    }

    public int size() {
        return mGeocacheVectorsList.size();
    }

    public void sort() {
        mLocationComparator.sort(mGeocacheVectorsList);
    }

}
