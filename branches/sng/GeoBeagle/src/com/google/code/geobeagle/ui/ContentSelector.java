package com.google.code.geobeagle.ui;

import android.widget.Spinner;

public class ContentSelector {

    private final Spinner mSpinner;

    public ContentSelector(Spinner spinner) {
        mSpinner = spinner;
    }

    public int getIndex() {
        return mSpinner.getSelectedItemPosition();
    }
}
