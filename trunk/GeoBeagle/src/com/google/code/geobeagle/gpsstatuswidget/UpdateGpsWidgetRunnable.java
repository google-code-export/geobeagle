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

import com.google.code.geobeagle.LocationControlBuffered;
import com.google.code.geobeagle.activity.cachelist.ActivityVisible;
import com.google.inject.Inject;
import com.google.inject.Provider;

import android.os.Handler;

public class UpdateGpsWidgetRunnable implements Runnable {
    private final Handler mHandler;
    private final Provider<LocationControlBuffered> mLocationControlBufferedProvider;
    private final Meter mMeterWrapper;
    private final TextLagUpdater mTextLagUpdater;
    private final ActivityVisible mActivityVisible;

    @Inject
    UpdateGpsWidgetRunnable(Handler handler,
            Provider<LocationControlBuffered> locationControlBufferedProvider,
            Meter meter, TextLagUpdater textLagUpdater, ActivityVisible activityVisible) {
        mLocationControlBufferedProvider = locationControlBufferedProvider;
        mMeterWrapper = meter;
        mTextLagUpdater = textLagUpdater;
        mHandler = handler;
        mActivityVisible = activityVisible;
    }

    @Override
    public void run() {
        if (!mActivityVisible.getVisible())
            return;
        // Update the lag time and the orientation.
        mTextLagUpdater.updateTextLag();
        LocationControlBuffered locationControlBuffered = mLocationControlBufferedProvider.get();
        mMeterWrapper.setAzimuth(locationControlBuffered.getAzimuth());
        mHandler.postDelayed(this, 500);
    }
}
