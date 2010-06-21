package com.google.code.geobeagle.activity.filterlist;

import com.google.code.geobeagle.activity.filterlist.FilterListActivityDelegate;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

/**
 * A list of the pre-made filters which the user can choose between by tapping a list item
 *
 */
public class FilterListActivity extends ListActivity {
    private FilterListActivityDelegate mFiltersActivityDelegate;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.d("GeoBeagle", "CacheListActivity onCreate");
        mFiltersActivityDelegate = new FilterListActivityDelegate();
            //CacheListDelegateDI.create(this, getLayoutInflater());
        mFiltersActivityDelegate.onCreate(this);
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        mFiltersActivityDelegate.onListItemClick(l, v, position, id);
    }
}
