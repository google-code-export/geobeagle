
package com.google.code.geobeagle.gpsstatuswidget;

import com.google.code.geobeagle.GeoFixProvider;
import com.google.code.geobeagle.Clock;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.formatting.DistanceFormatter;
import com.google.code.geobeagle.gpsstatuswidget.TextLagUpdater.LagNull;
import com.google.code.geobeagle.gpsstatuswidget.TextLagUpdater.LastKnownLocationUnavailable;
import com.google.code.geobeagle.gpsstatuswidget.TextLagUpdater.LastLocationUnknown;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

public class GpsWidgetAndUpdater {
    private final GpsStatusWidgetDelegate mGpsStatusWidgetDelegate;
    private final UpdateGpsWidgetRunnable mUpdateGpsRunnable;

    public GpsWidgetAndUpdater(Context context, View gpsWidgetView,
            GeoFixProvider geoFixProvider,            
            DistanceFormatter distanceFormatterMetric) {
        final Clock clock = new Clock();
        final Handler handler = new Handler();

        final MeterFormatter meterFormatter = new MeterFormatter(context);
        final TextView locationViewer = 
            (TextView)gpsWidgetView.findViewById(R.id.location_viewer);
        final MeterBars meterBars = new MeterBars(locationViewer, meterFormatter);
        
        final TextView accuracyView = (TextView)gpsWidgetView.findViewById(R.id.accuracy);
        final Meter meter = new Meter(meterBars, accuracyView);

        final TextView lag = (TextView)gpsWidgetView.findViewById(R.id.lag);
        final LagNull lagNull = new LagNull();
        final LastKnownLocationUnavailable lastKnownLocationUnavailable = 
            new LastKnownLocationUnavailable(lagNull);
        final LastLocationUnknown lastLocationUnknown = new LastLocationUnknown(
                geoFixProvider, lastKnownLocationUnavailable);
        final TextLagUpdater textLagUpdater = new TextLagUpdater(lastLocationUnknown, lag, clock);
        
        mUpdateGpsRunnable = new UpdateGpsWidgetRunnable(handler, geoFixProvider, meter,
                textLagUpdater);
        
        final TextView status = (TextView)gpsWidgetView.findViewById(R.id.status);
        final TextView provider = (TextView)gpsWidgetView.findViewById(R.id.provider);
        final MeterFader meterFader = new MeterFader(gpsWidgetView, meterBars, clock);
        mGpsStatusWidgetDelegate = new GpsStatusWidgetDelegate(geoFixProvider, 
                distanceFormatterMetric, meter, meterFader, provider, context, 
                status, textLagUpdater);
    }

    public GpsStatusWidgetDelegate getGpsStatusWidgetDelegate() {
        return mGpsStatusWidgetDelegate;
    }

    public UpdateGpsWidgetRunnable getUpdateGpsWidgetRunnable() {
        return mUpdateGpsRunnable;
    }
}
