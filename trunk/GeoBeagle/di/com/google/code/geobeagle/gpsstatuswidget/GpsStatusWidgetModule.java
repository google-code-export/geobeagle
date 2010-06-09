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
import com.google.code.geobeagle.gpsstatuswidget.GpsStatusWidget.InflatedGpsStatusWidget;
import com.google.code.geobeagle.gpsstatuswidget.GpsStatusWidgetDelegate.GpsStatusWidgetDelegateFactory;
import com.google.code.geobeagle.gpsstatuswidget.MeterFader.MeterFaderFactory;
import com.google.code.geobeagle.gpsstatuswidget.TextLagUpdater.TextLagUpdaterFactory;
import com.google.code.geobeagle.gpsstatuswidget.UpdateGpsWidgetRunnable.UpdateGpsWidgetRunnableFactory;
import com.google.inject.BindingAnnotation;
import com.google.inject.Key;
import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryProvider;

import roboguice.config.AbstractAndroidModule;
import roboguice.inject.ContextScoped;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

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
        @Lag
        TextView providesLagView(@GpsStatusWidgetView View gpsStatusWidget) {
            return (TextView)gpsStatusWidget.findViewById(R.id.lag);
        }

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
    }

    static class GpsStatusWidgetCacheListModule extends GpsStatusWidgetPrivateModule {
        @Override
        protected void configure() {
            bind(GpsWidgetAndUpdater.class).annotatedWith(CacheList.class).to(
                    GpsWidgetAndUpdater.class);
            expose(GpsWidgetAndUpdater.class).annotatedWith(CacheList.class);
            bind(View.class).annotatedWith(GpsStatusWidgetView.class).to(GpsStatusWidget.class);
        }
    }

    static class GpsStatusWidgetSearchOnlineModule extends GpsStatusWidgetPrivateModule {
        @Override
        protected void configure() {
            bind(GpsWidgetAndUpdater.class).annotatedWith(SearchOnline.class).to(
                    GpsWidgetAndUpdater.class);
            expose(GpsWidgetAndUpdater.class).annotatedWith(SearchOnline.class);
            bind(View.class).annotatedWith(GpsStatusWidgetView.class).to(
                    Key.get(InflatedGpsStatusWidget.class, SearchOnline.class));
        }
    }

    @BindingAnnotation @Target({ FIELD, PARAMETER, METHOD }) @Retention(RUNTIME)
    public static @interface Lag {}
    @Override
    protected void configure() {
        bind(TextLagUpdaterFactory.class).toProvider(
                FactoryProvider.newFactory(TextLagUpdaterFactory.class, TextLagUpdater.class));
        bind(UpdateGpsWidgetRunnableFactory.class).toProvider(
                FactoryProvider.newFactory(UpdateGpsWidgetRunnableFactory.class,
                        UpdateGpsWidgetRunnable.class));
        bind(MeterFaderFactory.class).toProvider(
                FactoryProvider.newFactory(MeterFaderFactory.class, MeterFader.class));
        bind(GpsStatusWidgetDelegateFactory.class).toProvider(
                FactoryProvider.newFactory(GpsStatusWidgetDelegateFactory.class,
                        GpsStatusWidgetDelegate.class));
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
