package com.google.code.geobeagle.activity.filterlist;

import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

class FilterPreferences implements SharedPreferences {
    private HashMap<String, Object> mValues;
    
    public FilterPreferences(String filterName) {
        mValues = new HashMap<String, Object>();
        setString("FilterName", filterName);
        //Default values
        /*
        setBoolean("Traditional", true);            
        setBoolean("Multi", true);
        setBoolean("Unknown", true);
        setBoolean("MyLocation", true);
        setBoolean("Others", true);
        setBoolean("Waypoints", false);

        setBoolean("Micro", true);
        setBoolean("Small", true);
        setBoolean("UnknownSize", true);

        setString("FilterString", "");
        setInteger("FilterLabel", 0);
        */
    };

    public void setBoolean(String key, boolean value) {
        mValues.put(key, new Boolean(value));
    }
    public void setString(String key, String value) {
        mValues.put(key, value);
    }
    public void setInteger(String key, int value) {
        mValues.put(key, new Integer(value));
    }

    @Override
    public boolean contains(String key) {
        return mValues.containsKey(key);
    }

    @Override
    public Editor edit() {
        return null;
    }

    @Override
    public Map<String, ?> getAll() {
        return mValues;
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        Object value = mValues.get(key);
        if (value == null) return defValue;
        return (Boolean)value;
    }

    @Override
    public float getFloat(String key, float defValue) {
        Object value = mValues.get(key);
        if (value == null) return defValue;
        return (Float)value;
    }

    @Override
    public int getInt(String key, int defValue) {
        Object value = mValues.get(key);
        if (value == null) return defValue;
        return (Integer)value;
    }

    @Override
    public long getLong(String key, long defValue) {
        Object value = mValues.get(key);
        if (value == null) return defValue;
        return (Long)value;
    }

    @Override
    public String getString(String key, String defValue) {
        Object value = mValues.get(key);
        if (value == null) return defValue;
        return (String)value;
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(
            OnSharedPreferenceChangeListener listener) {
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(
            OnSharedPreferenceChangeListener listener) {
    }

}
