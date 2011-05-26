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

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.Time;
import com.google.inject.Inject;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

class MeterFader {
    private long mLastUpdateTime;
    private final View mParent;
    private final Time mTime;
    private final TextView mBarsAndAzimuth;

    @Inject
    MeterFader(InflatedGpsStatusWidget parent, Time time) {
        mLastUpdateTime = -1;
        mBarsAndAzimuth = (TextView)parent.findViewById(R.id.location_viewer);
        mParent = parent;
        mTime = time;
    }

    void paint() {
        long currentTime = mTime.getCurrentTime();
        if (mLastUpdateTime == -1)
            mLastUpdateTime = currentTime;
        long lastUpdateLag = currentTime - mLastUpdateTime;
        setLag(lastUpdateLag);
        if (lastUpdateLag < 1000)
            mParent.postInvalidateDelayed(100);
        // Log.d("GeoBeagle", "painting " + lastUpdateLag);
    }

    void setLag(long lag) {
        mBarsAndAzimuth.setTextColor(Color.argb(lagToAlpha(lag), 147, 190, 38));
    }

    int lagToAlpha(long milliseconds) {
        return Math.max(128, 255 - (int)(milliseconds >> 3));
    }

    void reset() {
        mLastUpdateTime = -1;
        mParent.postInvalidate();
    }
}
