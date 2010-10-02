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

import com.google.inject.Provider;
import com.google.inject.matcher.Matchers;

import roboguice.config.RoboGuiceModule;
import roboguice.inject.ActivityProvider;
import roboguice.inject.ContentResolverProvider;
import roboguice.inject.ContextScope;
import roboguice.inject.ContextScoped;
import roboguice.inject.ExtrasListener;
import roboguice.inject.ResourceListener;
import roboguice.inject.ResourcesProvider;
import roboguice.inject.SharedPreferencesProvider;
import roboguice.inject.SystemServiceProvider;
import roboguice.inject.ViewListener;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.location.LocationManager;
import android.os.PowerManager;
import android.view.LayoutInflater;

public class FasterRoboGuiceModule extends RoboGuiceModule {

    public FasterRoboGuiceModule(ContextScope contextScope,
            Provider<Context> throwingContextProvider, Provider<Context> contextProvider,
            ResourceListener resourceListener, ViewListener viewListener,
            ExtrasListener extrasListener, Application application) {
        super(contextScope, throwingContextProvider, contextProvider, resourceListener,
                viewListener, extrasListener, application);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void configure() {
        // Sundry Android Classes
        bind(Resources.class).toProvider(ResourcesProvider.class);
        bind(ContentResolver.class).toProvider(ContentResolverProvider.class);

        for (Class<? extends Object> c = application.getClass(); c != null
                && Application.class.isAssignableFrom(c); c = c.getSuperclass()) {
            bind((Class<Object>)c).toInstance(application);
        }

        // System Services
        bind(LocationManager.class).toProvider(
                new SystemServiceProvider<LocationManager>(Context.LOCATION_SERVICE));
        bind(LayoutInflater.class).toProvider(
                new SystemServiceProvider<LayoutInflater>(Context.LAYOUT_INFLATER_SERVICE));
        bind(ActivityManager.class).toProvider(
                new SystemServiceProvider<ActivityManager>(Context.ACTIVITY_SERVICE));
        bind(PowerManager.class).toProvider(
                new SystemServiceProvider<PowerManager>(Context.POWER_SERVICE));


        // Context Scope bindings
        bindScope(ContextScoped.class, contextScope);
        bind(ContextScope.class).toInstance(contextScope);
        bind(Context.class).toProvider(throwingContextProvider).in(ContextScoped.class);
        bind(Activity.class).toProvider(ActivityProvider.class);
        

        // Android Resources, Views and extras require special handling
        bindListener(new ClassToTypeLiteralMatcherAdapter(Matchers.subclassesOf(Activity.class)),
                resourceListener);
        bindListener(new ClassToTypeLiteralMatcherAdapter(Matchers.subclassesOf(Activity.class)),
                viewListener);
        bindListener(new ClassToTypeLiteralMatcherAdapter(Matchers.subclassesOf(Activity.class)),
                extrasListener);
    }
}
