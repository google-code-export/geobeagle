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
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.compass.fieldnotes.Toaster;
import com.google.code.geobeagle.database.DbFrontend;
import com.google.code.geobeagle.database.WhereFactoryFixedArea;
import com.google.inject.Inject;

import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

class QueryManager {
    /**
     * We need to cache "needs loading" status because we might be zoomed out so
     * far we see no caches. In that situation, the compass will attempt to
     * refresh us every second, and we'll query the database over and over again
     * to learn that we have too many caches for the same set of points.
     */

    static class CachedNeedsLoading {
        private GeoPoint mOldBottomRight;
        private GeoPoint mOldTopLeft;

        @Inject
        public CachedNeedsLoading() {
            mOldTopLeft = new GeoPoint(0, 0);
            mOldBottomRight = new GeoPoint(0, 0);
        }

        public CachedNeedsLoading(GeoPoint topLeft, GeoPoint bottomRight) {
            mOldTopLeft = topLeft;
            mOldBottomRight = bottomRight;
        }

        boolean needsLoading(GeoPoint newTopLeft, GeoPoint newBottomRight) {
            if (mOldTopLeft.equals(newTopLeft) && mOldBottomRight.equals(newBottomRight)) {
                Log.d("GeoBeagle", "CachedNeedsLoading.needsLoading: false");
                return false;
            }
            mOldTopLeft = newTopLeft;
            mOldBottomRight = newBottomRight;
            Log.d("GeoBeagle", "CachedNeedsLoading.needsLoading: true");
            return true;
        }
    }

    static interface Loader {
        ArrayList<Geocache> load(int latMin, int lonMin, int latMax, int lonMax,
                WhereFactoryFixedArea where, int[] newBounds);
    }

    static class LoaderImpl implements Loader {
        private final DbFrontend mDbFrontend;

        @Inject
        LoaderImpl(DbFrontend dbFrontend) {
            mDbFrontend = dbFrontend;
        }

        @Override
        public ArrayList<Geocache> load(int latMin, int lonMin, int latMax, int lonMax,
                WhereFactoryFixedArea where, int[] newBounds) {
            Log.d("GeoBeagle", "LoaderImpl.load: " + latMin + ", " + lonMin + ", " + latMax + ", "
                    + lonMax);
            newBounds[0] = latMin;
            newBounds[1] = lonMin;
            newBounds[2] = latMax;
            newBounds[3] = lonMax;
            return mDbFrontend.loadCaches(0, 0, where);
        }
    }

    static class PeggedLoader implements Loader {
        private final DbFrontend mDbFrontend;
        private final LoaderImpl mLoader;
        private final ArrayList<Geocache> mNullList;
        private boolean mTooManyCaches;
        private final Toaster mToaster;

        @Inject
        PeggedLoader(DbFrontend dbFrontend, Toaster toaster,
                LoaderImpl loaderImpl) {
            mNullList = new ArrayList<Geocache>();
            mDbFrontend = dbFrontend;
            mToaster = toaster;
            mTooManyCaches = false;
            mLoader = loaderImpl;
        }

        @Override
        public ArrayList<Geocache> load(int latMin, int lonMin, int latMax, int lonMax,
                WhereFactoryFixedArea where, int[] newBounds) {
            Log.d("GeoBeagle", "PeggedLoader.load: " + latMin + ", " + lonMin + ", " + latMax
                    + ", " + lonMax);
            if (mDbFrontend.count(0, 0, where) > 1500) {
                latMin = latMax = lonMin = lonMax = 0;
                if (!mTooManyCaches) {
                    Log.d("GeoBeagle", "QueryManager.load: too many caches");
                    mToaster.toast(R.string.too_many_caches, Toast.LENGTH_SHORT);
                    mTooManyCaches = true;
                }
                return mNullList;
            }
            mTooManyCaches = false;
            return mLoader.load(latMin, lonMin, latMax, lonMax, where, newBounds);
        }
    }

    private final CachedNeedsLoading mCachedNeedsLoading;
    private final int[] mLatLonMinMax; // i.e. latmin, lonmin, latmax, lonmax
    private ArrayList<Geocache> mList;

    @Inject
    QueryManager(CachedNeedsLoading cachedNeedsLoading) {
        mLatLonMinMax = new int[] {
                0, 0, 0, 0
        };
        mCachedNeedsLoading = cachedNeedsLoading;
    }

    // For testing
    QueryManager(CachedNeedsLoading cachedNeedsLoading, int[] latLonMinMax) {
        mCachedNeedsLoading = cachedNeedsLoading;
        mLatLonMinMax = latLonMinMax;
    }

    ArrayList<Geocache> load(GeoPoint newTopLeft, GeoPoint newBottomRight, Loader loader) {
        // Expand the area by the resolution so we get complete patches for the
        // density map. This isn't needed for the pins overlay, but it doesn't
        // hurt either.
        Log.d("GeoBeagle", "QueryManager.load: " + newTopLeft + ", " + newBottomRight);
        final int lonMin = newTopLeft.getLongitudeE6()
                - DensityPatchManager.RESOLUTION_LONGITUDE_E6;
        final int latMax = newTopLeft.getLatitudeE6() + DensityPatchManager.RESOLUTION_LATITUDE_E6;
        final int latMin = newBottomRight.getLatitudeE6()
                - DensityPatchManager.RESOLUTION_LATITUDE_E6;
        final int lonMax = newBottomRight.getLongitudeE6()
                + DensityPatchManager.RESOLUTION_LONGITUDE_E6;
        final WhereFactoryFixedArea where = new WhereFactoryFixedArea(latMin / 1E6,
                lonMin / 1E6, latMax / 1E6, lonMax / 1E6);

        mList = loader.load(latMin, lonMin, latMax, lonMax, where, mLatLonMinMax);
        return mList;
    }

    boolean needsLoading(GeoPoint newTopLeft, GeoPoint newBottomRight) {
        Log.d("GeoBeagle", "QueryManager.needsLoading new Points: " + newTopLeft + ", "
                + newBottomRight);
        Log.d("GeoBeagle", "QueryManager.needsLoading old Points: " + mLatLonMinMax[0] + ", "
                + mLatLonMinMax[1] + ", " + mLatLonMinMax[2] + ", " + mLatLonMinMax[3]);
        final boolean needsLoading = newTopLeft.getLatitudeE6() > mLatLonMinMax[2]
                || newTopLeft.getLongitudeE6() < mLatLonMinMax[1]
                || newBottomRight.getLatitudeE6() < mLatLonMinMax[0]
                || newBottomRight.getLongitudeE6() > mLatLonMinMax[3];
        Log.d("GeoBeagle", "QueryManager.needsLoading: " + needsLoading);

        return mCachedNeedsLoading.needsLoading(newTopLeft, newBottomRight) && needsLoading;
    }
}
