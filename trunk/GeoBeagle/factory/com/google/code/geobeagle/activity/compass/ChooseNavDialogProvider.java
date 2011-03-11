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

package com.google.code.geobeagle.activity.compass;

import com.google.code.geobeagle.ErrorDisplayer;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.compass.ChooseNavDialog;
import com.google.code.geobeagle.activity.compass.CompassActivity;
import com.google.code.geobeagle.activity.compass.intents.GeocacheToGoogleGeo;
import com.google.code.geobeagle.activity.compass.intents.IntentStarter;
import com.google.code.geobeagle.activity.compass.intents.IntentStarterGeo;
import com.google.code.geobeagle.activity.compass.intents.IntentStarterViewUri;
import com.google.code.geobeagle.activity.compass.menuactions.NavigateOnClickListener;
import com.google.code.geobeagle.activity.compass.view.install_radar.InstallRadarAppDialog;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;

public class ChooseNavDialogProvider implements Provider<ChooseNavDialog> {
    private final ErrorDisplayer errorDisplayer;
    private final CompassActivity compassActivity;
    private final Provider<Resources> resourcesProvider;
    private final Provider<Context> contextProvider;
    private final Provider<InstallRadarAppDialog> installRadarAppDialogProvider;

    @Inject
    public ChooseNavDialogProvider(Injector injector) {
        errorDisplayer = injector.getInstance(ErrorDisplayer.class);
        compassActivity = (CompassActivity)injector.getInstance(Activity.class);
        resourcesProvider = injector.getProvider(Resources.class);
        contextProvider = injector.getProvider(Context.class);
        installRadarAppDialogProvider = injector.getProvider(InstallRadarAppDialog.class);
    }

    @Override
    public ChooseNavDialog get() {
        GeocacheToGoogleGeo geocacheToGoogleMaps = new GeocacheToGoogleGeo(resourcesProvider,
                R.string.google_maps_intent);
        GeocacheToGoogleGeo geocacheToNavigate = new GeocacheToGoogleGeo(resourcesProvider,
                R.string.navigate_intent);

        IntentStarterGeo intentStarterRadar = new IntentStarterGeo(compassActivity, new Intent(
                "com.google.android.radar.SHOW_RADAR"));
        IntentStarterViewUri intentStarterGoogleMaps = new IntentStarterViewUri(compassActivity,
                geocacheToGoogleMaps, errorDisplayer);
        IntentStarterViewUri intentStarterNavigate = new IntentStarterViewUri(compassActivity,
                geocacheToNavigate, errorDisplayer);
        IntentStarter[] intentStarters = {
                intentStarterRadar, intentStarterGoogleMaps, intentStarterNavigate
        };
        OnClickListener onClickListener = new NavigateOnClickListener(intentStarters,
                installRadarAppDialogProvider.get());
        return new ChooseNavDialog(new AlertDialog.Builder(contextProvider.get())
                .setItems(R.array.select_nav_choices, onClickListener)
                .setTitle(R.string.select_nav_choices_title).create());
    }
}
