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

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.google.code.geobeagle.LocationControlBuffered;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.Time;
import com.google.code.geobeagle.activity.cachelist.ActivityVisible;
import com.google.code.geobeagle.formatting.DistanceFormatter;
import com.google.code.geobeagle.gpsstatuswidget.TextLagUpdater.LastLocationUnknown;
import com.google.code.geobeagle.location.CombinedLocationListener;
import com.google.code.geobeagle.location.CombinedLocationManager;
import com.google.inject.BindingAnnotation;
import com.google.inject.Provider;
import com.google.inject.Provides;

import roboguice.config.AbstractAndroidModule;
import roboguice.inject.ContextScoped;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

public class GpsStatusWidgetModule extends AbstractAndroidModule {
    @BindingAnnotation @Target({ FIELD, PARAMETER, METHOD }) @Retention(RUNTIME)
    public static @interface GpsStatusWidgetView {}

    @Provides
    @ContextScoped
    Meter providesMeter(@GpsStatusWidgetView View gpsStatusWidget, MeterFormatter meterFormatter) {
        TextView locationViewer = (TextView)gpsStatusWidget.findViewById(R.id.location_viewer);
        MeterBars meterBars = new MeterBars(locationViewer, meterFormatter);
        return new Meter(meterBars, ((TextView)gpsStatusWidget.findViewById(R.id.accuracy)));
    }

    @Provides
    @ContextScoped
    TextLagUpdater providesTextLagUpdater(LastLocationUnknown lastKnownLocation, Time time,
            @GpsStatusWidgetView View gpsStatusWidget) {
        return new TextLagUpdater(lastKnownLocation, (TextView)gpsStatusWidget
                .findViewById(R.id.lag), time);
    }

    @Provides
    GpsStatusWidgetDelegate providesGpsStatusWidgetDelegate(
            CombinedLocationManager combinedLocationManager,
            Provider<DistanceFormatter> distanceFormatterProvider, Meter meter, Context context,
            TextLagUpdater textLagUpdater, @GpsStatusWidgetView View gpsStatusWidget, Time time) {
        TextView locationViewer = (TextView)gpsStatusWidget.findViewById(R.id.location_viewer);
        MeterFader meterFader = new MeterFader(gpsStatusWidget, locationViewer, time);
        return new GpsStatusWidgetDelegate(combinedLocationManager, distanceFormatterProvider,
                meter, meterFader, (TextView)gpsStatusWidget.findViewById(R.id.provider),
                context, (TextView)gpsStatusWidget.findViewById(R.id.status), textLagUpdater);
    }

    @Provides
    CombinedLocationListener providesCombinedLocationListener(
            LocationControlBuffered locationControlBuffered,
            GpsStatusWidgetDelegate locationListener, ActivityVisible activityVisible) {
        return new CombinedLocationListener(locationControlBuffered, locationListener,
                activityVisible);
    }
    
    @GpsStatusWidgetView
    @Provides
    public View providesGpsStatusWidgetView(Activity activity,
            Provider<InflatedGpsStatusWidget> inflatedGpsStatusWidgetProvider) {
        View inflatedGpsStatusWidget = activity.findViewById(R.id.gps_widget_view);
        if (inflatedGpsStatusWidget == null) {
            return inflatedGpsStatusWidgetProvider.get();
        }
        return inflatedGpsStatusWidget;
    }

    @Override
    protected void configure() {
    }

    @Provides
    @ContextScoped
    LinearLayout providesGpsStatusWidget(Context context) {
        return new LinearLayout(context);
    }

    @Provides
    @ContextScoped
    InflatedGpsStatusWidget providesInflatedGpsStatusWidget(Activity activity, Context context, LinearLayout gpsStatusWidget) {
        
        InflatedGpsStatusWidget inflatedGpsStatusWidget = (InflatedGpsStatusWidget)activity.findViewById(R.id.gps_widget_view);
        if (inflatedGpsStatusWidget != null)
            return inflatedGpsStatusWidget;
        inflatedGpsStatusWidget = new InflatedGpsStatusWidget(context);
        gpsStatusWidget.addView(inflatedGpsStatusWidget, ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        return inflatedGpsStatusWidget;
    }
}
