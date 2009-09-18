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
import com.google.android.maps.MapView;
import com.google.android.maps.Projection;
import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.database.GeocachesLoader;
import com.google.code.geobeagle.database.WhereFactoryFixedArea;

import java.util.ArrayList;
import java.util.List;

class DensityPatchManager {
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

    static class QueryManager {
        private int[] mLatLonMinMax; // i.e. latmin, lonmin, latmax, lonmax
        private final PeggedLoader mPeggedLoader;

        QueryManager(PeggedLoader peggedLoader, int[] latLonMinMax) {
            mLatLonMinMax = latLonMinMax;
            mPeggedLoader = peggedLoader;
        }

        ArrayList<Geocache> load(GeoPoint newTopLeft, GeoPoint newBottomRight) {
            final int lonMin = newTopLeft.getLongitudeE6() - OverlayManager.RESOLUTION_LONGITUDE_E6;
            final int latMax = newTopLeft.getLatitudeE6() + OverlayManager.RESOLUTION_LATITUDE_E6;
            final int latMin = newBottomRight.getLatitudeE6()
                    - OverlayManager.RESOLUTION_LATITUDE_E6;
            final int lonMax = newBottomRight.getLongitudeE6()
                    + OverlayManager.RESOLUTION_LONGITUDE_E6;
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

    private List<DensityMatrix.DensityPatch> mDensityPatches;
    private final QueryManager mQueryManager;

    DensityPatchManager(List<DensityMatrix.DensityPatch> patches, QueryManager queryManager) {
        mDensityPatches = patches;
        mQueryManager = queryManager;
    }

    public List<DensityMatrix.DensityPatch> getDensityPatches(MapView mMapView) {
        Projection projection = mMapView.getProjection();
        GeoPoint newTopLeft = projection.fromPixels(0, 0);
        GeoPoint newBottomRight = projection.fromPixels(mMapView.getRight(), mMapView.getBottom());

        if (!mQueryManager.needsLoading(newTopLeft, newBottomRight)) {
            return mDensityPatches;
        }

        ArrayList<Geocache> list = mQueryManager.load(newTopLeft, newBottomRight);
        DensityMatrix densityMatrix = new DensityMatrix(OverlayManager.RESOLUTION_LATITUDE,
                OverlayManager.RESOLUTION_LONGITUDE);
        densityMatrix.addCaches(list);
        mDensityPatches = densityMatrix.getDensityPatches();
        // Log.d("GeoBeagle", "Density patches:" + mDensityPatches.size());
        return mDensityPatches;
    }
}
