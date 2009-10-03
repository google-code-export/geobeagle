package com.google.code.geobeagle;

import android.app.Activity;
import android.content.SharedPreferences;

public class CacheFilter {
    
    public static interface SettingsProvider {
        public boolean getBoolean(int id);
        public String getString(int id);
        public void setBoolean(int id, boolean value);
        public void setString(int id, String value);
    }
    
    private static class FilterOption {
        public final String Label;
        public final String PrefsName;
        public final String SqlClause;
        public boolean Selected;
        public int ViewResource;
        public FilterOption(String label, String prefsName, String sqlClause, 
                int viewResource) {
            Label = label;
            PrefsName = prefsName;
            SqlClause = sqlClause;
            Selected = true;
            ViewResource = viewResource;
        }
    }
    private final FilterOption[] mOptions = { 
            new FilterOption("Traditional", "Traditional", "CacheType = 1", R.id.CheckBoxTrad),
            new FilterOption("Multi", "Multi", "CacheType = 2", R.id.CheckBoxMulti),
            new FilterOption("Mystery", "Unknown", "CacheType = 3", R.id.CheckBoxMystery),
            new FilterOption("My location", "MyLocation", "CacheType = 4", R.id.CheckBoxMyLocation),
            new FilterOption("Others", "Others", "CacheType = 0 OR (CacheType >= 5 AND CacheType <= 14)", R.id.CheckBoxOthers),
            new FilterOption("Waypoints", "Waypoints", "(CacheType >= 20 AND CacheType <= 25)", R.id.CheckBoxWaypoints),
            };
    private String mFilterString;

    public CacheFilter() {
    }
    
    public CacheFilter(Activity activity) {
        loadFromPrefs(activity);
    }
    
    public void loadFromPrefs(Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences("Filter", 0);
        for (FilterOption option : mOptions) {
            option.Selected = prefs.getBoolean(option.PrefsName, true);
        }
        mFilterString = prefs.getString("FilterString", null);
    }

    public void saveToPrefs(Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences("Filter", 0);
        SharedPreferences.Editor editor  = prefs.edit();
        for (FilterOption option : mOptions) {
            editor.putBoolean(option.PrefsName, option.Selected);
        }
        editor.putString("FilterString", mFilterString);
        editor.commit();
    }
        
    /** @return A number of conditions separated by AND */
    public String getSqlWhereClause() {
        int count = 0;
        for (FilterOption option : mOptions) {
            if (option.Selected)
                count++;
        }

        StringBuilder result = new StringBuilder();
        boolean isFirst = true;
        
        if (count != mOptions.length && count != 0) {
            for (FilterOption option : mOptions) {
                if (!option.Selected)
                    continue;
                if (isFirst) {
                    result.append("(");
                    isFirst = false;
                } else {
                    result.append(" OR ");
                }
                result.append(option.SqlClause);
            }
            result.append(")");
        }
        
        if (mFilterString != null && !mFilterString.equals("")) {
            if (isFirst) {
                isFirst = false;
            } else {
                result.append(" AND ");
            }
            
            if (containsUppercase(mFilterString)) {
                //Do case-sensitive query
                result.append("(Id LIKE '%" + mFilterString 
                        + "%' OR Description LIKE '%" + mFilterString + "%')");
            } else {
                //Do case-insensitive search
                result.append("(lower(Id) LIKE '%" + mFilterString
                        + "%' OR lower(Description) LIKE '%" + mFilterString + "%')");
            }
        }

        if (result.length() == 0)
            return null;
        return result.toString();
    }

    private boolean containsUppercase(String string) {
        return !string.equals(string.toLowerCase());
    }
    
    public void setFromProvider(SettingsProvider provider) {
        for (FilterOption option : mOptions) {
            option.Selected = provider.getBoolean(option.ViewResource);
        }
        mFilterString = provider.getString(R.id.FilterString);
    }

    /** Set up the view from the values in this CacheFilter. */
    public void pushToProvider(SettingsProvider provider) {
        for (FilterOption option : mOptions) {
            provider.setBoolean(option.ViewResource, option.Selected);
        }
        String filter = mFilterString == null ? "" : mFilterString;
        provider.setString(R.id.FilterString, filter);
    }
}
