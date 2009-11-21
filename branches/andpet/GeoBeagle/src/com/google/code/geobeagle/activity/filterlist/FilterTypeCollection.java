package com.google.code.geobeagle.activity.filterlist;

import com.google.code.geobeagle.CacheFilter;
import com.google.code.geobeagle.Labels;
import com.google.code.geobeagle.database.CachesProviderDb;
import com.google.code.geobeagle.database.DbFrontend;
import com.google.code.geobeagle.database.ICachesProviderArea;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FilterTypeCollection {

    public static class FilterType {
        /** The text visible to the user */
        private final String mLabel;
        /** The string used in Preferences to store a reference to this object */
        public final String mId;
        private final CacheFilter mCacheFilter;
        
        public FilterType(String label, String id, CacheFilter cacheFilter) {
            mLabel = label;
            mId = id;
            mCacheFilter = cacheFilter;
        }
        
        public ICachesProviderArea getProvider(DbFrontend dbFrontend) {
            return new CachesProviderDb(dbFrontend, mCacheFilter);
        }

        public Map<String, String> getMap() {
            HashMap<String, String> item1 = new HashMap<String, String>();
            item1.put("label", mLabel);
            return item1;
        }
    }
    
    
    ArrayList<FilterType> mFilterTypes = new ArrayList<FilterType>();
    
    public FilterTypeCollection() {
        CacheFilter allCaches = new CacheFilter(new FilterPreferences());
        add(new FilterType("All caches", "All", allCaches));

        {   FilterPreferences favoritesPref = new FilterPreferences();
            favoritesPref.setInteger("FilterLabel", Labels.FAVORITES);
            CacheFilter favoriteCaches = new CacheFilter(favoritesPref);
            add(new FilterType("Favorites", "Favorites", favoriteCaches));
        }

        {   FilterPreferences foundPref = new FilterPreferences();
            foundPref.setInteger("FilterLabel", Labels.FOUND);
            CacheFilter foundCaches = new CacheFilter(foundPref);
            add(new FilterType("Found", "Found", foundCaches));
        }

        {   FilterPreferences dnfPref = new FilterPreferences();
            dnfPref.setInteger("FilterLabel", Labels.DNF);
            CacheFilter dnfCaches = new CacheFilter(dnfPref);
            add(new FilterType("Did Not Find", "DNF", dnfCaches));
        }
    
    }

    private void add(FilterType filterType) {
        mFilterTypes.add(filterType);
    }
    
    private FilterType getFromId(String id) {
        for (FilterType filterType : mFilterTypes)
            if (filterType.mId.equals(id))
                return filterType;
        return null;
    }
    
    public FilterType getActiveFilter(Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences("Filter", 0);
        String id = prefs.getString("ActiveFilter", "All");
        return getFromId(id);
    }
    
    public void setActiveFilter(Activity activity, FilterType filterType) {
        SharedPreferences prefs = activity.getSharedPreferences("Filter", 0);
        Editor editor = prefs.edit();
        editor.putString("ActiveFilter", filterType.mId);
    }
    
    public ArrayList<Map<String, String>> getAdapterData() {
        ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
        for (FilterType filterType : mFilterTypes)
            data.add(filterType.getMap());
        return data;
    }

    public FilterType get(int position) {
        return mFilterTypes.get(position);
    }
    
    public int getIndexOf(FilterType filterType) {
        return mFilterTypes.indexOf(filterType);
    }
}
