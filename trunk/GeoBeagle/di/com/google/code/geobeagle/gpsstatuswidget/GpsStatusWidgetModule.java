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

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.Time;
import com.google.code.geobeagle.formatting.DistanceFormatter;
import com.google.code.geobeagle.gpsstatuswidget.GpsStatusWidget.InflatedGpsStatusWidget;
import com.google.code.geobeagle.gpsstatuswidget.TextLagUpdater.LastLocationUnknown;
import com.google.code.geobeagle.location.CombinedLocationManager;
import com.google.inject.BindingAnnotation;
import com.google.inject.Key;
import com.google.inject.PrivateModule;
import com.google.inject.Provider;
import com.google.inject.Provides;

import roboguice.config.AbstractAndroidModule;
import roboguice.inject.ContextScoped;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

public class GpsStatusWidgetModule extends AbstractAndroidModule {
    @BindingAnnotation @Target({ FIELD, PARAMETER, METHOD }) @Retention(RUNTIME)
    public static @interface CacheList {}

    @BindingAnnotation @Target({ FIELD, PARAMETER, METHOD }) @Retention(RUNTIME)
    public static @interface LocationViewer {}
    
    @BindingAnnotation @Target({ FIELD, PARAMETER, METHOD }) @Retention(RUNTIME)
    public static @interface SearchOnline {}

    @BindingAnnotation @Target({ FIELD, PARAMETER, METHOD }) @Retention(RUNTIME)
    public static @interface GpsStatusWidgetView {}

    static abstract class GpsStatusWidgetPrivateModule extends PrivateModule {
        @Provides
        @LocationViewer
        TextView providesLocationViewer(@GpsStatusWidgetView View gpsStatusWidget) {
            return (TextView)gpsStatusWidget.findViewById(R.id.location_viewer);
        }
        
        @Provides
        @ContextScoped
        Meter providesMeter(MeterBars meterBars, @GpsStatusWidgetView View gpsStatusWidget) {
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
        @ContextScoped
        MeterFader providesMeterFader(@GpsStatusWidgetView View gpsStatusWidget,
                MeterBars meterBars, Time time) {
            return new MeterFader(gpsStatusWidget, meterBars, time);
        }

        @Provides
        GpsStatusWidgetDelegate providesGpsStatusWidgetDelegate(
                CombinedLocationManager combinedLocationManager,
                Provider<DistanceFormatter> distanceFormatterProvider, Meter meter,
                MeterFader meterFader, Context context, TextLagUpdater textLagUpdater,
                @GpsStatusWidgetView View gpsStatusWidget) {
            return new GpsStatusWidgetDelegate(combinedLocationManager, distanceFormatterProvider,
                    meter, meterFader, (TextView)gpsStatusWidget.findViewById(R.id.provider),
                    context, (TextView)gpsStatusWidget.findViewById(R.id.status), textLagUpdater);
        }

        public void configure(Class<? extends Annotation> annotation) {
            bind(GpsStatusWidgetDelegate.class).annotatedWith(annotation).to(
                    GpsStatusWidgetDelegate.class);
            expose(GpsStatusWidgetDelegate.class).annotatedWith(annotation);
            bind(UpdateGpsWidgetRunnable.class).annotatedWith(annotation).to(
                    UpdateGpsWidgetRunnable.class);
            expose(UpdateGpsWidgetRunnable.class).annotatedWith(annotation);
        }
    }

    static class GpsStatusWidgetCacheListModule extends GpsStatusWidgetPrivateModule {
        @Override
        protected void configure() {
            super.configure(CacheList.class);
            bind(View.class).annotatedWith(GpsStatusWidgetView.class).to(GpsStatusWidget.class);
        }
    }

    static class GpsStatusWidgetSearchOnlineModule extends GpsStatusWidgetPrivateModule {
        @Override
        protected void configure() {
            super.configure(SearchOnline.class);
            bind(View.class).annotatedWith(GpsStatusWidgetView.class).to(
                    Key.get(InflatedGpsStatusWidget.class, SearchOnline.class));
        }
    }

    @Override
    protected void configure() {
        bind(GpsStatusWidget.class).in(ContextScoped.class);
        install(new GpsStatusWidgetSearchOnlineModule());
        install(new GpsStatusWidgetCacheListModule());
    }

    @Provides
    @ContextScoped
    @CacheList
    InflatedGpsStatusWidget providesInflatedGpsStatusWidget(Context context) {
        return new InflatedGpsStatusWidget(context);
    }

    @Provides
    @ContextScoped
    @SearchOnline
    InflatedGpsStatusWidget providesInflatedGpsStatusWidget(Activity activity) {
        return (InflatedGpsStatusWidget)activity.findViewById(R.id.gps_widget_view);
    }
    
}
