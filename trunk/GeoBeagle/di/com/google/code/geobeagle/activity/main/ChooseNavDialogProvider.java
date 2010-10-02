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

package com.google.code.geobeagle.activity.main;

import com.google.code.geobeagle.ErrorDisplayer;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.main.intents.GeocacheToGoogleGeo;
import com.google.code.geobeagle.activity.main.intents.IntentStarter;
import com.google.code.geobeagle.activity.main.intents.IntentStarterGeo;
import com.google.code.geobeagle.activity.main.intents.IntentStarterViewUri;
import com.google.code.geobeagle.activity.main.menuactions.NavigateOnClickListener;
import com.google.code.geobeagle.activity.main.view.install_radar.InstallRadarAppDialog;
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
    private final GeoBeagle geoBeagle;
    private final Provider<Resources> resourcesProvider;
    private final Provider<Context> contextProvider;
    private final Provider<InstallRadarAppDialog> installRadarAppDialogProvider;

    @Inject
    public ChooseNavDialogProvider(Injector injector) {
        errorDisplayer = injector.getInstance(ErrorDisplayer.class);
        geoBeagle = (GeoBeagle)injector.getInstance(Activity.class);
        resourcesProvider = injector.getProvider(Resources.class);
        contextProvider = injector.getProvider(Context.class);
        installRadarAppDialogProvider = injector.getProvider(InstallRadarAppDialog.class);
    }

    @Override
    public ChooseNavDialog get() {

        final GeocacheToGoogleGeo geocacheToGoogleMaps = new GeocacheToGoogleGeo(resourcesProvider,
                R.string.google_maps_intent);
        final GeocacheToGoogleGeo geocacheToNavigate = new GeocacheToGoogleGeo(resourcesProvider,
                R.string.navigate_intent);

        final IntentStarterGeo intentStarterRadar = new IntentStarterGeo(geoBeagle, new Intent(
                "com.google.android.radar.SHOW_RADAR"));
        final IntentStarterViewUri intentStarterGoogleMaps = new IntentStarterViewUri(geoBeagle,
                geocacheToGoogleMaps, errorDisplayer);
        final IntentStarterViewUri intentStarterNavigate = new IntentStarterViewUri(geoBeagle,
                geocacheToNavigate, errorDisplayer);
        final IntentStarter[] intentStarters = {
                intentStarterRadar, intentStarterGoogleMaps, intentStarterNavigate
        };
        final OnClickListener onClickListener = new NavigateOnClickListener(intentStarters,
                installRadarAppDialogProvider.get());
        return new ChooseNavDialog(new AlertDialog.Builder(contextProvider.get())
                .setItems(R.array.select_nav_choices, onClickListener)
                .setTitle(R.string.select_nav_choices_title).create());
    }
}
