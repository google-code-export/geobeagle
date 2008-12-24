
package com.google.code.geobeagle;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.Button;

public class LocationOnKeyListener implements OnKeyListener {
    private final Button mCachePage;
    private final TooString mTooString;

    public LocationOnKeyListener(Button cachePage, TooString editText) {
        this.mCachePage = cachePage;
        this.mTooString = editText;
    }

    public boolean onKey(View v, int keyCode, KeyEvent event) {
        mCachePage.setEnabled((new Destination(mTooString.tooString())).getDescription().startsWith("GC"));
        return false;
    }
}
