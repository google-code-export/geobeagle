package com.google.code.geobeagle.activity.main.menuactions;

import com.google.code.geobeagle.activity.main.intents.IntentStarter;

import android.content.DialogInterface;

public class NavigateOnClickListener implements DialogInterface.OnClickListener {
    private final IntentStarter[] mIntentStarters;

    public NavigateOnClickListener(IntentStarter[] intentStarters) {
        mIntentStarters = intentStarters;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        mIntentStarters[which].startIntent();
    }
}