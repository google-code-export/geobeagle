package com.google.code.geobeagle.database;

import com.google.code.geobeagle.Geocache;

import java.util.ArrayList;

/** Interface to access a subset of the cache database. */
public interface CachesProvider {

    /** Returns true if the result of getCaches() may have changed since the 
     * last call to setChanged(false) */
    public boolean hasChanged();
    
    /** Reset the change flag (not done from within the class)
     * or indicate the possibility of updated values */
    public void setChanged(boolean changed);

    public int getCount();
    
    //Need to get size of returned list, so it can't be an Iterable
    public ArrayList<Geocache> getCaches();
}
