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

import com.google.code.geobeagle.activity.cachelist.model.LocationControlBuffered;

import android.os.Handler;

public class UpdateGpsWidgetRunnable implements Runnable {
    private final Handler mHandler;
    private final LocationControlBuffered mLocationControlBuffered;
    private final MeterWrapper mMeterWrapper;
    private final TextLagUpdater mTextLagUpdater;

    UpdateGpsWidgetRunnable(Handler handler,
            LocationControlBuffered locationControlBuffered, MeterWrapper meterWrapper,
            TextLagUpdater textLagUpdater) {
        mMeterWrapper = meterWrapper;
        mLocationControlBuffered = locationControlBuffered;
        mTextLagUpdater = textLagUpdater;
        mHandler = handler;
    }

    public void run() {
        // Update the lag time and the orientation.
        mTextLagUpdater.updateTextLag();
        mMeterWrapper.setAzimuth(mLocationControlBuffered.getAzimuth());
        mHandler.postDelayed(this, 500);
    }
}