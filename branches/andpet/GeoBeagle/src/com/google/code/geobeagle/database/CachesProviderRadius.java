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


public class CachesProviderRadius implements ICachesProviderCenter {

    private ICachesProviderArea mCachesProviderArea;
    private double mLatitude;
    private double mLongitude;
    private double mDegrees;

    public CachesProviderRadius(ICachesProviderArea area) {
        mCachesProviderArea = area;
    }
    
    @Override
    public GeocacheList getCaches() {
        return mCachesProviderArea.getCaches();
    }

    @Override
    public int getCount() {
        return mCachesProviderArea.getCount();
    }

    @Override
    public boolean hasChanged() {
        return mCachesProviderArea.hasChanged();
    }

    @Override
    public void resetChanged() {
        mCachesProviderArea.resetChanged();
    }
    
    public void setRadius(double radius) {
        mDegrees = radius;
        updateBounds();
    }
    
    @Override
    public void setCenter(double latitude, double longitude) {
        mLatitude = latitude;
        mLongitude = longitude;
        updateBounds();
    }

    private void updateBounds() {
        double latLow = mLatitude - mDegrees;
        double latHigh = mLatitude + mDegrees;
        double lonLow = mLongitude - mDegrees;
        double lonHigh = mLongitude + mDegrees;
        /*
        double lat_radians = Math.toRadians(mLatitude);
        double cos_lat = Math.cos(lat_radians);
        double lonLow = Math.max(-180, mLongitude - mDegrees / cos_lat);
        double lonHigh = Math.min(180, mLongitude + mDegrees / cos_lat);
        */
        mCachesProviderArea.setBounds(latLow, lonLow, latHigh, lonHigh);
    }
}
