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

import com.google.code.geobeagle.actions.ContextActions;
import com.google.code.geobeagle.activity.cachelist.actions.context.ContextAction;
import com.google.code.geobeagle.activity.cachelist.actions.context.ContextActionDelete;
import com.google.code.geobeagle.activity.cachelist.actions.context.ContextActionEdit;
import com.google.code.geobeagle.activity.cachelist.actions.context.ContextActionView;
import com.google.code.geobeagle.activity.cachelist.presenter.AbsoluteBearingFormatter;
import com.google.code.geobeagle.activity.cachelist.presenter.BearingFormatter;
import com.google.code.geobeagle.activity.cachelist.presenter.RelativeBearingFormatter;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh.ActionManager;
import com.google.code.geobeagle.formatting.DistanceFormatter;
import com.google.code.geobeagle.formatting.DistanceFormatterImperial;
import com.google.code.geobeagle.formatting.DistanceFormatterMetric;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Provides;

import roboguice.config.AbstractAndroidModule;

import android.app.Activity;
import android.app.ListActivity;
import android.content.SharedPreferences;

public class CacheListModule extends AbstractAndroidModule {
    @Override
    protected void configure() {
        bind(ActionManager.class).toProvider(ActionManagerProvider.class);
        bind(DistanceFormatter.class).toProvider(DistanceFormatterProvider.class);
        bind(BearingFormatter.class).toProvider(BearingFormatterProvider.class);
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
        private AbsoluteBearingFormatter absoluteBearingFormatter;
        private RelativeBearingFormatter relativeBearingFormatter;
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

    @Provides
    ListActivity providesListActivity(Activity activity) {
        return (ListActivity)activity;
    }

    @Provides
    ContextActions provideContextActions(ContextActionDelete contextActionDelete,
            ContextActionEdit contextActionEdit, ContextActionView contextActionView) {
        return new ContextActions(new ContextAction[] {
                contextActionDelete, contextActionView, contextActionEdit
        });
    }


}
