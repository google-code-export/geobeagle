package com.google.code.geobeagle.activity.cachelist;

import com.google.inject.Inject;

import android.app.Activity;
import android.content.Intent;

class ImportIntentManager {
    static final String INTENT_EXTRA_IMPORT_TRIGGERED = "com.google.code.geabeagle.import_triggered";
    private final Activity mActivity;

    @Inject
    ImportIntentManager(Activity activity) {
        this.mActivity = activity;
    }

    boolean isImport() {
        Intent intent = mActivity.getIntent();
        if (intent == null)
            return false;

        String action = intent.getAction();
        if (action == null)
            return false;

        if (!action.equals("android.intent.action.VIEW"))
            return false;

        if (intent.getBooleanExtra(INTENT_EXTRA_IMPORT_TRIGGERED, false))
            return false;

        // Need to alter the intent so that the import isn't retriggered if
        // pause/resume is a result of the phone going to sleep and then
        // waking up again.
        intent.putExtra(INTENT_EXTRA_IMPORT_TRIGGERED, true);
        return true;
    }
}