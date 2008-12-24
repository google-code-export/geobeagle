
package com.google.code.geobeagle.ui;

import com.google.code.geobeagle.Destination;
import com.google.code.geobeagle.TooString;

import android.view.View;

public class CachePageButtonEnabler {
    private final TooString mTooString;
    private final View mCachePageButton;

    public CachePageButtonEnabler(TooString editText, View cachePageButton) {
        mTooString = editText;
        mCachePageButton = cachePageButton;
    }

    public void check() {
        mCachePageButton.setEnabled(((String)Destination.extractDescription(mTooString
                .tooString())).startsWith("GC"));
    }
}
