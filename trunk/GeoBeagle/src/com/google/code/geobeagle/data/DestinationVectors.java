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

import com.google.code.geobeagle.data.DestinationVector.DestinationVectorFactory;
import com.google.code.geobeagle.data.DestinationVector.LocationComparator;

import android.location.Location;

import java.util.ArrayList;
import java.util.Map;

public class DestinationVectors {
    private final DestinationVectorFactory mDestinationVectorFactory;
    private final ArrayList<IDestinationVector> mDestinationVectorsList;
    private final LocationComparator mLocationComparator;

    public DestinationVectors(LocationComparator locationComparator,
            DestinationVectorFactory destinationVectorFactory) {
        mDestinationVectorsList = new ArrayList<IDestinationVector>(0);
        mLocationComparator = locationComparator;
        mDestinationVectorFactory = destinationVectorFactory;
    }

    public void add(IDestinationVector destinationVector) {
        mDestinationVectorsList.add(0, destinationVector);
    }

    public void addLocations(ArrayList<CharSequence> locations, Location here) {
        for (CharSequence location : locations) {
            add(mDestinationVectorFactory.create(location, here));
        }
    }

    public void delete(int position) {
        mDestinationVectorsList.remove(position);
    }

    public IDestinationVector get(int position) {
        return mDestinationVectorsList.get(position);
    }

    public ArrayList<Map<String, Object>> getAdapterData() {
        ArrayList<Map<String, Object>> arrayList = new ArrayList<Map<String, Object>>(
                mDestinationVectorsList.size());
        for (IDestinationVector destination : mDestinationVectorsList) {
            arrayList.add(destination.getViewMap());
        }
        return arrayList;
    }

    public CharSequence getId(int position) {
        return mDestinationVectorsList.get(position).getId();
    }

    public CharSequence getLocation(int position) {
        return mDestinationVectorsList.get(position).getLocation();
    }

    public void reset(int size) {
        mDestinationVectorsList.clear();
        mDestinationVectorsList.ensureCapacity(size);
    }

    public void sort() {
        mLocationComparator.sort(mDestinationVectorsList);
    }

}
