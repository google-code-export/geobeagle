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

package com.google.code.geobeagle.activity.map;

import com.google.android.maps.GeoPoint;
import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.database.GeocachesLoader;
import com.google.code.geobeagle.database.WhereFactoryFixedArea;

import java.util.ArrayList;

class QueryManager {
    static class PeggedLoader {
        private final GeocachesLoader mGeocachesLoader;
        private final ArrayList<Geocache> mNullList;
    
        PeggedLoader(GeocachesLoader geocachesLoader, ArrayList<Geocache> nullList) {
            mGeocachesLoader = geocachesLoader;
            mNullList = nullList;
        }
    
        ArrayList<Geocache> load(int latMin, int lonMin, int latMax, int lonMax,
                WhereFactoryFixedArea where, int[] newBounds) {
            if (mGeocachesLoader.count(0, 0, where) > 1500) {
                latMin = latMax = lonMin = lonMax = 0;
                return mNullList;
            }
            newBounds[0] = latMin;
            newBounds[1] = lonMin;
            newBounds[2] = latMax;
            newBounds[3] = lonMax;
            return mGeocachesLoader.loadCaches(0, 0, where);
        }
    }

    private int[] mLatLonMinMax; // i.e. latmin, lonmin, latmax, lonmax
    private final QueryManager.PeggedLoader mPeggedLoader;

    QueryManager(QueryManager.PeggedLoader peggedLoader, int[] latLonMinMax) {
        mLatLonMinMax = latLonMinMax;
        mPeggedLoader = peggedLoader;
    }

    ArrayList<Geocache> load(GeoPoint newTopLeft, GeoPoint newBottomRight) {
        final int lonMin = newTopLeft.getLongitudeE6() - DensityPatchManager.RESOLUTION_LONGITUDE_E6;
        final int latMax = newTopLeft.getLatitudeE6() + DensityPatchManager.RESOLUTION_LATITUDE_E6;
        final int latMin = newBottomRight.getLatitudeE6()
                - DensityPatchManager.RESOLUTION_LATITUDE_E6;
        final int lonMax = newBottomRight.getLongitudeE6()
                + DensityPatchManager.RESOLUTION_LONGITUDE_E6;
        final WhereFactoryFixedArea where = new WhereFactoryFixedArea((double)latMin / 1E6,
                (double)lonMin / 1E6, (double)latMax / 1E6, (double)lonMax / 1E6);

        ArrayList<Geocache> list = mPeggedLoader.load(latMin, lonMin, latMax, lonMax, where,
                mLatLonMinMax);
        return list;
    }

    boolean needsLoading(GeoPoint newTopLeft, GeoPoint newBottomRight) {
        return newTopLeft.getLatitudeE6() > mLatLonMinMax[2]
                || newTopLeft.getLongitudeE6() < mLatLonMinMax[1]
                || newBottomRight.getLatitudeE6() < mLatLonMinMax[0]
                || newBottomRight.getLongitudeE6() > mLatLonMinMax[3];
    }
}