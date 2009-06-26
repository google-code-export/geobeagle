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

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

class MeterView {
    private final MeterFormatter mMeterFormatter;
    private final TextView mTextView;

    MeterView(TextView textView, MeterFormatter meterFormatter) {
        mTextView = textView;
        mMeterFormatter = meterFormatter;
    }

    void set(float accuracy, float azimuth) {
        final String center = String.valueOf((int)azimuth);
        final int barCount = mMeterFormatter.accuracyToBarCount(accuracy);
        final String barsToMeterText = mMeterFormatter.barsToMeterText(barCount, center);
        mTextView.setText(barsToMeterText);
    }

    void setLag(long lag) {
        mTextView.setTextColor(Color.argb(mMeterFormatter.lagToAlpha(lag), 147, 190, 38));
    }

    public static MeterView create(Context context, View gpsWidget) {
        final MeterFormatter meterFormatter = new MeterFormatter(context);
        final TextView locationViewer = (TextView)gpsWidget.findViewById(R.id.location_viewer);
        return new MeterView(locationViewer, meterFormatter);
    }
}
