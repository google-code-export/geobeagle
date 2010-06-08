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
import com.google.code.geobeagle.formatting.DistanceFormatter;
import com.google.code.geobeagle.location.CombinedLocationManager;
import com.google.inject.Inject;
import com.google.inject.Provider;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author sng Displays the GPS status (mAccuracy, availability, etc).
 */
public class GpsStatusWidget extends LinearLayout {

    static GpsStatusWidgetDelegate createGpsStatusWidgetDelegate(View gpsStatusWidget, Time time,
            CombinedLocationManager combinedLocationManager, Meter meter,
            Provider<DistanceFormatter> distanceFormatterProvider, MeterBars meterBars,
            TextLagUpdater textLagUpdater, Context parent) {
        final TextView status = (TextView)gpsStatusWidget.findViewById(R.id.status);
        final TextView provider = (TextView)gpsStatusWidget.findViewById(R.id.provider);
        final MeterFader meterFader = new MeterFader(gpsStatusWidget, meterBars, time);

        return new GpsStatusWidgetDelegate(combinedLocationManager, distanceFormatterProvider,
                meter, meterFader, provider, parent, status, textLagUpdater);
    }

    public static class InflatedGpsStatusWidget extends LinearLayout {
        private GpsStatusWidgetDelegate mGpsStatusWidgetDelegate;

        public InflatedGpsStatusWidget(Context context) {
            super(context);
            LayoutInflater.from(context).inflate(R.layout.gps_widget, this, true);
        }

        public InflatedGpsStatusWidget(Context context, AttributeSet attrs) {
            super(context, attrs);
            LayoutInflater.from(context).inflate(R.layout.gps_widget, this, true);
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            super.dispatchDraw(canvas);
            mGpsStatusWidgetDelegate.paint();
        }

        public void setDelegate(GpsStatusWidgetDelegate gpsStatusWidgetDelegate) {
            mGpsStatusWidgetDelegate = gpsStatusWidgetDelegate;
        }
    }

    @Inject
    public GpsStatusWidget(Context context) {
        super(context);
    }
}
