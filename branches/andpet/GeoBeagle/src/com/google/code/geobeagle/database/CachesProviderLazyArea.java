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
import com.google.code.geobeagle.xmlimport.GpxImporterDI.Toaster;

import java.util.ArrayList;

/** Strategy to only invalidate/reload the list of caches when the bounds are
 * changed to outside the previous bounds. Also returns an empty list if the count is 
 * greater than MAX_COUNT. */
public class CachesProviderLazyArea implements ICachesProviderArea {

    //The bounds of the loaded area
    private double mLatLow;
    private double mLonLow;
    private double mLatHigh;
    private double mLonHigh;
    /** If the user of this instance thinks the list has changed */
    private boolean mHasChanged = true;
    private boolean mTooManyCaches = false;
    /** Maximum number of caches to show */
    public static final int MAX_COUNT = 1500;
    private final Toaster mToaster;
    private static final ArrayList<Geocache> NULL_LIST = new ArrayList<Geocache>();
    
    private final ICachesProviderArea mCachesProviderArea;

    public CachesProviderLazyArea(ICachesProviderArea cachesProviderArea,
                Toaster toaster) {
        mCachesProviderArea = cachesProviderArea;
        mToaster = toaster;
    }

    @Override
    public void setBounds(double latLow, double lonLow, double latHigh, double lonHigh) {
        if (latLow < mLatLow || lonLow < mLonLow 
            || latHigh > mLatHigh || lonHigh > mLonHigh) {
            if (mTooManyCaches && latLow <= mLatLow && lonLow <= mLonLow 
                    && latHigh >= mLatHigh && lonHigh >= mLonHigh) {
                //The new bounds are strictly bigger but the old ones 
                //already contained too many caches
                return;
            }
            mLatLow = latLow;
            mLonLow = lonLow;
            mLatHigh = latHigh;
            mLonHigh = lonHigh;
            mCachesProviderArea.setBounds(latLow, lonLow, latHigh, lonHigh);
            mHasChanged = true;
        }
    }
    
    @Override
    public ArrayList<Geocache> getCaches() {
        if (mCachesProviderArea.hasChanged()) {
            int count = mCachesProviderArea.getCount();
            mCachesProviderArea.setChanged(false);
            if (count > MAX_COUNT) {
                if (!mTooManyCaches)
                    mToaster.showToast();
                mTooManyCaches = true;
            } else {
                mTooManyCaches = false;
            }
        }
        if (mTooManyCaches) {
            return NULL_LIST;
        }
        return mCachesProviderArea.getCaches();
    }

    @Override
    public int getCount() {
        if (mCachesProviderArea.hasChanged()) {
            int count = mCachesProviderArea.getCount();
            mCachesProviderArea.setChanged(false);
            if (count > MAX_COUNT) {
                if (!mTooManyCaches)
                    mToaster.showToast();
                mTooManyCaches = true;
            } else {
                mTooManyCaches = false;
                return count;
            }
        }

        if (mTooManyCaches)
            return 0;
        return mCachesProviderArea.getCount();
    }

    @Override
    public boolean hasChanged() {
        return mHasChanged || mCachesProviderArea.hasChanged();
    }

    @Override
    public void setChanged(boolean changed) {
        mHasChanged = changed;
        if (!changed)
            mCachesProviderArea.setChanged(false);
    }

    @Override
    public void setExtraCondition(String condition) {
        mCachesProviderArea.setExtraCondition(condition);
    }
}
