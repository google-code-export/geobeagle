package com.google.code.geobeagle;

public class Tags {
    /** This id must never occur in the database */
    public final static int NULL = 0;

    public final static int FOUND = 1;
    public final static int DNF = 2;
    public final static int FAVORITES = 3;
    
    //These attributes are actually not related to the specific user
    public final static int UNAVAILABLE = 4;
    public final static int ARCHIVED = 5;

    /** The cache is newly added */
    public final static int NEW = 6;
    /** The user placed this cache */
    public final static int MINE = 7;

    /** No logs for this cache */
    public final static int FOUND_BY_NOONE = 8;

}
