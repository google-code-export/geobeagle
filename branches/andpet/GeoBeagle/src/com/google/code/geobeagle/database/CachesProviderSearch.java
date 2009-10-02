package com.google.code.geobeagle.database;

import com.google.code.geobeagle.Geocache;

import java.util.ArrayList;

/** SKETCH
 * Does asynchronous database calls for free-text search.
 */
public class CachesProviderSearch implements CachesProvider {
    private CachesProvider mProvider;
    private String mSubstring;
    private boolean mIsBusy;
    private boolean mApplyingSubstring;
    private boolean mHasChanged;
    
    public CachesProviderSearch(CachesProvider provider) {
        mProvider = provider;
    }
    
    @Override
    public void setExtraCondition(String condition) {
        mProvider.setExtraCondition(condition);
    }

    @Override
    public ArrayList<Geocache> getCaches() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    private void doSearch() {
        if (mIsBusy)
            return;
        mIsBusy = true;
        //TODO: Launch thread to process the search query
        
        //After finishing:
        mHasChanged = true;
        //if (!mSubstring.equals())
            //Launch again
    }
    
    public void setFilterText(String substring) {
        mSubstring = substring;
        doSearch();
    }

    @Override
    public boolean hasChanged() {
        return mHasChanged || mProvider.hasChanged();
    }

    @Override
    public void setChanged(boolean changed) {
        mHasChanged = changed;
        if (!changed)
            mProvider.setChanged(false);
    }

}