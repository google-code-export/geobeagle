package com.google.code.geobeagle.database;

import com.google.code.geobeagle.Geocache;

import java.util.ArrayList;

//TODO: Rename to ICachesProvider
/** Interface to access a subset of the cache database. 
 * Used to form a Decorator pattern. */
public interface CachesProvider {

    /** Returns true if the result of getCaches() may have changed since the 
     * last call to resetChanged() */
    public boolean hasChanged();
    
    /** Reset the change flag (never done from within the class) */
    public void resetChanged();

    public int getCount();
    
    //Need to get size of returned list, so it can't be an Iterable
    /** The returned list is considered immutable */
    public ArrayList<Geocache> getCaches();
}
