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
import com.google.code.geobeagle.GeocacheList;
import com.google.code.geobeagle.database.CachesProviderLazyArea;

import android.util.Log;

import java.util.List;

class DensityPatchManager {
    private List<DensityMatrix.DensityPatch> mDensityPatches;
    private final CachesProviderLazyArea mLazyArea;
    public static final double RESOLUTION_LATITUDE = 0.01;
    public static final double RESOLUTION_LONGITUDE = 0.02;
    public static final int RESOLUTION_LATITUDE_E6 = (int)(RESOLUTION_LATITUDE * 1E6);
    public static final int RESOLUTION_LONGITUDE_E6 = (int)(RESOLUTION_LONGITUDE * 1E6);

    DensityPatchManager(List<DensityMatrix.DensityPatch> patches, CachesProviderLazyArea lazyArea) {
        mDensityPatches = patches;
        mLazyArea = lazyArea;
    }

    public List<DensityMatrix.DensityPatch> getDensityPatches(MapView mMapView) {
        Projection projection = mMapView.getProjection();
        GeoPoint newTopLeft = projection.fromPixels(0, 0);
        GeoPoint newBottomRight = projection.fromPixels(mMapView.getRight(), mMapView.getBottom());

        double latLow = newBottomRight.getLatitudeE6() / 1.0E6;
        double latHigh = newTopLeft.getLatitudeE6() / 1.0E6;
        double lonLow = newTopLeft.getLongitudeE6() / 1.0E6;
        double lonHigh = newBottomRight.getLongitudeE6() / 1.0E6;
        mLazyArea.setBounds(latLow, lonLow, latHigh, lonHigh);
        if (!mLazyArea.hasChanged()) {
            return mDensityPatches;
        }

        GeocacheList list = mLazyArea.getCaches();
        mLazyArea.resetChanged();
        DensityMatrix densityMatrix = new DensityMatrix(DensityPatchManager.RESOLUTION_LATITUDE,
                DensityPatchManager.RESOLUTION_LONGITUDE);
        densityMatrix.addCaches(list);
        mDensityPatches = densityMatrix.getDensityPatches();
        Log.d("GeoBeagle", "Density patches:" + mDensityPatches.size());
        return mDensityPatches;
    }
}
