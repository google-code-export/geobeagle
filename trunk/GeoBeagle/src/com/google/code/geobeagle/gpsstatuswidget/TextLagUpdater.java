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
import com.google.inject.Inject;

import android.location.Location;
import android.widget.TextView;

import java.util.Formatter;

class TextLagUpdater {
    static interface Lag {
        String getFormatted(long currentTime);
    }

    static class LagImpl implements Lag {
        private final long mLastTextLagUpdateTime;

        LagImpl(long lastTextLagUpdateTime) {
            mLastTextLagUpdateTime = lastTextLagUpdateTime;
        }

        @Override
        public String getFormatted(long currentTime) {
            return formatTime((currentTime - mLastTextLagUpdateTime) / 1000);
        }
    }

    static class LagNull implements Lag {

        @Override
        public String getFormatted(long currentTime) {
            return "";
        }
    }

    static class LastKnownLocation implements LastLocation {
        private final LagImpl mLagImpl;

        public LastKnownLocation(long time) {
            mLagImpl = new LagImpl(time);
        }

        public Lag getLag() {
            return mLagImpl;
        }
    }

    static class LastKnownLocationUnavailable implements LastLocation {
        private final Lag mLagNull;

        @Inject
        public LastKnownLocationUnavailable(LagNull lagNull) {
            mLagNull = lagNull;
        }

        public Lag getLag() {
            return mLagNull;
        }
    }

    static interface LastLocation {
        Lag getLag();
    }

    static class LastLocationUnknown implements LastLocation {
        private final CombinedLocationManager mCombinedLocationManager;
        private final LastKnownLocationUnavailable mLastKnownLocationUnavailable;

        @Inject
        public LastLocationUnknown(CombinedLocationManager combinedLocationManager,
                LastKnownLocationUnavailable lastKnownLocationUnavailable) {
            mCombinedLocationManager = combinedLocationManager;
            mLastKnownLocationUnavailable = lastKnownLocationUnavailable;
        }

        @Override
        public Lag getLag() {
            return getLastLocation(mCombinedLocationManager.getLastKnownLocation()).getLag();
        }

        private LastLocation getLastLocation(Location lastKnownLocation) {
            if (lastKnownLocation == null)
                return mLastKnownLocationUnavailable;
            return new LastKnownLocation(lastKnownLocation.getTime());
        }
    }

    private final static StringBuilder aStringBuilder = new StringBuilder();
    private final static Formatter mFormatter = new Formatter(aStringBuilder);

    static String formatTime(long l) {
        aStringBuilder.setLength(0);
        if (l < 60) {
            return mFormatter.format("%ds", l).toString();
        } else if (l < 3600) {
            return mFormatter.format("%dm %ds", l / 60, l % 60).toString();
        }
        return mFormatter.format("%dh %dm", l / 3600, (l % 3600) / 60).toString();
    }

    private LastLocation mLastLocation;
    private final TextView mTextLag;
    private final Time mTime;

    TextLagUpdater(LastLocationUnknown lastLocationUnknown, TextView textLag, Time time) {
        mLastLocation = lastLocationUnknown;
        mTextLag = textLag;
        mTime = time;
    }

    void reset(long time) {
        mLastLocation = new LastKnownLocation(time);
    }

    void setDisabled() {
        mTextLag.setText("");
    }

    void updateTextLag() {
        mTextLag.setText(mLastLocation.getLag().getFormatted(mTime.getCurrentTime()));
    }
}
