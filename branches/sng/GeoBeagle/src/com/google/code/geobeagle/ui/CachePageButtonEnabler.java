
package com.google.code.geobeagle.ui;

import com.google.code.geobeagle.Destination;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.ResourceProvider;

import android.view.View;

public class CachePageButtonEnabler {
    private final View mCachePageButton;
    private final String[] mContentPrefixes;
    private final TooString mTooString;

    public CachePageButtonEnabler(TooString editText, View cachePageButton,
            ResourceProvider resourceProvider) {
        mTooString = editText;
        mCachePageButton = cachePageButton;
        mContentPrefixes = resourceProvider.getStringArray(R.array.content_prefixes);
    }

    public void check() {
        final String description = (String)Destination.extractDescription(mTooString.tooString());

        for (String contentPrefix : mContentPrefixes) {
            if (description.startsWith(contentPrefix)) {
                mCachePageButton.setEnabled(true);
                return;
            }
        }
        mCachePageButton.setEnabled(false);
    }
}
