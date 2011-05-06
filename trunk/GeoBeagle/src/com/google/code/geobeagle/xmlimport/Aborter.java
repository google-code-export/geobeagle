package com.google.code.geobeagle.xmlimport;

import roboguice.inject.ContextScoped;

import android.util.Log;

@ContextScoped
public class Aborter {
    private static boolean mAborted = false;

    Aborter() {
        mAborted = false;
    }

    public void abort() {
        Log.d("GeoBeagle", this + ": aborting");
        mAborted = true;
    }

    public boolean isAborted() {
        return mAborted;
    }

    public void reset() {
        mAborted = false;
    }
}