
package com.google.code.geobeagle.activity.cachelist;

import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public interface IGeocacheListController {

    public abstract boolean onContextItemSelected(MenuItem menuItem);

    public abstract boolean onCreateOptionsMenu(Menu menu);

    public abstract void onListItemClick(ListView l, View v, int position, long id);

    public abstract boolean onMenuOpened(int featureId, Menu menu);

    public abstract boolean onOptionsItemSelected(MenuItem item);

    public abstract void onPause();

    public abstract void onResume(CacheListRefresh cacheListRefresh);

}
