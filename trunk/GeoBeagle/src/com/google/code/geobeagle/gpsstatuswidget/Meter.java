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
import com.google.code.geobeagle.formatting.DistanceFormatter;
import com.google.inject.Inject;

import roboguice.inject.ContextScoped;

import android.widget.TextView;

@ContextScoped
class Meter {
    private float mAccuracy;
    private final TextView mAccuracyView;
    private float mAzimuth;
    private final MeterBars mMeterView;

    @Inject
    Meter(MeterBars meterBars, InflatedGpsStatusWidget inflatedGpsStatusWidget) {
        mAccuracyView = ((TextView)inflatedGpsStatusWidget.findViewById(R.id.accuracy));
        mMeterView = meterBars;
    }

    void setAccuracy(float accuracy, DistanceFormatter distanceFormatter) {
        mAccuracy = accuracy;
        distanceFormatter.formatDistance(accuracy);
        mAccuracyView.setText(distanceFormatter.formatDistance(accuracy));
        mMeterView.set(accuracy, mAzimuth);
    }

    void setAzimuth(float azimuth) {
        mAzimuth = azimuth;
        mMeterView.set(mAccuracy, azimuth);
    }

    void setDisabled() {
        mAccuracyView.setText("");
        mMeterView.set(Float.MAX_VALUE, 0);
    }
}
