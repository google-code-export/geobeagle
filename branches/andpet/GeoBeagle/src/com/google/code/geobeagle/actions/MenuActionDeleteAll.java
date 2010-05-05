package com.google.code.geobeagle.actions;

import com.google.code.geobeagle.activity.cachelist.presenter.CacheListAdapter;
import com.google.code.geobeagle.activity.cachelist.presenter.TitleUpdater;
import com.google.code.geobeagle.database.CachesProviderDb;
import com.google.code.geobeagle.database.DbFrontend;

import android.content.res.Resources;

/** Deletes all caches in the database */
public class MenuActionDeleteAll extends ActionStaticLabel implements MenuAction {
    private final DbFrontend mDbFrontend;
    private final CachesProviderDb mCachesToFlush;
    private final TitleUpdater mTitleUpdater;
    private final CacheListAdapter mCacheListAdapter;
    
    public MenuActionDeleteAll(DbFrontend dbFrontend, Resources resources, 
            CachesProviderDb cachesToFlush, CacheListAdapter cacheListAdapter,
            TitleUpdater titleUpdater, int labelId) {
        super(resources, labelId);
        mDbFrontend = dbFrontend;
        mCachesToFlush = cachesToFlush;
        mCacheListAdapter = cacheListAdapter;
        mTitleUpdater = titleUpdater;
    }

    @Override
    public void act() {
        mDbFrontend.deleteAll();
        mCachesToFlush.notifyOfDbChange();  //Reload the cache list from the database
        mTitleUpdater.refresh();
        mCacheListAdapter.forceRefresh();
    }

}
