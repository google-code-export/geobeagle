
package com.google.code.geobeagle.ui;

import com.google.code.geobeagle.LifecycleManager;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.Spinner;

public class ContentSelector implements LifecycleManager {

    private static final String CONTENT_PROVIDER = "ContentProvider";
    private final SharedPreferences mPreferences;
    private final Spinner mSpinner;

    public ContentSelector(Spinner spinner, SharedPreferences sharedPreferences) {
        mSpinner = spinner;
        mPreferences = sharedPreferences;
    }

    public int getIndex() {
        return mSpinner.getSelectedItemPosition();
    }

    public void onPause(Editor editor) {
        editor.putInt(CONTENT_PROVIDER, mSpinner.getSelectedItemPosition());
    }

    public void onResume(SharedPreferences preferences, ErrorDisplayer errorDisplayer) {
        mSpinner.setSelection(mPreferences.getInt(CONTENT_PROVIDER, 0));
    }
}
