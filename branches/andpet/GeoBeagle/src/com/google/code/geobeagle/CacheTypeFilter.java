package com.google.code.geobeagle;

import java.util.ArrayList;

public class CacheTypeFilter {
    private ArrayList<CacheType> mEnabledTypes = new ArrayList<CacheType>();
    
    public boolean isEnabled(CacheType cacheType) {
        return mEnabledTypes.contains(cacheType);
    }
    
    public void setEnabled(CacheType cacheType, boolean enable) {
        if (enable) {
            if (isEnabled(cacheType))
                return;
            mEnabledTypes.add(cacheType);
        } else {
            if (!isEnabled(cacheType))
                return;
            mEnabledTypes.remove(cacheType);
        }
    }
    
    public CharSequence getSqlWhereClause() {
        StringBuilder result = new StringBuilder();
        boolean isFirst = true;
        for (CacheType cacheType : mEnabledTypes) {
            if (isFirst) {
                result.append(" CacheType == ");
                isFirst = false;
            } else {
                result.append(" AND CacheType == ");
            }
            result.append(cacheType.toInt());
        }
        return result;
    }
}
