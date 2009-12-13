package com.google.code.geobeagle.activity.filterlist;

import com.google.code.geobeagle.CacheFilter;
import com.google.code.geobeagle.Tags;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import java.util.ArrayList;

public class FilterTypeCollection {
    private static final String FILTER_PREFS = "Filter";
    private final Activity mActivity;
    private ArrayList<CacheFilter> mFilterTypes = new ArrayList<CacheFilter>();
    
    public FilterTypeCollection(Activity activity) {
        mActivity = activity;
        load();
    }

    /** Loads/reloads the list of filters from the Preferences */
    private void load() {
        mFilterTypes.clear();
        SharedPreferences prefs = mActivity.getSharedPreferences(FILTER_PREFS, 0);
        String ids = prefs.getString("FilterList", "");
        String[] idArray = ids.split(", ");
        if (idArray.length == 1 && idArray[0].equals("")) {
            //No filters were registered. Setup the default ones
            firstSetup();
        } else {
            for (String id : idArray) {
                mFilterTypes.add(new CacheFilter(id, mActivity));
            }
        }
    }

    private void firstSetup() {
        Log.d("GeoBeagle", "FilterTypeCollection first setup");
        {   FilterPreferences pref = new FilterPreferences("All caches");
            pref.setBoolean("Waypoints", false);
            add(new CacheFilter("All", mActivity, pref));
        }

        {   FilterPreferences favoritesPref = new FilterPreferences("Favorites");
            favoritesPref.setInteger("FilterTag", Tags.FAVORITES);
            add(new CacheFilter("Favorites", mActivity, favoritesPref));
        }

        {   FilterPreferences pref = new FilterPreferences("Custom 1");
            pref.setBoolean("Waypoints", false);
            add(new CacheFilter("Filter1", mActivity, pref));
        }
        
        {   FilterPreferences pref = new FilterPreferences("Custom 2");
            pref.setBoolean("Waypoints", false);
            add(new CacheFilter("Filter2", mActivity, pref));
        }

        {   FilterPreferences pref = new FilterPreferences("Custom 3");
            pref.setBoolean("Waypoints", false);
            add(new CacheFilter("Filter3", mActivity, pref));
        }

        /*
        {   FilterPreferences foundPref = new FilterPreferences("Found");
            foundPref.setInteger("FilterTag", Tags.FOUND);
            add(new CacheFilter("Found", mActivity, foundPref));
        }

        {   FilterPreferences dnfPref = new FilterPreferences("Did Not Find");
            dnfPref.setInteger("FilterTag", Tags.DNF);
            add(new CacheFilter("DNF", mActivity, dnfPref));
        }
        */
        
        String filterList = null;
        for (CacheFilter cacheFilter : mFilterTypes) {
            cacheFilter.saveToPreferences();
            if (filterList == null)
                filterList = cacheFilter.mId;
            else
                filterList = filterList + ", " + cacheFilter.mId;
        }
        SharedPreferences prefs = mActivity.getSharedPreferences(FILTER_PREFS, 0);
        Editor editor = prefs.edit();
        editor.putString("FilterList", filterList);
        editor.commit();
    }
    
    private void add(CacheFilter cacheFilter) {
        mFilterTypes.add(cacheFilter);
    }
    
    private CacheFilter getFromId(String id) {
        for (CacheFilter cacheFilter : mFilterTypes)
            if (cacheFilter.mId.equals(id))
                return cacheFilter;
        return null;
    }
    
    public CacheFilter getActiveFilter() {
        SharedPreferences prefs = mActivity.getSharedPreferences(FILTER_PREFS, 0);
        String id = prefs.getString("ActiveFilter", "All");
        return getFromId(id);
    }
    
    public void setActiveFilter(CacheFilter cacheFilter) {
        SharedPreferences prefs = mActivity.getSharedPreferences(FILTER_PREFS, 0);
        Editor editor = prefs.edit();
        editor.putString("ActiveFilter", cacheFilter.mId);
        editor.commit();
    }

    public int getCount() {
        return mFilterTypes.size();
    }
    
    public CacheFilter get(int position) {
        return mFilterTypes.get(position);
    }
    
    public int getIndexOf(CacheFilter cacheFilter) {
        return mFilterTypes.indexOf(cacheFilter);
    }
}
