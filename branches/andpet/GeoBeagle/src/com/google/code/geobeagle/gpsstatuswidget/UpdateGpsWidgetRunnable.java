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

package com.google.code.geobeagle.gpsstatuswidget;

import com.google.code.geobeagle.GeoFixProvider;
import com.google.code.geobeagle.gpsstatuswidget.GpsStatusWidgetDelegate.TimeProvider;

import android.os.Handler;

/** Updates the Gps widget two times a second */
public class UpdateGpsWidgetRunnable implements Runnable {
    private final Handler mHandler;
    private final GeoFixProvider mGeoFixProvider;
    private final Meter mMeter;
    private final GpsStatusWidgetDelegate mGpsStatusWidgetDelegate;
    private final TimeProvider mTimeProvider;

    UpdateGpsWidgetRunnable(Handler handler, GeoFixProvider geoFixProvider,
            Meter meter, GpsStatusWidgetDelegate gpsStatusWidgetDelegate, 
            TimeProvider timeProvider) {
        mMeter = meter;
        mGeoFixProvider = geoFixProvider;
        mHandler = handler;
        mGpsStatusWidgetDelegate = gpsStatusWidgetDelegate;
        mTimeProvider = timeProvider;
    }

    public void run() {
        // Update the lag time and the orientation.
        long systemTime = mTimeProvider.getTime();
        mGpsStatusWidgetDelegate.updateLagText(systemTime);
        mMeter.setAzimuth(mGeoFixProvider.getAzimuth());
        mHandler.postDelayed(this, 500);
    }
}
