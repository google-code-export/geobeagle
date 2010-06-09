
package com.google.code.geobeagle.gpsstatuswidget;

import com.google.code.geobeagle.Time;
import com.google.code.geobeagle.formatting.DistanceFormatter;
import com.google.code.geobeagle.gpsstatuswidget.GpsStatusWidgetModule.GpsStatusWidgetView;
import com.google.code.geobeagle.gpsstatuswidget.GpsStatusWidgetModule.LocationProvider;
import com.google.code.geobeagle.gpsstatuswidget.GpsStatusWidgetModule.Status;
import com.google.code.geobeagle.location.CombinedLocationManager;
import com.google.inject.Inject;
import com.google.inject.Provider;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

public class GpsWidgetAndUpdater {
    private final GpsStatusWidgetDelegate mGpsStatusWidgetDelegate;
    private final UpdateGpsWidgetRunnable mUpdateGpsRunnable;

    @Inject
    public GpsWidgetAndUpdater(Context context, @GpsStatusWidgetView View gpsWidgetView,
            CombinedLocationManager combinedLocationManager,
            Provider<DistanceFormatter> distanceFormatterProvider, Time time, MeterBars meterBars,
            Meter meter, TextLagUpdater textLagUpdater, UpdateGpsWidgetRunnable updateGpsRunnable,
            @Status TextView status, @LocationProvider TextView provider) {
        mUpdateGpsRunnable = updateGpsRunnable;
        final MeterFader meterFader = new MeterFader(gpsWidgetView, meterBars, time);
        mGpsStatusWidgetDelegate = new GpsStatusWidgetDelegate(combinedLocationManager, distanceFormatterProvider,
        meter, meterFader, provider, context, status, textLagUpdater);
    }

    public GpsStatusWidgetDelegate getGpsStatusWidgetDelegate() {
        return mGpsStatusWidgetDelegate;
    }

    public UpdateGpsWidgetRunnable getUpdateGpsWidgetRunnable() {
        return mUpdateGpsRunnable;
    }
}
