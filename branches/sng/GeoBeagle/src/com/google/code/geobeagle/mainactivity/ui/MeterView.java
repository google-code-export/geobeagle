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

package com.google.code.geobeagle.mainactivity.ui;


import android.graphics.Color;
import android.widget.TextView;

public class MeterView {
    public static class MeterFormatter {
        public int accuracyToBarCount(float accuracy) {
            return Math.min(MeterView.METER_LEFT.length(), (int)(Math.log(Math.max(1, accuracy)) / Math
                    .log(2)));
        }
    
        public String barsToMeterText(int bars, String center) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append('[').append(MeterView.METER_LEFT.substring(MeterView.METER_LEFT.length() - bars))
                    .append(center).append(MeterView.METER_RIGHT.substring(0, bars)).append(']');
            return stringBuilder.toString();
        }
    
        public int lagToAlpha(long milliseconds) {
            return Math.max(128, 255 - (int)(milliseconds >> 3));
        }
    }

    private final MeterView.MeterFormatter mMeterFormatter;
    private final TextView mTextView;
    public final static String METER_RIGHT = "····›····»····";
    public final static String METER_LEFT = "····«····‹····";

    public MeterView(TextView textView, MeterView.MeterFormatter meterFormatter) {
        mTextView = textView;
        mMeterFormatter = meterFormatter;
    }

    public void set(float accuracy, float azimuth) {
        mTextView.setText(mMeterFormatter.barsToMeterText(mMeterFormatter
                .accuracyToBarCount(accuracy), String.valueOf((int)azimuth) + "°"));
    }

    public void setLag(long lag) {
        mTextView.setTextColor(Color.argb(mMeterFormatter.lagToAlpha(lag), 147, 190, 38));
    }
}