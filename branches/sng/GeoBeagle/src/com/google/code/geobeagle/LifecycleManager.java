
package com.google.code.geobeagle;

import com.google.code.geobeagle.ui.ErrorDisplayer;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public interface LifecycleManager {

    public abstract void onPause(Editor editor);

    public abstract void onResume(SharedPreferences preferences, ErrorDisplayer errorDisplayer);
}
