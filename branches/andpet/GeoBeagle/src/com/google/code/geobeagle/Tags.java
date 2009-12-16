package com.google.code.geobeagle;

import java.util.HashMap;
import java.util.Map;

public class Tags {
    /** This id must never occur in the database */
    public final static int NULL = 0;

    public final static int FOUND = 1;
    public final static int DNF = 2;
    public final static int FAVORITES = 3;  //TODO: Rename to FAVORITE
    
    //These attributes are actually not related to the specific user
    public final static int UNAVAILABLE = 4;
    public final static int ARCHIVED = 5;

    /** The cache is newly added */
    public final static int NEW = 6;
    /** The user placed this cache */
    public final static int MINE = 7;

    /** No logs for this cache */
    public final static int FOUND_BY_NOONE = 8;

    /** Indicates that the user has edited the cache. 
     * Don't overwrite when importing a newer GPX. */
    public final static int LOCKED_FROM_OVERWRITING = 9;

    //This method will be refactored into a more elegant solution
    public static Map<Integer, String> GetAllTags() {
        HashMap<Integer, String> map = new HashMap<Integer, String>();
        map.put(FOUND, "Found");
        map.put(DNF, "Did Not Find");
        map.put(FAVORITES, "Favorite");
        map.put(NEW, "New");
        map.put(MINE, "Mine");
        return map;
    }
}
