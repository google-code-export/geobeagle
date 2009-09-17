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

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

class DensityPatchManager {
    DensityPatchManager(List<DensityMatrix.DensityPatch> patches, GeoPoint topLeft,
            GeoPoint bottomRight, GeocachesLoader geocachesLoader) {
        mTopLeft = topLeft;
        mBottomRight = bottomRight;
        mGeocachesLoader = geocachesLoader;
        mDensityPatches = patches;
    }

    private boolean fTooManyCaches;
    private GeoPoint mTopLeft;
    private GeoPoint mBottomRight;
    private final GeocachesLoader mGeocachesLoader;
    private List<DensityMatrix.DensityPatch> mDensityPatches;

    public List<DensityMatrix.DensityPatch> getDensityPatches(MapView mMapView) {
        Projection proj = mMapView.getProjection();
        GeoPoint newTopLeft = proj.fromPixels(0, 0);
        GeoPoint newBottomRight = proj.fromPixels(mMapView.getRight(), mMapView.getBottom());

        // Further top/North is bigger, further left/West is smaller.
        if (!fTooManyCaches && newTopLeft.getLatitudeE6() <= mTopLeft.getLatitudeE6()
                && newTopLeft.getLongitudeE6() >= mTopLeft.getLongitudeE6()
                && newBottomRight.getLatitudeE6() >= mBottomRight.getLatitudeE6()
                && newBottomRight.getLongitudeE6() <= mBottomRight.getLongitudeE6()) {
//            Log.d("GeoBeagle", "DP punting");
            return mDensityPatches;
        }
        Log.d("GeoBeagle", "------refresh density matrix");
        mTopLeft = newTopLeft;
        mBottomRight = newBottomRight;

        double latMin = newBottomRight.getLatitudeE6() / 1000000.0;
        double lonMin = newTopLeft.getLongitudeE6() / 1000000.0;
        double latMax = newTopLeft.getLatitudeE6() / 1000000.0;
        double lonMax = newBottomRight.getLongitudeE6() / 1000000.0;

        // Expand area to cover whole density patches:
        latMin = Math.floor(latMin / OverlayManager.RESOLUTION_LATITUDE)
                * OverlayManager.RESOLUTION_LATITUDE;
        lonMin = Math.floor(lonMin / OverlayManager.RESOLUTION_LONGITUDE)
                * OverlayManager.RESOLUTION_LONGITUDE;
        latMax = Math.ceil(latMax / OverlayManager.RESOLUTION_LATITUDE)
                * OverlayManager.RESOLUTION_LATITUDE;
        lonMax = Math.ceil(lonMax / OverlayManager.RESOLUTION_LONGITUDE)
                * OverlayManager.RESOLUTION_LONGITUDE;

        final WhereFactoryFixedArea where = new WhereFactoryFixedArea(latMin, lonMin, latMax,
                lonMax);
        ArrayList<Geocache> list = mGeocachesLoader.loadCaches(0, 0, where, 1500);

        fTooManyCaches = false;
        if (list == null) {
            list = new ArrayList<Geocache>();
            fTooManyCaches = true;
        }
        DensityMatrix densityMatrix = new DensityMatrix(OverlayManager.RESOLUTION_LATITUDE,
                OverlayManager.RESOLUTION_LONGITUDE);
        densityMatrix.addCaches(list);
        mDensityPatches = densityMatrix.getDensityPatches();
        Log.d("GeoBeagle", "Density patches:" + mDensityPatches.size());
        return mDensityPatches;
    }
}
