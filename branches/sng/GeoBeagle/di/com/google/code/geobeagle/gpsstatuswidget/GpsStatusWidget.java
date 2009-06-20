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
import com.google.code.geobeagle.ResourceProvider;
import com.google.code.geobeagle.Time;
import com.google.code.geobeagle.activity.cachelist.model.LocationControlBuffered;
import com.google.code.geobeagle.activity.cachelist.presenter.DistanceFormatter;
import com.google.code.geobeagle.activity.cachelist.presenter.HasDistanceFormatter;
import com.google.code.geobeagle.location.CombinedLocationManager;

import android.content.Context;
import android.graphics.Canvas;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author sng Displays the GPS status (mAccuracy, availability, etc).
 */
public class GpsStatusWidget extends LinearLayout implements LocationListener, HasDistanceFormatter {

    public static GpsStatusWidget CreateStatusWidget(Context context,
            LocationControlBuffered locationControlBuffered,
            CombinedLocationManager locationManager, DistanceFormatter distanceFormatter,
            View gpsWidgetView) {
        final Time time = new Time();
        final ResourceProvider resourceProvider = new ResourceProvider(context);
        final MeterFormatter meterFormatter = new MeterFormatter(context);
        return new GpsStatusWidget(context, locationControlBuffered, locationManager,
                meterFormatter, resourceProvider, time, distanceFormatter, gpsWidgetView);
    }

    public static UpdateGpsWidgetRunnable CreateUpdateGpsWidgetRunnable(
            GpsStatusWidget gpsStatusWidget, LocationControlBuffered locationControlBuffered) {
        final Handler handler = new Handler();
        final MeterWrapper meterWrapper = gpsStatusWidget.getMeterWrapper();
        final TextLagUpdater textLagUpdater = gpsStatusWidget.getTextLagUpdater();

        return new UpdateGpsWidgetRunnable(handler, locationControlBuffered, meterWrapper,
                textLagUpdater);
    }

    private final GpsStatusWidgetDelegate mGpsStatusWidgetDelegate;
    private final MeterWrapper mMeterWrapper;
    private final TextLagUpdater mTextLagUpdater;

    public GpsStatusWidget(Context context, LocationControlBuffered locationControlBuffered,
            CombinedLocationManager locationManager, MeterFormatter meterFormatter,
            ResourceProvider resourceProvider, Time time, DistanceFormatter distanceFormatter,
            View gpsWidgetView) {
        super(context);
        if (gpsWidgetView == null) {
            final LayoutInflater inflater = (LayoutInflater)context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            gpsWidgetView = inflater.inflate(R.layout.gps_widget, this);
        }
        final TextView accuracyView = (TextView)gpsWidgetView.findViewById(R.id.accuracy);
        final TextView lag = (TextView)gpsWidgetView.findViewById(R.id.lag);
        final TextView provider = (TextView)gpsWidgetView.findViewById(R.id.provider);
        final TextView status = (TextView)gpsWidgetView.findViewById(R.id.status);
        final TextView locationViewer = (TextView)gpsWidgetView.findViewById(R.id.location_viewer);

        final MeterView meterView = new MeterView(locationViewer, meterFormatter);
        mMeterWrapper = new MeterWrapper(meterView, accuracyView);
        final MeterFader meterFader = new MeterFader(this, meterView, time);
        mTextLagUpdater = new TextLagUpdater(locationManager, lag, time);

        mGpsStatusWidgetDelegate = new GpsStatusWidgetDelegate(locationManager, meterFader,
                mMeterWrapper, provider, resourceProvider, status, mTextLagUpdater,
                distanceFormatter);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        mGpsStatusWidgetDelegate.mMeterFader.paint();
        // Log.v("GeoBeagle", "painting " + lastUpdateLag);
        // Log.v("GeoBeagle", "dispatch draw");
        super.dispatchDraw(canvas);
    }

    public GpsStatusWidgetDelegate getGpsStatusWidgetDelegate() {
        return mGpsStatusWidgetDelegate;
    }

    public MeterWrapper getMeterWrapper() {
        return mMeterWrapper;
    }

    public TextLagUpdater getTextLagUpdater() {
        return mTextLagUpdater;
    };

    public void onLocationChanged(Location location) {
        mGpsStatusWidgetDelegate.onLocationChanged(location);
    }

    public void onProviderDisabled(String provider) {
        mGpsStatusWidgetDelegate.onProviderDisabled(provider);
    }

    public void onProviderEnabled(String provider) {
        mGpsStatusWidgetDelegate.onProviderEnabled(provider);
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        mGpsStatusWidgetDelegate.onStatusChanged(provider, status, extras);
    }

    public void setDistanceFormatter(DistanceFormatter distanceFormatter) {
        mGpsStatusWidgetDelegate.setDistanceFormatter(distanceFormatter);
    }
}
