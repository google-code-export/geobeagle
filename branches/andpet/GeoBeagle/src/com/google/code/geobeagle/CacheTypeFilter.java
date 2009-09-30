package com.google.code.geobeagle;

import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;

public class CacheTypeFilter {
    private static class FilterOption {
        public final String Label;
        public final String PrefsName;
        public final String SqlClause;
        public boolean Selected;
        public FilterOption(String label, String prefsName, String sqlClause) {
            Label = label;
            PrefsName = prefsName;
            SqlClause = sqlClause;
            Selected = true;
        }
    };
    private final FilterOption[] mOptions = { 
            new FilterOption("Traditional", "Traditional", "CacheType = 1"),
            new FilterOption("Multi", "Multi", "CacheType = 2"),
            new FilterOption("Mystery", "Unknown", "CacheType = 3"),
            new FilterOption("My location", "MyLocation", "CacheType = 4"),
            new FilterOption("Others", "Others", "CacheType = 0 OR (CacheType >= 5 AND CacheType <= 14)"),
            new FilterOption("Waypoints", "Waypoints", "(CacheType >= 20 AND CacheType <= 25)"),
            };

    public void loadFromPrefs(SharedPreferences prefs) {
        for (FilterOption option : mOptions) {
            option.Selected = prefs.getBoolean(option.PrefsName, true);
        }
    }

    public void saveToPrefs(SharedPreferences prefs) {
        SharedPreferences.Editor editor  = prefs.edit();
        for (FilterOption option : mOptions) {
            editor.putBoolean(option.PrefsName, option.Selected);
        }
        editor.commit();
    }
        
    public String getSqlWhereClause() {
        int count = 0;
        for (FilterOption option : mOptions) {
            if (option.Selected)
                count++;
        }

        if (count == mOptions.length || count == 0)
            //All or none are enabled
            return null;
        
        StringBuilder result = new StringBuilder();
        boolean isFirst = true;
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
        return result.toString();
    }

    public boolean[] getSelection() {
        boolean[] selected = new boolean[mOptions.length];
        for (int ix = 0; ix < mOptions.length; ix++) {
            selected[ix] = mOptions[ix].Selected;
        }
        return selected;
    }

    public void setEnabled(int ix, boolean enable) {
        if (ix < 0 || ix >= mOptions.length)
            Log.w("GeoBeagle", "CacheTypeFilter.setEnabled(): Ignoring call outside range");
        mOptions[ix].Selected = enable;
    }

    public CharSequence[] getOptionLabels() {
        CharSequence[] labels = new CharSequence[mOptions.length];
        for (int ix = 0; ix < mOptions.length; ix++) {
            labels[ix] = mOptions[ix].Label;
        }
        return labels;
    }
}
