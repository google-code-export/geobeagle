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

package com.google.code.geobeagle.ui;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.ResourceProvider;
import com.google.code.geobeagle.ui.Misc.Time;

import android.graphics.Color;
import android.location.Location;
import android.location.LocationProvider;
import android.widget.TextView;

/**
 * @author sng Displays the GPS status (accuracy, availability, etc).
 */
public class GpsStatusWidget {
    public static class MeterFormatter {
        public int accuracyToBarCount(float accuracy) {
            return Math.min(METER_LEFT.length(), (int)(Math.log(Math.max(1, accuracy)) / Math
                    .log(2)));
        }

        public String barsToMeterText(int bars) {
            return METER_LEFT.substring(METER_LEFT.length() - bars) + "×"
                    + METER_RIGHT.substring(0, bars);
        }

        public int lagToAlpha(long milliseconds) {
            return Math.max(128, 255 - (int)(milliseconds >> 3));
        }
    }

    public static class MeterView {
        private final MeterFormatter mMeterFormatter;
        private final TextView mTextView;

        public MeterView(TextView textView, MeterFormatter meterFormatter) {
            mTextView = textView;
            mMeterFormatter = meterFormatter;
        }

        public void set(long lag, float accuracy) {
            mTextView.setText(mMeterFormatter.barsToMeterText(mMeterFormatter
                    .accuracyToBarCount(accuracy)));
            mTextView.setTextColor(Color.argb(mMeterFormatter.lagToAlpha(lag), 147, 190, 38));
        }
    }

    public final static String METER_LEFT = "····«····‹····";
    public final static String METER_RIGHT = "····›····»····";
    private float mAccuracy;
    private final TextView mAccuracyView;
    private final TextView mLag;
    private long mLastUpdateTime;
    private long mLocationTime;
    private final MeterView mMeterView;
    private final TextView mProvider;
    private final ResourceProvider mResourceProvider;
    private final TextView mStatus;
    private final Time mTime;

    public GpsStatusWidget(ResourceProvider resourceProvider, MeterView meterView,
            TextView provider, TextView lag, TextView accuracy, TextView status, Time time,
            Location initialLocation) {
        mResourceProvider = resourceProvider;
        mMeterView = meterView;
        mLag = lag;
        mAccuracyView = accuracy;
        mProvider = provider;
        mStatus = status;
        mTime = time;
    }

    public void refreshLocation() {
        long currentTime = mTime.getCurrentTime();
        long lastUpdateLag = currentTime - mLastUpdateTime;
        long locationLag = currentTime - mLocationTime;
        mLag.setText((locationLag / 1000 + "s").trim());
        mAccuracyView.setText((mAccuracy + "m").trim());
        mMeterView.set(lastUpdateLag, mAccuracy);
    };

    public void setDisabled() {
        mStatus.setText("DISABLED");
    }

    public void setEnabled() {
        mStatus.setText("ENABLED");
    }

    public void setLocation(Location location) {
        // TODO: use currentTime for alpha channel, but locationTime for text
        // lag.
        mLastUpdateTime = mTime.getCurrentTime();
        mLocationTime = Math.min(location.getTime(), mLastUpdateTime);
        mProvider.setText(location.getProvider());
        mAccuracy = location.getAccuracy();
    }

    public void setStatus(String provider, int status) {
        switch (status) {
            case LocationProvider.OUT_OF_SERVICE:
                mStatus.setText(provider + " status: "
                        + mResourceProvider.getString(R.string.out_of_service));
                break;
            case LocationProvider.AVAILABLE:
                mStatus.setText(provider + " status: "
                        + mResourceProvider.getString(R.string.available));
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                mStatus.setText(provider + " status: "
                        + mResourceProvider.getString(R.string.temporarily_unavailable));
                break;
        }
    }
}
