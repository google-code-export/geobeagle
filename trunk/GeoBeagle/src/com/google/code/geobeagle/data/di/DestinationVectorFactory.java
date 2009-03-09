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
import com.google.code.geobeagle.data.Destination;
import com.google.code.geobeagle.data.DestinationVector;
import com.google.code.geobeagle.data.DistanceFormatter;
import com.google.code.geobeagle.data.IDestinationVector;

import android.location.Location;

public class DestinationVectorFactory {
    private final DestinationFactory mDestinationFactory;
    private final DistanceFormatter mDistanceFormatter;
    private final ResourceProvider mResourceProvider;

    public DestinationVectorFactory(DestinationFactory destinationFactory,
            DistanceFormatter distanceFormatter, ResourceProvider resourceProvider) {
        mDestinationFactory = destinationFactory;
        mDistanceFormatter = distanceFormatter;
        mResourceProvider = resourceProvider;
    }

    private float calculateDistance(Location here, Destination destination) {
        if (here != null) {
            float[] results = new float[1];
            Location.distanceBetween(here.getLatitude(), here.getLongitude(), destination
                    .getLatitude(), destination.getLongitude(), results);

            return results[0];
        }
        return -1;
    }

    public DestinationVector create(CharSequence location, Location here) {
        final Destination destinationHere = mDestinationFactory.create(location);
        return new DestinationVector(destinationHere, calculateDistance(here, destinationHere),
                mDistanceFormatter);
    }

    public IDestinationVector createMyLocation() {
        return new DestinationVector.MyLocation(mResourceProvider);
    }
}
