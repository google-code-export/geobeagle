
package com.google.code.geobeagle;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.Button;

public class LocationOnKeyListener implements OnKeyListener {
    private final CachePageButtonEnabler mCachePageButtonEnabler;
    
    public LocationOnKeyListener(Button cachePage, TooString editText) {
        this.mCachePageButtonEnabler = new CachePageButtonEnabler(editText, cachePage);
    }

    public boolean onKey(View v, int keyCode, KeyEvent event) {
        this.mCachePageButtonEnabler.check();
        return false;
    }
}
