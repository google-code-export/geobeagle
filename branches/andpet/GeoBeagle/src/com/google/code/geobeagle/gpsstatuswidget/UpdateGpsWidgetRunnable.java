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

import android.os.Handler;

public class UpdateGpsWidgetRunnable implements Runnable {
    private final Handler mHandler;
    private final GeoFixProvider mGeoFixProvider;
    private final Meter mMeterWrapper;
    private final TextLagUpdater mTextLagUpdater;

    UpdateGpsWidgetRunnable(Handler handler, GeoFixProvider geoFixProvider,
            Meter meter, TextLagUpdater textLagUpdater) {
        mMeterWrapper = meter;
        mGeoFixProvider = geoFixProvider;
        mTextLagUpdater = textLagUpdater;
        mHandler = handler;
    }

    public void run() {
        // Update the lag time and the orientation.
        mTextLagUpdater.updateTextLag();
        mMeterWrapper.setAzimuth(mGeoFixProvider.getAzimuth());
        mHandler.postDelayed(this, 500);
    }
}
