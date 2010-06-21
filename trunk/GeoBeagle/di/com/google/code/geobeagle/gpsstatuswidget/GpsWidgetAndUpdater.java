
package com.google.code.geobeagle.gpsstatuswidget;

import com.google.code.geobeagle.Time;
import com.google.code.geobeagle.formatting.DistanceFormatter;
import com.google.code.geobeagle.gpsstatuswidget.GpsStatusWidgetModule.GpsStatusWidgetView;
import com.google.code.geobeagle.location.CombinedLocationManager;
import com.google.inject.Inject;
import com.google.inject.Provider;

import android.content.Context;
import android.view.View;

public class GpsWidgetAndUpdater {
    private final GpsStatusWidgetDelegate mGpsStatusWidgetDelegate;
    private final UpdateGpsWidgetRunnable mUpdateGpsRunnable;

    @Inject
    public GpsWidgetAndUpdater(Context context, @GpsStatusWidgetView View gpsWidgetView,
            CombinedLocationManager combinedLocationManager,
            Provider<DistanceFormatter> distanceFormatterProvider, Time time, MeterBars meterBars,
            Meter meter, TextLagUpdater textLagUpdater, UpdateGpsWidgetRunnable updateGpsRunnable) {
        mUpdateGpsRunnable = updateGpsRunnable;
        mGpsStatusWidgetDelegate = GpsStatusWidget.createGpsStatusWidgetDelegate(gpsWidgetView,
                time, combinedLocationManager, meter, distanceFormatterProvider, meterBars,
                textLagUpdater, context);
    }

    public GpsStatusWidgetDelegate getGpsStatusWidgetDelegate() {
        return mGpsStatusWidgetDelegate;
    }

    public UpdateGpsWidgetRunnable getUpdateGpsWidgetRunnable() {
        return mUpdateGpsRunnable;
    }
}
