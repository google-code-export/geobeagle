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

package com.google.code.geobeagle.database;

import com.google.code.geobeagle.Geocache;

import java.util.AbstractList;


/**
 * Strategy to only invalidate/reload the list of caches when the bounds are
 * changed to outside the previous bounds. Also returns an empty list if the
 * count is greater than MAX_COUNT.
 */
public class CachesProviderLazyArea implements ICachesProviderArea {

    public static class CoordinateManager {

        private final double mExpandRatio;
        // The bounds of the loaded area
        private double mLatLow;
        private double mLatHigh;
        private double mLonLow;
        private double mLonHigh;

        public CoordinateManager(double expandRatio) {
            mExpandRatio = expandRatio;
        }

        boolean atLeastOneSideIsSmaller(double latLow, double lonLow,
                double latHigh, double lonHigh) {
            boolean fAtLeastOneSideIsSmaller = latLow < mLatLow
                    || lonLow < mLonLow || latHigh > mLatHigh
                    || lonHigh > mLonHigh;
            return fAtLeastOneSideIsSmaller;
        }

        boolean everySideIsBigger(double latLow, double lonLow, double latHigh,
                double lonHigh) {
            return latLow <= mLatLow && lonLow <= mLonLow
                    && latHigh >= mLatHigh && lonHigh >= mLonHigh;
        }

        void expandCoordinates(double latLow, double lonLow, double latHigh,
                double lonHigh) {
            double latExpand = (latHigh - latLow) * mExpandRatio / 2.0;
            double lonExpand = (lonHigh - lonLow) * mExpandRatio / 2.0;
            mLatLow = latLow - latExpand;
            mLonLow = lonLow - lonExpand;
            mLatHigh = latHigh + latExpand;
            mLonHigh = lonHigh + lonExpand;
        }

    }

    /** Maximum number of caches to show */
    public static final int MAX_COUNT = 1000;
    private final ICachesProviderArea mCachesProviderArea;
    private final CoordinateManager mCoordinateManager;

    /** If the user of this instance thinks the list has changed */
    private boolean mHasChanged = true;
    private final PeggedCacheProvider mPeggedCacheProvider;

    public CachesProviderLazyArea(ICachesProviderArea cachesProviderArea,
            PeggedCacheProvider peggedCacheProvider,
            CoordinateManager coordinateManager) {
        mCachesProviderArea = cachesProviderArea;
        mPeggedCacheProvider = peggedCacheProvider;
        mCoordinateManager = coordinateManager;
    }

    @Override
    public void clearBounds() {
        mCachesProviderArea.clearBounds();
    }

    @Override
    public AbstractList<Geocache> getCaches() {
        return getCaches(MAX_COUNT);
    }

    @Override
    public AbstractList<Geocache> getCaches(int maxCount) {
        // Reading one extra cache to see if there are too many
        AbstractList<Geocache> caches = mCachesProviderArea.getCaches(maxCount + 1);
        mCachesProviderArea.resetChanged();
        return mPeggedCacheProvider.pegCaches(maxCount, caches);
    }

    public int getCount() {
        // Not perfectly efficient but it's not used anyway
        return getCaches().size();
    }

    @Override
    public int getTotalCount() {
        return mCachesProviderArea.getTotalCount();
    }

    @Override
    public boolean hasChanged() {
        return mHasChanged || mCachesProviderArea.hasChanged();
    }

    @Override
    public void resetChanged() {
        mHasChanged = false;
        mCachesProviderArea.resetChanged();
    }

    @Override
    public void setBounds(double latLow, double lonLow, double latHigh,
            double lonHigh) {
        boolean tooManyCaches = mPeggedCacheProvider.isTooManyCaches();
        if (tooManyCaches
                && mCoordinateManager.everySideIsBigger(latLow, lonLow,
                        latHigh, lonHigh)) {
            // The new bounds are strictly bigger but the old ones
            // already contained too many caches
            return;
        }
        if (tooManyCaches
                || mCoordinateManager.atLeastOneSideIsSmaller(latLow, lonLow,
                        latHigh, lonHigh)) {
            mCoordinateManager.expandCoordinates(latLow, lonLow, latHigh,
                    lonHigh);
            mCachesProviderArea.setBounds(mCoordinateManager.mLatLow,
                    mCoordinateManager.mLonLow, mCoordinateManager.mLatHigh,
                    mCoordinateManager.mLonHigh);
            mHasChanged = true;
        }
    }

    public void showToastIfTooManyCaches() {
        mPeggedCacheProvider.showToastIfTooManyCaches();
    }

    public boolean tooManyCaches() {
        return mPeggedCacheProvider.isTooManyCaches();
    }
}
