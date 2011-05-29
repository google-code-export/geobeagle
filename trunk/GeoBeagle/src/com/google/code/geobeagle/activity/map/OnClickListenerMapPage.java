package com.google.code.geobeagle.activity.map;

import com.google.code.geobeagle.ErrorDisplayer;
import com.google.code.geobeagle.activity.compass.fieldnotes.HasGeocache;
import com.google.code.geobeagle.activity.compass.intents.IntentStarterGeo;
import com.google.code.geobeagle.activity.compass.view.OnClickListenerIntentStarter;
import com.google.inject.Inject;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

public class OnClickListenerMapPage implements OnClickListener {

    private final OnClickListenerIntentStarter onClickListenerIntentStarter;

    @Inject
    public OnClickListenerMapPage(Activity activity,
            HasGeocache hasGeocache,
            ErrorDisplayer errorDisplayer) {
        Intent geoMapActivityIntent = new Intent(activity, GeoMapActivity.class);
        IntentStarterGeo intentStarterGeo = new IntentStarterGeo(activity,
                geoMapActivityIntent, hasGeocache);
        onClickListenerIntentStarter = new OnClickListenerIntentStarter(intentStarterGeo,
                errorDisplayer);
    }

    @Override
    public void onClick(View v) {
        onClickListenerIntentStarter.onClick(v);
    }
}
