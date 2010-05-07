package com.google.code.geobeagle;

import android.app.Activity;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

//TODO: Allow filtering on Source
/**
 * A CacheFilter determines which of all geocaches that should be 
 * visible in the list and map views.
 * It can be translated into a SQL constraint for accessing the database.
 */
public class CacheFilter {
    private final Activity mActivity;
    
    /** The name of this filter as visible to the user */
    private String mName;

    /** The string used in Preferences to identify this filter */
    public final String mId;
    
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
        public final String PrefsName;
        public final String SqlClause;
        public boolean Selected;
        public int ViewResource;
        public BooleanOption(String prefsName, String sqlClause, 
                int viewResource) {
            PrefsName = prefsName;
            SqlClause = sqlClause;
            Selected = true;
            ViewResource = viewResource;
        }
    }
    private final BooleanOption[] mTypeOptions = { 
            new BooleanOption("Traditional", "CacheType = 1", R.id.ToggleButtonTrad),
            new BooleanOption("Multi", "CacheType = 2", R.id.ToggleButtonMulti),
            new BooleanOption("Unknown", "CacheType = 3", R.id.ToggleButtonMystery),
            new BooleanOption("MyLocation", "CacheType = 4", R.id.ToggleButtonMyLocation),
            new BooleanOption("Others", "CacheType = 0 OR (CacheType >= 5 AND CacheType <= 14)", R.id.ToggleButtonOthers),
            new BooleanOption("Waypoints", "(CacheType >= 20 AND CacheType <= 25)", R.id.ToggleButtonWaypoints),
            };
    
    //These SQL are to be applied when the option is deselected!
    private final BooleanOption[] mSizeOptions = { 
        new BooleanOption("Micro", "Container != 1", R.id.ToggleButtonMicro),
        new BooleanOption("Small", "Container != 2", R.id.ToggleButtonSmall),
        new BooleanOption("UnknownSize", "Container != 0", R.id.ToggleButtonUnknownSize),
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
        for (BooleanOption option : mTypeOptions) {
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
        for (BooleanOption option : mTypeOptions) {
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
        for (BooleanOption option : mTypeOptions) {
            if (option.Selected)
                count++;
        }

        StringBuilder result = new StringBuilder();
        boolean isFirst = true;
        
        if (count != mTypeOptions.length && count != 0) {
            for (BooleanOption option : mTypeOptions) {
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
        for (BooleanOption option : mTypeOptions) {
            option.Selected = provider.getBoolean(option.ViewResource);
        }
        for (BooleanOption option : mSizeOptions) {
            option.Selected = provider.getBoolean(option.ViewResource);
        }
        mFilterString = provider.getString(R.id.FilterString);

        mRequiredTags = new HashSet<Integer>();
        mForbiddenTags = new HashSet<Integer>();
        
        if (provider.getBoolean(R.id.CheckBoxRequireFavorites))
            mRequiredTags.add(Tags.FAVORITES);
        else if (provider.getBoolean(R.id.CheckBoxForbidFavorites))
            mForbiddenTags.add(Tags.FAVORITES);

        if (provider.getBoolean(R.id.CheckBoxRequireFound))
            mRequiredTags.add(Tags.FOUND);
        else if (provider.getBoolean(R.id.CheckBoxForbidFound))
            mForbiddenTags.add(Tags.FOUND);
        
        if (provider.getBoolean(R.id.CheckBoxRequireDNF))
            mRequiredTags.add(Tags.DNF);
        else if (provider.getBoolean(R.id.CheckBoxForbidDNF))
            mForbiddenTags.add(Tags.DNF);
       
    }

    /** Set up the view from the values in this CacheFilter. */
    public void pushToGui(FilterGui provider) {
        provider.setString(R.id.NameOfFilter, mName);
        for (BooleanOption option : mTypeOptions) {
            provider.setBoolean(option.ViewResource, option.Selected);
        }
        for (BooleanOption option : mSizeOptions) {
            provider.setBoolean(option.ViewResource, option.Selected);
        }
        String filter = mFilterString == null ? "" : mFilterString;
        provider.setString(R.id.FilterString, filter);
        provider.setBoolean(R.id.CheckBoxRequireFavorites, 
                mRequiredTags.contains(Tags.FAVORITES));
        provider.setBoolean(R.id.CheckBoxForbidFavorites, 
                mForbiddenTags.contains(Tags.FAVORITES));
        provider.setBoolean(R.id.CheckBoxRequireFound, 
                mRequiredTags.contains(Tags.FOUND));
        provider.setBoolean(R.id.CheckBoxForbidFound, 
                mForbiddenTags.contains(Tags.FOUND));
        provider.setBoolean(R.id.CheckBoxRequireDNF, 
                mRequiredTags.contains(Tags.DNF));
        provider.setBoolean(R.id.CheckBoxForbidDNF, 
                mForbiddenTags.contains(Tags.DNF));
    }

    public Set<Integer> getForbiddenTags() {
        return mForbiddenTags;
    }
}
