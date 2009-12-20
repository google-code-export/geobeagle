package com.google.code.geobeagle;

import android.app.Activity;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

//TODO: Allow filtering on Source
public class CacheFilter {

    private final Activity mActivity;
    
    /** The name of this filter as visible to the user */
    private String mName;

    /** The string used in Preferences to identify this filter */
    public final String mId;
    
    /*
    public ICachesProviderArea getProvider(DbFrontend dbFrontend) {
        return new CachesProviderDb(dbFrontend, this);
    }
    */

    /** The name of this filter as visible to the user */
    public String getName() {
        return mName;
    }
    
    public static interface FilterGui {
        public boolean getBoolean(int id);
        public String getString(int id);
        public void setBoolean(int id, boolean value);
        public void setString(int id, String value);
    }
    
    private static class BooleanOption {
        public final String Label;
        public final String PrefsName;
        public final String SqlClause;
        public boolean Selected;
        public int ViewResource;
        public BooleanOption(String label, String prefsName, String sqlClause, 
                int viewResource) {
            Label = label;
            PrefsName = prefsName;
            SqlClause = sqlClause;
            Selected = true;
            ViewResource = viewResource;
        }
    }
    private final BooleanOption[] mOptions = { 
            new BooleanOption("Traditional", "Traditional", "CacheType = 1", R.id.CheckBoxTrad),
            new BooleanOption("Multi", "Multi", "CacheType = 2", R.id.CheckBoxMulti),
            new BooleanOption("Mystery", "Unknown", "CacheType = 3", R.id.CheckBoxMystery),
            new BooleanOption("My location", "MyLocation", "CacheType = 4", R.id.CheckBoxMyLocation),
            new BooleanOption("Others", "Others", "CacheType = 0 OR (CacheType >= 5 AND CacheType <= 14)", R.id.CheckBoxOthers),
            new BooleanOption("Waypoints", "Waypoints", "(CacheType >= 20 AND CacheType <= 25)", R.id.CheckBoxWaypoints),
            };
    
    //These SQL are to be applied when the option is deselected!
    private final BooleanOption[] mSizeOptions = { 
        new BooleanOption("Include micro's", "Micro", "Container != 1", R.id.CheckBoxMicro),
        new BooleanOption("Include small", "Small", "Container != 2", R.id.CheckBoxSmall),
        new BooleanOption("Include unknown sizes", "UnknownSize", "Container != 0", R.id.CheckBoxUnknownSize),
    };
       
    private String mFilterString;

    private static final Set<Integer> EMPTY_SET = new HashSet<Integer>();
    
    /** Limits the filter to only include geocaches with this tag. 
     * Zero means no limit. */
    private Set<Integer> mRequiredTags = EMPTY_SET;

    /** Caches with this tag are not included in the results no matter what. 
     * Zero means no restriction. */
    private Set<Integer> mForbiddenTags = EMPTY_SET;
    
    public CacheFilter(String id, Activity activity) {
        mId = id;
        mActivity = activity;
        SharedPreferences preferences = mActivity.getSharedPreferences(mId, 0);
        loadFromPreferences(preferences);
    }
    
    public CacheFilter(String id, Activity activity,
            SharedPreferences sharedPreferences) {
        mId = id;
        mActivity = activity;
        loadFromPreferences(sharedPreferences);
    }

    /** 
     * Load the values from SharedPreferences.
     * @return true if any value in the filter was changed
     */
    private void loadFromPreferences(SharedPreferences preferences) {
        for (BooleanOption option : mOptions) {
            option.Selected = preferences.getBoolean(option.PrefsName, true);
        }
        for (BooleanOption option : mSizeOptions) {
            option.Selected = preferences.getBoolean(option.PrefsName, true);
        }
        mFilterString = preferences.getString("FilterString", null);

        String required = preferences.getString("FilterTags", "");
        mRequiredTags = StringToIntegerSet(required);
        
        String forbidden = preferences.getString("FilterForbiddenTags", "");
        mForbiddenTags = StringToIntegerSet(forbidden);
        
        mName = preferences.getString("FilterName", "Unnamed");
    }

    public void saveToPreferences() {
        SharedPreferences preferences = mActivity.getSharedPreferences(mId, 0);
        SharedPreferences.Editor editor  = preferences.edit();
        for (BooleanOption option : mOptions) {
            editor.putBoolean(option.PrefsName, option.Selected);
        }
        for (BooleanOption option : mSizeOptions) {
            editor.putBoolean(option.PrefsName, option.Selected);
        }
        editor.putString("FilterString", mFilterString);
        editor.putString("FilterTags", SetToString(mRequiredTags));
        editor.putString("FilterForbiddenTags", SetToString(mForbiddenTags));
        editor.putString("FilterName", mName);
        editor.commit();
    }

    private static String SetToString(Set<Integer> set) {
        StringBuffer buffer = new StringBuffer();
        boolean first = true;
        for (int i : set) {
            if (first)
                first = false;
            else 
                buffer.append(' ');
            buffer.append(i);
        }
        return buffer.toString();
    }

    private static Set<Integer> StringToIntegerSet(String string) {
        if (string.equals(""))
            return EMPTY_SET;
        Set<Integer> set = new HashSet<Integer>();
        String[] parts = string.split(" ");
        for (String part : parts) {
            set.add(Integer.decode(part));
        }
        return set;
    }
    
    /** @return A number of conditions separated by AND, 
     *  or an empty string if there isn't any limit */
    public String getSqlWhereClause() {
        int count = 0;
        for (BooleanOption option : mOptions) {
            if (option.Selected)
                count++;
        }

        StringBuilder result = new StringBuilder();
        boolean isFirst = true;
        
        if (count != mOptions.length && count != 0) {
            for (BooleanOption option : mOptions) {
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

        for (BooleanOption option : mSizeOptions) {
            if (!option.Selected) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    result.append(" AND ");
                }
                result.append(option.SqlClause);
            }
        }
            
        return result.toString();
    }
    
    public Set<Integer> getRequiredTags() {
        return mRequiredTags;
    }

    private static boolean containsUppercase(String string) {
        return !string.equals(string.toLowerCase());
    }
    
    public void loadFromGui(FilterGui provider) {
        String newName = provider.getString(R.id.NameOfFilter);
        if (!newName.trim().equals("")) {
            mName = newName;
        }
        for (BooleanOption option : mOptions) {
            option.Selected = provider.getBoolean(option.ViewResource);
        }
        for (BooleanOption option : mSizeOptions) {
            option.Selected = provider.getBoolean(option.ViewResource);
        }
        mFilterString = provider.getString(R.id.FilterString);
        mRequiredTags = new HashSet<Integer>();
        if (provider.getBoolean(R.id.CheckBoxOnlyFavorites))
            mRequiredTags.add(Tags.FAVORITES);
        mForbiddenTags = new HashSet<Integer>();
        if (!provider.getBoolean(R.id.CheckBoxIncludeFinds))
            mForbiddenTags.add(Tags.FOUND);
    }

    /** Set up the view from the values in this CacheFilter. */
    public void pushToGui(FilterGui provider) {
        provider.setString(R.id.NameOfFilter, mName);
        for (BooleanOption option : mOptions) {
            provider.setBoolean(option.ViewResource, option.Selected);
        }
        for (BooleanOption option : mSizeOptions) {
            provider.setBoolean(option.ViewResource, option.Selected);
        }
        String filter = mFilterString == null ? "" : mFilterString;
        provider.setString(R.id.FilterString, filter);
        provider.setBoolean(R.id.CheckBoxIncludeFinds, !mForbiddenTags.contains(Tags.FOUND));
        provider.setBoolean(R.id.CheckBoxOnlyFavorites, mRequiredTags.contains(Tags.FAVORITES));
    }

    public Set<Integer> getForbiddenTags() {
        return mForbiddenTags;
    }
}
