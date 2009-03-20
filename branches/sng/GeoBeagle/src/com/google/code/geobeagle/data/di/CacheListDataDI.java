
package com.google.code.geobeagle.data.di;

import com.google.code.geobeagle.data.CacheListData;
import com.google.code.geobeagle.data.GeocacheVectors;

public class CacheListDataDI {

    public static CacheListData create(GeocacheVectors geocacheVectors,
            GeocacheFromTextFactory geocacheFromTextFactory,
            GeocacheVectorFactory geocacheVectorFactory) {
        return new CacheListData(geocacheVectors, geocacheVectorFactory);
    }

}
