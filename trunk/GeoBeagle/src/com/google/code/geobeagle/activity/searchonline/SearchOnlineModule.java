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

package com.google.code.geobeagle.activity.searchonline;

import com.google.code.geobeagle.LocationControlBuffered;
import com.google.code.geobeagle.LocationControlProvider;
import com.google.code.geobeagle.activity.ActivityDI;
import com.google.code.geobeagle.activity.ActivitySaver;
import com.google.code.geobeagle.activity.cachelist.presenter.DistanceFormatterManager;
import com.google.code.geobeagle.activity.cachelist.presenter.DistanceFormatterManagerProvider;
import com.google.code.geobeagle.activity.searchonline.SearchOnlineActivityDelegate.SearchOnlineActivityDelegateFactory;
import com.google.code.geobeagle.formatting.DistanceFormatter;
import com.google.code.geobeagle.gpsstatuswidget.GpsWidgetAndUpdater;
import com.google.code.geobeagle.gpsstatuswidget.GpsWidgetAndUpdater.GpsWidgetAndUpdaterFactory;
import com.google.code.geobeagle.location.CombinedLocationListener;
import com.google.code.geobeagle.location.CombinedLocationManager;
import com.google.code.geobeagle.location.CombinedLocationListener.CombinedLocationListenerFactory;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryProvider;

import roboguice.config.AbstractAndroidModule;

import android.location.LocationListener;
import android.location.LocationManager;

import java.util.ArrayList;

public class SearchOnlineModule extends AbstractAndroidModule {

    @Override
    protected void configure() {
        bind(LocationControlBuffered.class).toProvider(LocationControlProvider.class);
        bind(DistanceFormatterManager.class).toProvider(DistanceFormatterManagerProvider.class);
        bind(ActivitySaver.class).toProvider(ActivityDI.class);

        bind(GpsWidgetAndUpdaterFactory.class).toProvider(
                FactoryProvider.newFactory(GpsWidgetAndUpdaterFactory.class,
                        GpsWidgetAndUpdater.class));
        bind(CombinedLocationListenerFactory.class).toProvider(
                FactoryProvider.newFactory(CombinedLocationListenerFactory.class,
                        CombinedLocationListener.class));
        bind(SearchOnlineActivityDelegateFactory.class).toProvider(
                FactoryProvider.newFactory(SearchOnlineActivityDelegateFactory.class,
                        SearchOnlineActivityDelegate.class));
    }

    @Provides
    CombinedLocationManager provideCombinedLocationManager(LocationManager locationManager) {
        final ArrayList<LocationListener> locationListeners = new ArrayList<LocationListener>(3);
        return new CombinedLocationManager(locationManager, locationListeners);
    }

    @Provides
    DistanceFormatter providesDistanceFormatter(DistanceFormatterManager distanceFormatterManager) {
        return distanceFormatterManager.getFormatter();
    }

}
