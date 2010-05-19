package com.google.code.geobeagle;

import java.util.Hashtable;

public class CacheTypeFactory {
    private final Hashtable<Integer, CacheType> mCacheTypes = 
        new Hashtable<Integer, CacheType>(CacheType.values().length);

    public CacheTypeFactory() {
        for (CacheType cacheType : CacheType.values())
            mCacheTypes.put(cacheType.toInt(), cacheType);
    }

    public CacheType fromInt(int i) {
        return mCacheTypes.get(i);
    }

    public CacheType fromTag(String tag) {
        for (CacheType cacheType : mCacheTypes.values()) {
            if (tag.equals(cacheType.getTag()))
                return cacheType;
        }
        
        //Quick-n-dirty way of implementing additional names for certain cache types
        if (tag.equals("Traditional Cache"))
            return CacheType.TRADITIONAL;
        if (tag.equals("Multi-cache"))
            return CacheType.MULTI;
        if (tag.equals("Virtual"))
            return CacheType.VIRTUAL;
        if (tag.equals("Event"))
            return CacheType.EVENT;
        if (tag.equals("Webcam"))
            return CacheType.WEBCAM;
        if (tag.equals("Earth"))
            return CacheType.EARTHCACHE;

        return CacheType.NULL;
    }

    public int container(String container) {
        if (container.equals("Micro")) {
            return 1;
        } else if (container.equals("Small")) {
            return 2;
        } else if (container.equals("Regular")) {
            return 3;
        } else if (container.equals("Large")) {
            return 4;
        }
        return 0;
    }

    public int stars(String stars) {
        try {
            return Math.round(Float.parseFloat(stars) * 2);
        } catch (Exception ex) {
            return 0;
        }
    }
}