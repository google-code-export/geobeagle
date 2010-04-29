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

import com.google.code.geobeagle.Time;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import android.view.View;

class MeterFader {
    static interface MeterFaderFactory {
        MeterFader create(View parent, MeterBars meterBars);
    }

    private long mLastUpdateTime;
    private final MeterBars mMeterView;
    private final View mParent;
    private final Time mTime;

    @Inject
    MeterFader(@Assisted View parent, @Assisted MeterBars meterBars, Time time) {
        mLastUpdateTime = -1;
        mMeterView = meterBars;
        mParent = parent;
        mTime = time;
    }

    void paint() {
        final long currentTime = mTime.getCurrentTime();
        if (mLastUpdateTime == -1)
            mLastUpdateTime = currentTime;
        long lastUpdateLag = currentTime - mLastUpdateTime;
        mMeterView.setLag(lastUpdateLag);
        if (lastUpdateLag < 1000)
            mParent.postInvalidateDelayed(100);
        // Log.d("GeoBeagle", "painting " + lastUpdateLag);
    }

    void reset() {
        mLastUpdateTime = -1;
        mParent.postInvalidate();
    }
}
