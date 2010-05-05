package com.google.code.geobeagle.actions;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.database.ICachesProvider;

import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class CacheContextMenu implements OnCreateContextMenuListener {
    private final ICachesProvider mCachesProvider;
    private final CacheAction mCacheActions[];
    /** The geocache for which the menu was launched */
    private Geocache mGeocache = null;

    public CacheContextMenu(ICachesProvider cachesProvider,
            CacheAction cacheActions[]) {
        mCachesProvider = cachesProvider;
        mCacheActions = cacheActions;
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        AdapterContextMenuInfo acmi = (AdapterContextMenuInfo)menuInfo;
        if (acmi.position > 0) {
            mGeocache = mCachesProvider.getCaches().get(acmi.position - 1);
            menu.setHeaderTitle(mGeocache.getId());
            for (int ix = 0; ix < mCacheActions.length; ix++) {
                menu.add(0, ix, ix, mCacheActions[ix].getLabel(mGeocache));
            }
        }
    }

    public boolean onContextItemSelected(MenuItem menuItem) {
        Log.d("GeoBeagle", "Context menu doing action " + menuItem.getItemId() + " = " +
                mCacheActions[menuItem.getItemId()].getClass().toString());
        mCacheActions[menuItem.getItemId()].act(mGeocache);
        return true;
    }
    
    public Geocache getGeocache() {
        return mGeocache;
    }
}
