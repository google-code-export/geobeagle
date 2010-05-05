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

package com.google.code.geobeagle.activity.cachelist;

import com.google.code.geobeagle.LocationControlBuffered.GpsDisabledLocation;
import com.google.code.geobeagle.activity.cachelist.presenter.AbsoluteBearingFormatter;
import com.google.code.geobeagle.activity.cachelist.presenter.ActionAndTolerance;
import com.google.code.geobeagle.activity.cachelist.presenter.AdapterCachesSorter;
import com.google.code.geobeagle.activity.cachelist.presenter.BearingFormatter;
import com.google.code.geobeagle.activity.cachelist.presenter.DistanceUpdater;
import com.google.code.geobeagle.activity.cachelist.presenter.GeocacheListAdapter;
import com.google.code.geobeagle.activity.cachelist.presenter.GeocacheListPresenter;
import com.google.code.geobeagle.activity.cachelist.presenter.LocationAndAzimuthTolerance;
import com.google.code.geobeagle.activity.cachelist.presenter.LocationTolerance;
import com.google.code.geobeagle.activity.cachelist.presenter.RelativeBearingFormatter;
import com.google.code.geobeagle.activity.cachelist.presenter.SqlCacheLoader;
import com.google.code.geobeagle.activity.cachelist.presenter.ToleranceStrategy;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh.ActionManager;
import com.google.code.geobeagle.activity.cachelist.presenter.GeocacheListPresenter.GeocacheListPresenterFactory;
import com.google.code.geobeagle.activity.main.GeoBeagleModule.DefaultSharedPreferences;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryProvider;

import roboguice.config.AbstractAndroidModule;
import roboguice.inject.ContextScoped;

import android.app.Activity;
import android.app.ListActivity;
import android.content.SharedPreferences;

public class CacheListModule extends AbstractAndroidModule {

    @Override
    protected void configure() {
        bind(GeocacheListAdapter.class).in(ContextScoped.class);
        bind(GeocacheListPresenterFactory.class).toProvider(
                FactoryProvider.newFactory(GeocacheListPresenterFactory.class,
                        GeocacheListPresenter.class));
    }

    @Provides
    ListActivity providesListActivity(Activity activity) {
        return (ListActivity)activity;
    }
    
    @Provides
    BearingFormatter providesBearingFormatter(
            @DefaultSharedPreferences SharedPreferences preferenceManager) {
        final boolean absoluteBearing = preferenceManager.getBoolean("absolute-bearing", false);
        if (absoluteBearing)
            return new AbsoluteBearingFormatter();
        return new RelativeBearingFormatter();
    }

    @Provides
    ActionManager providesActionManager(GpsDisabledLocation gpsDisabledLocation,
            AdapterCachesSorter adapterCachesSorter, DistanceUpdater distanceUpdater,
            SqlCacheLoader sqlCacheLoader) {
        final ToleranceStrategy sqlCacheLoaderTolerance = new LocationTolerance(500,
                gpsDisabledLocation, 1000);
        final ToleranceStrategy adapterCachesSorterTolerance = new LocationTolerance(6,
                gpsDisabledLocation, 1000);
        final LocationTolerance distanceUpdaterLocationTolerance = new LocationTolerance(1,
                gpsDisabledLocation, 1000);
        final ToleranceStrategy distanceUpdaterTolerance = new LocationAndAzimuthTolerance(
                distanceUpdaterLocationTolerance, 720);

        final ActionAndTolerance[] actionAndTolerances = new ActionAndTolerance[] {
                new ActionAndTolerance(sqlCacheLoader, sqlCacheLoaderTolerance),
                new ActionAndTolerance(adapterCachesSorter, adapterCachesSorterTolerance),
                new ActionAndTolerance(distanceUpdater, distanceUpdaterTolerance)
        };

        return new ActionManager(actionAndTolerances);

    }

    @Provides
    ActionManagerFactory providesActionManagerFactory(GpsDisabledLocation gpsDisabledLocation,
            AdapterCachesSorter adapterCachesSorter, DistanceUpdater distanceUpdater) {
        final ToleranceStrategy adapterCachesSorterTolerance = new LocationTolerance(6,
                gpsDisabledLocation, 1000);
        final LocationTolerance distanceUpdaterLocationTolerance = new LocationTolerance(1,
                gpsDisabledLocation, 1000);
        final ToleranceStrategy distanceUpdaterTolerance = new LocationAndAzimuthTolerance(
                distanceUpdaterLocationTolerance, 720);

        final ActionAndTolerance[] actionAndTolerances = new ActionAndTolerance[] {
                null, new ActionAndTolerance(adapterCachesSorter, adapterCachesSorterTolerance),
                new ActionAndTolerance(distanceUpdater, distanceUpdaterTolerance)
        };
        return new ActionManagerFactory(actionAndTolerances, distanceUpdaterTolerance);

    }

}
