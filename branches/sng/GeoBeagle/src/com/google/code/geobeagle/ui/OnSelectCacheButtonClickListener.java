
package com.google.code.geobeagle.ui;

import com.google.code.geobeagle.intents.SelectCache;

import android.view.View;
import android.view.View.OnClickListener;

public class OnSelectCacheButtonClickListener implements OnClickListener {

    private final SelectCache mSelectCache;

    public OnSelectCacheButtonClickListener(SelectCache selectCache) {
        mSelectCache = selectCache;
    }

    public void onClick(View v) {
        mSelectCache.startIntent();
    }

}
