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
import com.google.code.geobeagle.LocationControlBuffered.GpsDisabledLocation;
import com.google.code.geobeagle.LocationControlBuffered.GpsEnabledLocation;
import com.google.code.geobeagle.LocationControlBuffered.IGpsLocation;
import com.google.code.geobeagle.activity.ActivitySaverProvider;
import com.google.code.geobeagle.activity.ActivitySaver;
import com.google.code.geobeagle.activity.cachelist.presenter.DistanceFormatterManager;
import com.google.code.geobeagle.activity.cachelist.presenter.DistanceFormatterManagerProvider;
import com.google.code.geobeagle.activity.cachelist.presenter.DistanceSortStrategy;
import com.google.code.geobeagle.activity.cachelist.presenter.NullSortStrategy;
import com.google.code.geobeagle.activity.searchonline.SearchOnlineActivityDelegate.SearchOnlineActivityDelegateFactory;
import com.google.code.geobeagle.formatting.DistanceFormatter;
import com.google.code.geobeagle.location.CombinedLocationListener;
import com.google.code.geobeagle.location.LocationControl;
import com.google.code.geobeagle.location.CombinedLocationListener.CombinedLocationListenerFactory;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryProvider;

import roboguice.config.AbstractAndroidModule;
import roboguice.inject.ContextScoped;

import android.location.Location;
import android.location.LocationManager;

public class SearchOnlineModule extends AbstractAndroidModule {

    @Override
    protected void configure() {
        bind(DistanceFormatterManager.class).toProvider(DistanceFormatterManagerProvider.class);
        bind(ActivitySaver.class).toProvider(ActivitySaverProvider.class);


        bind(CombinedLocationListenerFactory.class).toProvider(
                FactoryProvider.newFactory(CombinedLocationListenerFactory.class,
                        CombinedLocationListener.class));
        bind(SearchOnlineActivityDelegateFactory.class).toProvider(
                FactoryProvider.newFactory(SearchOnlineActivityDelegateFactory.class,
                        SearchOnlineActivityDelegate.class));
    }

    @Provides
    @ContextScoped
    LocationControlBuffered providesLocationControlBuffered(LocationManager locationManager,
            LocationControl locationControl, NullSortStrategy nullSortStrategy,
            DistanceSortStrategy distanceSortStrategy, GpsDisabledLocation gpsDisabledLocation) {

        IGpsLocation lastGpsLocation;
        final Location lastKnownLocation = locationManager.getLastKnownLocation("gps");
        if (lastKnownLocation == null)
            lastGpsLocation = gpsDisabledLocation;
        else
            lastGpsLocation = new GpsEnabledLocation((float)lastKnownLocation.getLatitude(),
                    (float)lastKnownLocation.getLongitude());
        return new LocationControlBuffered(locationControl, distanceSortStrategy, nullSortStrategy,
                gpsDisabledLocation, lastGpsLocation, lastKnownLocation);
    }

    @Provides
    DistanceFormatter providesDistanceFormatter(DistanceFormatterManager distanceFormatterManager) {
        return distanceFormatterManager.getFormatter();
    }

}
