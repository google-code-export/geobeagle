package com.google.code.geobeagle.actions;

import com.google.code.geobeagle.CacheFilter;
import com.google.code.geobeagle.activity.filterlist.FilterTypeCollection;
import com.google.code.geobeagle.database.CachesProviderDb;

import java.util.List;

//TODO: Rename class CacheFilterUpdater.. "reload cache list from cachefilter"
public class CacheFilterUpdater {
    private final FilterTypeCollection mFilterTypeCollection;
    private final List<CachesProviderDb> mCachesProviderDb;

    public CacheFilterUpdater(FilterTypeCollection filterTypeCollection,
            List<CachesProviderDb> cachesProviderDb) {
        mCachesProviderDb = cachesProviderDb;
        mFilterTypeCollection = filterTypeCollection;
    }
    
    public void loadActiveFilter() {
        CacheFilter filter = mFilterTypeCollection.getActiveFilter();
        for (CachesProviderDb provider : mCachesProviderDb) {
            provider.setFilter(filter);
        }
    }

}
