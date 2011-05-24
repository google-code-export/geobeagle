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

import com.google.code.geobeagle.CacheListActivityStarter;
import com.google.code.geobeagle.CacheListActivityStarterPreHoneycomb;
import com.google.code.geobeagle.activity.cachelist.actions.menu.CompassFrameHider;
import com.google.code.geobeagle.activity.cachelist.actions.menu.HoneycombCompassFrameHider;
import com.google.code.geobeagle.activity.cachelist.actions.menu.NullCompassFrameHider;
import com.google.code.geobeagle.activity.cachelist.presenter.AbsoluteBearingFormatter;
import com.google.code.geobeagle.activity.cachelist.presenter.BearingFormatter;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh.ActionManager;
import com.google.code.geobeagle.activity.cachelist.presenter.ListActivityOnCreateHandler;
import com.google.code.geobeagle.activity.cachelist.presenter.ListFragmentOnCreateHandler;
import com.google.code.geobeagle.activity.cachelist.presenter.ListFragtivityOnCreateHandler;
import com.google.code.geobeagle.activity.cachelist.presenter.RelativeBearingFormatter;
import com.google.code.geobeagle.formatting.DistanceFormatter;
import com.google.code.geobeagle.formatting.DistanceFormatterImperial;
import com.google.code.geobeagle.formatting.DistanceFormatterMetric;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import roboguice.config.AbstractAndroidModule;

import android.content.SharedPreferences;
import android.os.Build;

public class CacheListModule extends AbstractAndroidModule {
    @Override
    protected void configure() {
        bind(ActionManager.class).toProvider(ActionManagerProvider.class).in(Singleton.class);
        bind(DistanceFormatter.class).toProvider(DistanceFormatterProvider.class);
        bind(BearingFormatter.class).toProvider(BearingFormatterProvider.class);

        int sdkVersion = Integer.parseInt(Build.VERSION.SDK);
        bind(CacheListActivityStarter.class).to(CacheListActivityStarterPreHoneycomb.class);
        if (sdkVersion >= Build.VERSION_CODES.HONEYCOMB) {
            bind(ListFragtivityOnCreateHandler.class).to(ListFragmentOnCreateHandler.class);
            bind(CompassFrameHider.class).to(HoneycombCompassFrameHider.class);
        } else {
            bind(ListFragtivityOnCreateHandler.class).to(ListActivityOnCreateHandler.class);
            bind(CompassFrameHider.class).to(NullCompassFrameHider.class);
        }
    }

    static class DistanceFormatterProvider implements Provider<DistanceFormatter> {
        private final SharedPreferences preferenceManager;
        private final DistanceFormatterMetric distanceFormatterMetric;
        private final DistanceFormatterImperial distanceFormatterImperial;

        @Inject
        DistanceFormatterProvider(SharedPreferences preferenceManager) {
            this.preferenceManager = preferenceManager;
            this.distanceFormatterMetric = new DistanceFormatterMetric();
            this.distanceFormatterImperial = new DistanceFormatterImperial();
        }

        @Override
        public DistanceFormatter get() {
            return preferenceManager.getBoolean("imperial", false) ? distanceFormatterImperial
                    : distanceFormatterMetric;
        }
    }

    static class BearingFormatterProvider implements Provider<BearingFormatter> {
        private final AbsoluteBearingFormatter absoluteBearingFormatter;
        private final RelativeBearingFormatter relativeBearingFormatter;
        private final SharedPreferences preferenceManager;

        @Inject
        BearingFormatterProvider(SharedPreferences preferenceManager) {
            this.preferenceManager = preferenceManager;
            this.absoluteBearingFormatter = new AbsoluteBearingFormatter();
            this.relativeBearingFormatter = new RelativeBearingFormatter();
        }

        @Override
        public BearingFormatter get() {
            return preferenceManager.getBoolean("absolute-bearing", false) ? absoluteBearingFormatter
                    : relativeBearingFormatter;
        }
    }

}
