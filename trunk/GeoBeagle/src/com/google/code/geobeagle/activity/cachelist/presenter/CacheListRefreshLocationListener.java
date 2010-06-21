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

package com.google.code.geobeagle.activity.cachelist.presenter;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class CacheListRefreshLocationListener implements LocationListener {
    private final CacheListRefresh mCacheListRefresh;

    public CacheListRefreshLocationListener(CacheListRefresh cacheListRefresh) {
        mCacheListRefresh = cacheListRefresh;
    }

    public void onLocationChanged(Location location) {
        // Log.d("GeoBeagle", "location changed");
        mCacheListRefresh.refresh();
    }

    public void onProviderDisabled(String provider) {
    }

    public void onProviderEnabled(String provider) {
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
}