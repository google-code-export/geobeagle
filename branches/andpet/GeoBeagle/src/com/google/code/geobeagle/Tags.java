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
    
}
