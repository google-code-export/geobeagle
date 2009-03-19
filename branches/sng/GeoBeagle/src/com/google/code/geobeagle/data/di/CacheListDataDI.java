
package com.google.code.geobeagle.data.di;

import com.google.code.geobeagle.ResourceProvider;
import com.google.code.geobeagle.data.CacheListData;
import com.google.code.geobeagle.data.DistanceFormatter;
import com.google.code.geobeagle.data.GeocacheVectors;
import com.google.code.geobeagle.data.GeocacheVector.LocationComparator;

public class CacheListDataDI {

    public static CacheListData create(ResourceProvider resourceProvider,
            GeocacheFromTextFactory geocacheFromTextFactory) {
        final DistanceFormatter distanceFormatter = new DistanceFormatter();

        final GeocacheVectorFactory geocacheVectorFactory = new GeocacheVectorFactory(
                geocacheFromTextFactory, distanceFormatter, resourceProvider);

        final LocationComparator locationComparator = new LocationComparator();
        final GeocacheVectors geocacheVectors = new GeocacheVectors(locationComparator,
                geocacheVectorFactory);
        return new CacheListData(geocacheVectors, geocacheVectorFactory);
    }

}
