package com.google.code.geobeagle;

import com.google.code.geobeagle.database.DbFrontend;

import java.util.ArrayList;
import java.util.Iterator;

/** Fetch the geocaches from the database (or previously loaded instance) 
  * when first needed */
public class GeocacheListLazy extends GeocacheList {
    /** Elements are either String (cacheId) or Geocache */
    private final ArrayList<Object> mList;
    
    private final DbFrontend mDbFrontend;

    public GeocacheListLazy() {
        mList = new ArrayList<Object>();
        mDbFrontend = null;
    }

    /** @param initialList The list of cacheIds (Strings) to load */
    public GeocacheListLazy(DbFrontend dbFrontend, ArrayList<Object> initialList) {
        mDbFrontend = dbFrontend;
        mList = initialList;
    }
    
    public boolean equals(GeocacheListLazy otherList) {
        if (otherList == this || otherList.mList == mList)
            return true;
        
        if (mList.size() != otherList.mList.size())
            return false;
        
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i) == otherList.mList.get(i))
                continue;
            //TODO! Incomplete comparison - implement equals() in Geocache class instead
            Object obj = mList.get(i);
            if (obj instanceof Geocache) {
                if (((Geocache)obj).getId().equals(otherList.mList.get(i)))
                    continue;
            }
            return false;
        }
        return true;
    }

    private class LazyIterator implements Iterator<Geocache> {
        private int nextIx = 0;
        @Override
        public boolean hasNext() {
            return mList.size() > nextIx;
        }
        @Override
        public Geocache next() {
            Geocache cache = get(nextIx);
            nextIx++;
            return cache;
        }
        @Override
        public void remove() {
            //GeocacheLists are immutable
            throw new UnsupportedOperationException();
        }
    }
    
    @Override
    public Iterator<Geocache> iterator() {
        return new LazyIterator();
    }

    @Override
    public int size() {
        return mList.size();
    }
    
    public Geocache get(int position) {
        Object nextObj = mList.get(position);
        if (nextObj instanceof String) {
            Geocache cache = mDbFrontend.loadCacheFromId((String)nextObj);
            mList.set(position, cache);
            return cache;
        }
        return (Geocache)nextObj;
    }
}
