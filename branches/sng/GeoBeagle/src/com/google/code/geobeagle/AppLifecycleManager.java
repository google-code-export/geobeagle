
package com.google.code.geobeagle;

import com.google.code.geobeagle.ui.ErrorDisplayer;

import android.content.SharedPreferences;

public class AppLifecycleManager {
    private final SharedPreferences mPreferences;
    private final LifecycleManager[] mLifecycleManagers;

    public AppLifecycleManager(SharedPreferences preferences, LifecycleManager[] lifecycleManagers) {
        mLifecycleManagers = lifecycleManagers;
        mPreferences = preferences;
    }

    public void onPause() {
        final SharedPreferences.Editor editor = mPreferences.edit();
        for (LifecycleManager lifecycleManager : mLifecycleManagers) {
            lifecycleManager.onPause(editor);
        }
        editor.commit();
    }

    public void onResume(ErrorDisplayer errorDisplayer) {
        for (LifecycleManager lifecycleManager : mLifecycleManagers) {
            lifecycleManager.onResume(mPreferences, errorDisplayer);
        }
    }
}
