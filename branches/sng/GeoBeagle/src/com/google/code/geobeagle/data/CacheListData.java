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
import com.google.code.geobeagle.data.Destination.DestinationFactory;
import com.google.code.geobeagle.data.DestinationVector.DestinationVectorFactory;
import com.google.code.geobeagle.data.DestinationVector.LocationComparator;

import android.content.Context;
import android.location.Location;

import java.util.ArrayList;
import java.util.Map;

public class CacheListData {
    private final DestinationVectorFactory mDestinationVectorFactory;
    private final DestinationVectors mDestinationVectors;
    private ArrayList<Map<String, Object>> mAdapterData;

    public static CacheListData create(DestinationFactory destinationFactory, Context parent) {
        final DistanceFormatter distanceFormatter = new DistanceFormatter();

        final DestinationVectorFactory destinationVectorFactory = new DestinationVectorFactory(
                destinationFactory, parent.getString(R.string.my_current_location),
                distanceFormatter);

        final LocationComparator locationComparator = new LocationComparator();
        final DestinationVectors destinationVectors = new DestinationVectors(locationComparator,
                destinationVectorFactory);
        return new CacheListData(destinationVectors, destinationVectorFactory);
    }

    public CacheListData(DestinationVectors destinationVectors,
            DestinationVectorFactory destinationVectorFactory) {
        mDestinationVectors = destinationVectors;
        mDestinationVectorFactory = destinationVectorFactory;
    }

    public void add(ArrayList<CharSequence> locations, Location here) {
        mDestinationVectors.reset(locations.size());
        mDestinationVectors.addLocations(locations, here);
        mDestinationVectors.add(mDestinationVectorFactory.createMyLocation());
        mDestinationVectors.sort();
    }

    public ArrayList<Map<String, Object>> getAdapterData() {
        mAdapterData = mDestinationVectors.getAdapterData();
        return mAdapterData;
    }

    public CharSequence getId(int position) {
        return mDestinationVectors.getId(position);
    }

    public CharSequence getLocation(int position) {
        return mDestinationVectors.getLocation(position);
    }

    public void delete(int position) {
        // TODO: write a custom adapter, because only deletes are handled
        // dynamically; any adds require a rebuild.
        mDestinationVectors.delete(position);
        mAdapterData.remove(position);
    }
}
