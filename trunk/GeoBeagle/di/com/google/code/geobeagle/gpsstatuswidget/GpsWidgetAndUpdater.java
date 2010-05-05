
package com.google.code.geobeagle.gpsstatuswidget;

import com.google.code.geobeagle.LocationControlBuffered;
import com.google.code.geobeagle.Time;
import com.google.code.geobeagle.formatting.DistanceFormatter;
import com.google.code.geobeagle.location.CombinedLocationManager;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import android.content.Context;
import android.os.Handler;
import android.view.View;

public class GpsWidgetAndUpdater {
    private final GpsStatusWidgetDelegate mGpsStatusWidgetDelegate;
    private final UpdateGpsWidgetRunnable mUpdateGpsRunnable;

    public interface GpsWidgetAndUpdaterFactory {
        public GpsWidgetAndUpdater create(View gpsWidgetView);
    }

    @Inject
    public GpsWidgetAndUpdater(Context context, @Assisted View gpsWidgetView,
            LocationControlBuffered mLocationControlBuffered,
            CombinedLocationManager combinedLocationManager,
            DistanceFormatter distanceFormatter) {
        final Time time = new Time();
        final Handler handler = new Handler();
        final MeterBars meterBars = GpsStatusWidget.create(context, gpsWidgetView);
        final Meter meter = GpsStatusWidget.createMeterWrapper(gpsWidgetView, meterBars);
        final TextLagUpdater textLagUpdater = GpsStatusWidget.createTextLagUpdater(gpsWidgetView,
                combinedLocationManager, time);
        mUpdateGpsRunnable = new UpdateGpsWidgetRunnable(handler, mLocationControlBuffered, meter,
                textLagUpdater);
        mGpsStatusWidgetDelegate = GpsStatusWidget.createGpsStatusWidgetDelegate(gpsWidgetView,
                time, combinedLocationManager, meter, distanceFormatter, meterBars,
                textLagUpdater, context);
    }

    public GpsStatusWidgetDelegate getGpsStatusWidgetDelegate() {
        return mGpsStatusWidgetDelegate;
    }

    public UpdateGpsWidgetRunnable getUpdateGpsWidgetRunnable() {
        return mUpdateGpsRunnable;
    }
}
