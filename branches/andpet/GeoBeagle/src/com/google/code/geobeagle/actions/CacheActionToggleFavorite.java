package com.google.code.geobeagle.actions;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.Tags;
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
        boolean isFavorite = mDbFrontend.geocacheHasTag(geocache.getId(), 
                Tags.FAVORITES);
        mDbFrontend.setGeocacheTag(geocache.getId(), Tags.FAVORITES, !isFavorite);
        mCacheFilterUpdater.loadActiveFilter();
        mCacheList.forceRefresh();
    }

    @Override
    public String getLabel(Geocache geocache) {
        boolean isFavorite = mDbFrontend.geocacheHasTag(geocache.getId(), 
                Tags.FAVORITES);
        return isFavorite ? "Remove from Favorites" : "Add to Favorites";
    }

}
