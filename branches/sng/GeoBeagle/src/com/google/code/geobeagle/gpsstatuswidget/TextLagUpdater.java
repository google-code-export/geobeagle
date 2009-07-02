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
import com.google.code.geobeagle.location.CombinedLocationManager;

import android.widget.TextView;

class TextLagUpdater {
    private long mLastTextLagUpdateTime;
    private final TextView mTextLag;
    private final Time mTime;
    private final CombinedLocationManager mCombinedLocationManager;

    TextLagUpdater(CombinedLocationManager combinedLocationManager, TextView textLag, Time time) {
        mCombinedLocationManager = combinedLocationManager;
        mTextLag = textLag;
        mTime = time;
    }

    static String formatTime(long l) {
        if (l < 60)
            return String.valueOf(l) + "s";
        else if (l < 3600)
            return String.valueOf(l / 60) + "m " + String.valueOf(l % 60) + "s";
        return String.valueOf(l / 3600) + "h " + String.valueOf((l % 3600) / 60) + "m";
    }

    void reset(long time) {
        mLastTextLagUpdateTime = time;
    }

    void setDisabled() {
        mTextLag.setText("");
    }

    void updateTextLag() {
        final long lag = mTime.getCurrentTime() - mLastTextLagUpdateTime;
        if (mCombinedLocationManager.isProviderEnabled()) {
            mTextLag.setText(formatTime(lag / 1000));
        }
    }
}
