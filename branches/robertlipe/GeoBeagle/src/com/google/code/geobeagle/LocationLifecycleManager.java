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

package com.google.code.geobeagle;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.LocationListener;
import android.location.LocationManager;

/*
 * Handle onPause and onResume for the LocationManager.
 */
public class LocationLifecycleManager implements LifecycleManager {
    private final LocationListener mLocationListener;
    private final LocationManager mLocationManager;

    public LocationLifecycleManager(LocationListener locationListener,
            LocationManager locationManager) {
        mLocationListener = locationListener;
        mLocationManager = locationManager;
    }

    /*
     * (non-Javadoc)
     * @see com.google.code.geobeagle.LifecycleManager#onPause()
     */
    public void onPause(Editor editor) {
        mLocationManager.removeUpdates(mLocationListener);
    }

    /*
     * (non-Javadoc)
     * @see com.google.code.geobeagle.LifecycleManager#onResume()
     */
    public void onResume(SharedPreferences preferences) {
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
                mLocationListener);
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
                mLocationListener);
    }

}
