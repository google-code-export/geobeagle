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

import java.util.Formatter;

class TextLagUpdater {
    private long mLastTextLagUpdateTime;
    private final TextView mTextLag;
    private final Time mTime;
    private final CombinedLocationManager mCombinedLocationManager;
    private final static StringBuilder stringBuilder = new StringBuilder();
    private final static Formatter mFormatter = new Formatter(stringBuilder);

    TextLagUpdater(CombinedLocationManager combinedLocationManager, TextView textLag, Time time) {
        mCombinedLocationManager = combinedLocationManager;
        mTextLag = textLag;
        mTime = time;
    }

    static String formatTime(long l) {
        stringBuilder.setLength(0);
        if (l < 60) {
            return mFormatter.format("%ds", l).toString();
        } else if (l < 3600) {
            return mFormatter.format("%dm %ds", l / 60, l % 60).toString();
        }
        return mFormatter.format("%dh %dm", l / 3600, (l % 3600) / 60).toString();
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
