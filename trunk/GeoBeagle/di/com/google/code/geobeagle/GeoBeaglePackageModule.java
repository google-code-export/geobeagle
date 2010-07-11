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

package com.google.code.geobeagle;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.google.code.geobeagle.CompassListener.Azimuth;
import com.google.code.geobeagle.LocationControlBuffered.GpsDisabledLocation;
import com.google.code.geobeagle.LocationControlBuffered.GpsEnabledLocation;
import com.google.code.geobeagle.LocationControlBuffered.IGpsLocation;
import com.google.code.geobeagle.activity.cachelist.presenter.DistanceSortStrategy;
import com.google.code.geobeagle.activity.cachelist.presenter.NullSortStrategy;
import com.google.code.geobeagle.activity.cachelist.presenter.GeoBeaglePackageAnnotations.GeoBeagle;
import com.google.code.geobeagle.activity.searchonline.NullRefresher;
import com.google.code.geobeagle.location.LocationControl;
import com.google.inject.BindingAnnotation;
import com.google.inject.Provides;

import roboguice.config.AbstractAndroidModule;
import roboguice.inject.ContextScoped;
import roboguice.inject.SystemServiceProvider;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.preference.PreferenceManager;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

// TODO rename to GeoBeagleModule
public class GeoBeaglePackageModule extends AbstractAndroidModule {

    @BindingAnnotation
    @Target( {
            FIELD, PARAMETER, METHOD
    })
    @Retention(RUNTIME)
    public static @interface DefaultSharedPreferences {
    }

    @Provides
    @DefaultSharedPreferences
    public SharedPreferences providesDefaultSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Provides
    AlertDialog.Builder providesAlertDialogBuilder(Activity activity) {
        return new AlertDialog.Builder(activity);
    }

    @Override
    protected void configure() {
        bind(Refresher.class).to(NullRefresher.class);
        bind(SensorManager.class).toProvider(
                new SystemServiceProvider<SensorManager>(Context.SENSOR_SERVICE));
        bindConstant().annotatedWith(Azimuth.class).to(-1440f);
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
    @GeoBeagle
    public Intent geoBeagleIntent(Context context) {
        return new Intent(context, com.google.code.geobeagle.activity.main.GeoBeagle.class);
    }
}
