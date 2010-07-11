
package com.google.code.geobeagle.activity.main;

import com.google.code.geobeagle.ErrorDisplayer;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.main.intents.GeocacheToGoogleGeo;
import com.google.code.geobeagle.activity.main.intents.IntentStarter;
import com.google.code.geobeagle.activity.main.intents.IntentStarterGeo;
import com.google.code.geobeagle.activity.main.intents.IntentStarterViewUri;
import com.google.code.geobeagle.activity.main.menuactions.MenuActionNavigate;
import com.google.inject.Inject;
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

    @Inject
    public ChooseNavDialogProvider(ErrorDisplayer errorDisplayer, Activity geoBeagle,
            Provider<Resources> resourcesProvider, Provider<Context> contextProvider) {
        this.errorDisplayer = errorDisplayer;
        this.geoBeagle = (GeoBeagle)geoBeagle;
        this.resourcesProvider = resourcesProvider;
        this.contextProvider = contextProvider;
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
        final OnClickListener onClickListener = new MenuActionNavigate.OnClickListener(
                intentStarters);
        return new ChooseNavDialog(new AlertDialog.Builder(contextProvider.get()).setItems(
                R.array.select_nav_choices, onClickListener).setTitle(
                R.string.select_nav_choices_title).create());
    }
}
