
package com.google.code.geobeagle.gpsstatuswidget;

import com.google.code.geobeagle.LocationControlBuffered;
import com.google.code.geobeagle.ResourceProvider;
import com.google.code.geobeagle.Time;
import com.google.code.geobeagle.formatting.DistanceFormatter;
import com.google.code.geobeagle.location.CombinedLocationManager;

import android.content.Context;
import android.os.Handler;
import android.view.View;

public class GpsWidgetAndUpdater {
    private final GpsStatusWidgetDelegate mGpsStatusWidgetDelegate;
    private final UpdateGpsWidgetRunnable mUpdateGpsRunnable;

    public GpsWidgetAndUpdater(Context context, View gpsWidgetView,
            LocationControlBuffered mLocationControlBuffered,
            CombinedLocationManager combinedLocationManager,
            DistanceFormatter distanceFormatterMetric) {
        final ResourceProvider resourceProvider = new ResourceProvider(context);
        final Time time = new Time();
        final Handler handler = new Handler();
        final MeterView meterView = MeterView.create(context, gpsWidgetView);
        final MeterWrapper meterWrapper = MeterWrapper.create(gpsWidgetView, meterView);
        final TextLagUpdater textLagUpdater = TextLagUpdater.createTextLagUpdater(gpsWidgetView,
                combinedLocationManager, time);
        mUpdateGpsRunnable = new UpdateGpsWidgetRunnable(handler, mLocationControlBuffered,
                meterWrapper, textLagUpdater);
        mGpsStatusWidgetDelegate = GpsStatusWidgetDelegate.createGpsStatusWidgetDelegate(
                gpsWidgetView, time, combinedLocationManager, meterWrapper, resourceProvider,
                distanceFormatterMetric, meterView, textLagUpdater);
    }

    public GpsStatusWidgetDelegate getGpsStatusWidgetDelegate() {
        return mGpsStatusWidgetDelegate;
    }

    public UpdateGpsWidgetRunnable getUpdateGpsWidgetRunnable() {
        return mUpdateGpsRunnable;
    }
}
