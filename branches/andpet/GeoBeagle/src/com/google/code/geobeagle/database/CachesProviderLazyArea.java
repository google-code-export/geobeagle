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

import com.google.code.geobeagle.GeocacheList;
import com.google.code.geobeagle.GeocacheListPrecomputed;

/** Strategy to only invalidate/reload the list of caches when the bounds are
 * changed to outside the previous bounds. Also returns an empty list if the count is 
 * greater than MAX_COUNT. */
public class CachesProviderLazyArea implements ICachesProviderArea {

    //The bounds of the loaded area
    private double mLatLow;
    private double mLonLow;
    private double mLatHigh;
    private double mLonHigh;
    private double mExpandRatio;
    /** If the user of this instance thinks the list has changed */
    private boolean mHasChanged = true;
    /** True if the last getCaches() was capped because too high cache count */
    private boolean mTooManyCaches = false;
    /** Maximum number of caches to show */
    public static final int MAX_COUNT = 1000;

    private final ICachesProviderArea mCachesProviderArea;

    public CachesProviderLazyArea(ICachesProviderArea cachesProviderArea,
                double expandRatio) {
        mCachesProviderArea = cachesProviderArea;
        mExpandRatio = expandRatio;
    }

    @Override
    public void setBounds(double latLow, double lonLow, double latHigh, double lonHigh) {
        if (mTooManyCaches && latLow <= mLatLow && lonLow <= mLonLow 
                && latHigh >= mLatHigh && lonHigh >= mLonHigh) {
            //The new bounds are strictly bigger but the old ones 
            //already contained too many caches
            return;
        }
        if (mTooManyCaches 
                || latLow < mLatLow || lonLow < mLonLow 
                || latHigh > mLatHigh || lonHigh > mLonHigh) {
            double latExpand = (latHigh - latLow) * mExpandRatio / 2.0;
            double lonExpand = (lonHigh - lonLow) * mExpandRatio / 2.0;
            mLatLow = latLow - latExpand;
            mLonLow = lonLow - lonExpand;
            mLatHigh = latHigh + latExpand;
            mLonHigh = lonHigh + lonExpand;
            mCachesProviderArea.setBounds(mLatLow, mLonLow, mLatHigh, mLonHigh);
            mHasChanged = true;
        }
    }
    
    @Override
    public GeocacheList getCaches() {
        return getCaches(MAX_COUNT);
    }

    @Override
    public GeocacheList getCaches(int maxCount) {
        //Reading one extra cache allows us to learn whether there were too many
        GeocacheList caches = mCachesProviderArea.getCaches(maxCount + 1);
        mCachesProviderArea.resetChanged();
        mTooManyCaches = (caches.size() > maxCount);
        if (mTooManyCaches) {
            return GeocacheListPrecomputed.EMPTY;
        }
        return caches;
    }

    public boolean tooManyCaches() {
        return mTooManyCaches;
    }
    
    public int getCount() {
        //Not perfectly efficient but it's not used anyway
        return getCaches().size();
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
}
