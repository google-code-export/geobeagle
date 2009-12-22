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
import com.google.code.geobeagle.GeocacheList;
import com.google.code.geobeagle.GeocacheListPrecomputed;

import java.util.ArrayList;

class CachesProviderStub implements ICachesProviderArea {

    private ArrayList<Geocache> mGeocaches = new ArrayList<Geocache>();
    private double mLatLow = 0.0;
    private double mLatHigh = 0.0;
    private double mLonLow = 0.0;
    private double mLonHigh = 0.0;
    private boolean mHasChanged = true;

    private int mBoundsCalls = 0;
    private boolean mIsInitialized = false;
    public CachesProviderStub() {
        
    }
    public CachesProviderStub(ArrayList<Geocache> geocacheList)  {
        mGeocaches = geocacheList;
    }

    public void addCache(Geocache geocache) {
        mGeocaches.add(geocache);
    }

    /** maxCount <= 0 means no limit */
    private GeocacheList fetchCaches(int maxCount) {
        if (!mIsInitialized)
            return new GeocacheListPrecomputed(mGeocaches);
        
        ArrayList<Geocache> selection = new ArrayList<Geocache>();
        for (Geocache geocache : mGeocaches) {
            if (geocache.getLatitude() >= mLatLow
                && geocache.getLatitude() <= mLatHigh
                && geocache.getLongitude() >= mLonLow
                && geocache.getLongitude() <= mLonHigh) {
                selection.add(geocache);
                if (selection.size() == maxCount)
                    break;
            }
        }
        return new GeocacheListPrecomputed(selection);
    }
    
    @Override
    public GeocacheList getCaches() {
        return fetchCaches(-1);
    }

    @Override
    public GeocacheList getCaches(int maxCount) {
        return fetchCaches(maxCount);
    }
    
    @Override
    public int getCount() {
        return fetchCaches(-1).size();
    }

    public int getSetBoundsCalls() {
        return mBoundsCalls;
    }

    @Override
    public void setBounds(double latLow, double lonLow, double latHigh, double lonHigh) {
        mBoundsCalls += 1;
        mLatLow = latLow;
        mLatHigh = latHigh;
        mLonLow = lonLow;
        mLonHigh = lonHigh;
        mHasChanged = true;
        mIsInitialized = true;
    }

    @Override
    public boolean hasChanged() {
        return mHasChanged;
    }

    @Override
    public void resetChanged() {
        mHasChanged = false;
    }

    public void setChanged(boolean changed) {
        mHasChanged = changed;
    }

    @Override
    public int getTotalCount() {
        return mGeocaches.size();
    }
    
    @Override
    public void clearBounds() {
        mIsInitialized = false;
    }
}
