
package com.google.code.geobeagle;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;

public class LocationOnKeyListener implements OnKeyListener {
    private final CachePageButtonEnabler mCachePageButtonEnabler;
    
    public LocationOnKeyListener(CachePageButtonEnabler cachePageButtonEnabler) {
        this.mCachePageButtonEnabler = cachePageButtonEnabler;
    }

    public boolean onKey(View v, int keyCode, KeyEvent event) {
        this.mCachePageButtonEnabler.check();
        return false;
    }
}
