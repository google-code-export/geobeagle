
package com.google.code.geobeagle.gpsstatuswidget;

import com.google.code.geobeagle.formatting.DistanceFormatter;
import com.google.code.geobeagle.gpsstatuswidget.GpsStatusWidgetModule.LocationProvider;
import com.google.code.geobeagle.gpsstatuswidget.GpsStatusWidgetModule.Status;
import com.google.code.geobeagle.location.CombinedLocationManager;
import com.google.inject.Inject;
import com.google.inject.Provider;

import android.content.Context;
import android.widget.TextView;

public class GpsWidgetAndUpdater {
    private final GpsStatusWidgetDelegate mGpsStatusWidgetDelegate;
    private final UpdateGpsWidgetRunnable mUpdateGpsRunnable;

    @Inject
    public GpsWidgetAndUpdater(Context context, CombinedLocationManager combinedLocationManager,
            Provider<DistanceFormatter> distanceFormatterProvider, Meter meter,
            TextLagUpdater textLagUpdater, UpdateGpsWidgetRunnable updateGpsRunnable,
            @Status TextView status, @LocationProvider TextView provider, MeterFader meterFader) {
        mUpdateGpsRunnable = updateGpsRunnable;
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
