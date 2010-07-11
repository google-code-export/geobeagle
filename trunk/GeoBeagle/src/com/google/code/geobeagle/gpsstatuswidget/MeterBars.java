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
/*
 * Displays the accuracy (graphically) and azimuth of the gps.
 */

import android.graphics.Color;
import android.widget.TextView;

class MeterBars {
    private final TextView mBarsAndAzimuth;
    private final MeterFormatter mMeterFormatter;

    MeterBars(TextView textView, MeterFormatter meterFormatter) {
        mBarsAndAzimuth = textView;
        mMeterFormatter = meterFormatter;
    }

    void set(float accuracy, float azimuth) {
        final String center = String.valueOf((int)azimuth);
        final int barCount = mMeterFormatter.accuracyToBarCount(accuracy);
        final String barsToMeterText = mMeterFormatter.barsToMeterText(barCount, center);
        mBarsAndAzimuth.setText(barsToMeterText);
    }

    void setLag(long lag) {
        mBarsAndAzimuth.setTextColor(Color.argb(mMeterFormatter.lagToAlpha(lag), 147, 190, 38));
    }
}
