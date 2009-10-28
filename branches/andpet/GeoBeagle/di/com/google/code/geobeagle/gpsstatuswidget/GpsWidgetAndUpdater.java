
package com.google.code.geobeagle.gpsstatuswidget;

import com.google.code.geobeagle.LocationControlBuffered;
import com.google.code.geobeagle.Clock;
import com.google.code.geobeagle.formatting.DistanceFormatter;

import android.content.Context;
import android.os.Handler;
import android.view.View;

public class GpsWidgetAndUpdater {
    private final GpsStatusWidgetDelegate mGpsStatusWidgetDelegate;
    private final UpdateGpsWidgetRunnable mUpdateGpsRunnable;

    public GpsWidgetAndUpdater(Context context, View gpsWidgetView,
            LocationControlBuffered locationControlBuffered,            
            DistanceFormatter distanceFormatterMetric) {
        final Clock time = new Clock();
        final Handler handler = new Handler();
        final MeterBars meterBars = GpsStatusWidget.create(context, gpsWidgetView);
        final Meter meter = GpsStatusWidget.createMeterWrapper(gpsWidgetView, meterBars);
        final TextLagUpdater textLagUpdater = GpsStatusWidget.createTextLagUpdater(gpsWidgetView,
                locationControlBuffered, time);
        mUpdateGpsRunnable = new UpdateGpsWidgetRunnable(handler, locationControlBuffered, meter,
                textLagUpdater);
        mGpsStatusWidgetDelegate = GpsStatusWidget.createGpsStatusWidgetDelegate(gpsWidgetView,
                time, locationControlBuffered, meter, distanceFormatterMetric, meterBars,
                textLagUpdater, context);
    }

    public GpsStatusWidgetDelegate getGpsStatusWidgetDelegate() {
        return mGpsStatusWidgetDelegate;
    }

    public UpdateGpsWidgetRunnable getUpdateGpsWidgetRunnable() {
        return mUpdateGpsRunnable;
    }
}
