
package com.google.code.geobeagle.gpsstatuswidget;

import com.google.code.geobeagle.GeoFixProvider;
import com.google.code.geobeagle.Clock;
import com.google.code.geobeagle.formatting.DistanceFormatter;

import android.content.Context;
import android.os.Handler;
import android.view.View;

public class GpsWidgetAndUpdater {
    private final GpsStatusWidgetDelegate mGpsStatusWidgetDelegate;
    private final UpdateGpsWidgetRunnable mUpdateGpsRunnable;

    public GpsWidgetAndUpdater(Context context, View gpsWidgetView,
            GeoFixProvider geoFixProvider,            
            DistanceFormatter distanceFormatterMetric) {
        final Clock time = new Clock();
        final Handler handler = new Handler();
        final MeterBars meterBars = GpsStatusWidget.create(context, gpsWidgetView);
        final Meter meter = GpsStatusWidget.createMeterWrapper(gpsWidgetView, meterBars);
        final TextLagUpdater textLagUpdater = GpsStatusWidget.createTextLagUpdater(gpsWidgetView,
                geoFixProvider, time);
        mUpdateGpsRunnable = new UpdateGpsWidgetRunnable(handler, geoFixProvider, meter,
                textLagUpdater);
        mGpsStatusWidgetDelegate = GpsStatusWidget.createGpsStatusWidgetDelegate(gpsWidgetView,
                time, geoFixProvider, meter, distanceFormatterMetric, meterBars,
                textLagUpdater, context);
    }

    public GpsStatusWidgetDelegate getGpsStatusWidgetDelegate() {
        return mGpsStatusWidgetDelegate;
    }

    public UpdateGpsWidgetRunnable getUpdateGpsWidgetRunnable() {
        return mUpdateGpsRunnable;
    }
}
