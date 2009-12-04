package com.google.code.geobeagle.actions;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.Labels;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListAdapter;
import com.google.code.geobeagle.database.DbFrontend;

public class CacheActionToggleFavorite implements CacheAction {
    private final DbFrontend mDbFrontend;
    private final CacheListAdapter mCacheList;
    private final CacheFilterUpdater mCacheFilterUpdater;
    
    public CacheActionToggleFavorite(DbFrontend dbFrontend,
            CacheListAdapter cacheList, CacheFilterUpdater cacheFilterUpdater) {
        mDbFrontend = dbFrontend;
        mCacheList = cacheList;
        mCacheFilterUpdater = cacheFilterUpdater;
    }
    
    @Override
    public void act(Geocache geocache) {
        boolean isFavorite = mDbFrontend.geocacheHasLabel(geocache.getId(), 
                Labels.FAVORITES);
        if (isFavorite)
            mDbFrontend.unsetGeocacheLabel(geocache.getId(), Labels.FAVORITES);
        else
            mDbFrontend.setGeocacheLabel(geocache.getId(), Labels.FAVORITES);
        mCacheFilterUpdater.loadActiveFilter();
        mCacheList.forceRefresh();
    }

    @Override
    public String getLabel(Geocache geocache) {
        boolean isFavorite = mDbFrontend.geocacheHasLabel(geocache.getId(), 
                Labels.FAVORITES);
        return isFavorite ? "Remove from Favorites" : "Add to Favorites";
    }

}
