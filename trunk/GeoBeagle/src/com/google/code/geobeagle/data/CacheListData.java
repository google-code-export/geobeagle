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

import com.google.code.geobeagle.data.di.GeocacheVectorFactory;

import android.location.Location;

import java.util.ArrayList;
import java.util.Map;

public class CacheListData {
    private ArrayList<Map<String, Object>> mAdapterData;
    private final GeocacheVectorFactory mDestinationVectorFactory;
    private final GeocacheVectors mDestinationVectors;

    public CacheListData(GeocacheVectors geocacheVectors,
            GeocacheVectorFactory geocacheVectorFactory) {
        mDestinationVectors = geocacheVectors;
        mDestinationVectorFactory = geocacheVectorFactory;
    }

    public void add(ArrayList<Geocache> locations, Location here) {
        mDestinationVectors.reset(locations.size());
        mDestinationVectors.addLocations(locations, here);
        mDestinationVectors.add(mDestinationVectorFactory.createMyLocation());
        mDestinationVectors.sort();
    }

    public void delete(int position) {
        // TODO: write a custom adapter, because only deletes are handled
        // dynamically; any adds require a rebuild.
        mDestinationVectors.delete(position);
        mAdapterData.remove(position);
    }

    public ArrayList<Map<String, Object>> getAdapterData() {
        mAdapterData = mDestinationVectors.getAdapterData();
        return mAdapterData;
    }

    public CharSequence getCoordinatesIdAndName(int position) {
        return mDestinationVectors.getCoordinatesIdAndName(position);
    }

    public CharSequence getId(int position) {
        return mDestinationVectors.getId(position);
    }
}
