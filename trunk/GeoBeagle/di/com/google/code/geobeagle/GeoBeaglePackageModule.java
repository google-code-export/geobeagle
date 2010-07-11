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

import com.google.code.geobeagle.activity.searchonline.NullRefresher;
import com.google.inject.BindingAnnotation;
import com.google.inject.Provides;

import roboguice.config.AbstractAndroidModule;
import roboguice.inject.ContextScoped;
import roboguice.inject.SystemServiceProvider;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
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
        bind(LocationControlBuffered.class).toProvider(LocationControlBufferedProvider.class).in(
                ContextScoped.class);
        bind(SensorManager.class).toProvider(
                new SystemServiceProvider<SensorManager>(Context.SENSOR_SERVICE));
    }
}
