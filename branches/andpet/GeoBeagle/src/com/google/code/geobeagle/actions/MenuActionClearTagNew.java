package com.google.code.geobeagle.actions;

import com.google.code.geobeagle.GeocacheFactory;
import com.google.code.geobeagle.Tags;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListAdapter;
import com.google.code.geobeagle.database.DbFrontend;

public class MenuActionClearTagNew implements MenuAction {

    private final DbFrontend mDbFrontend;
    private final GeocacheFactory mGeocacheFactory;
    private final CacheListAdapter mCacheListAdapter;
    
    public MenuActionClearTagNew(DbFrontend dbFrontend, 
            GeocacheFactory geocacheFactory, CacheListAdapter cacheListAdapter) {
        mDbFrontend = dbFrontend;
        mGeocacheFactory = geocacheFactory;
        mCacheListAdapter = cacheListAdapter;
    }
    
    @Override
    public void act() {
        mDbFrontend.clearTagForAllCaches(Tags.NEW);
        mGeocacheFactory.flushCacheIcons();
        mCacheListAdapter.notifyDataSetChanged();
    }

    @Override
    public String getLabel() {
        return "Clear all 'new'";
    }

}
