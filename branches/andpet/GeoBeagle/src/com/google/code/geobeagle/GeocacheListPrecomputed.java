package com.google.code.geobeagle;

import java.util.ArrayList;
import java.util.Iterator;

/** An ordered list of geocache objects, with support for
 *  comparing with another such list.
 * 
 * A future implementation can fetch 
 * the geocaches from the database when first needed */
public class GeocacheListPrecomputed extends GeocacheList {
    private final ArrayList<Geocache> mList;

    public static GeocacheListPrecomputed EMPTY =
        new GeocacheListPrecomputed();
    
    public GeocacheListPrecomputed() {
        mList = new ArrayList<Geocache>();
    }
    
    public GeocacheListPrecomputed(ArrayList<Geocache> list) {
        mList = list;
    }
    
    /** 'Special' constructor used by ProximityPainter */
    public GeocacheListPrecomputed(GeocacheList list, Geocache extra) {
        mList = new ArrayList<Geocache>(list);
        mList.add(extra);
    }

    //Actually the default AbstractList implementation might suffice
    public boolean equals(GeocacheListPrecomputed otherList) {
        if (otherList == this || otherList.mList == mList)
            return true;
        
        if (mList.size() != otherList.mList.size())
            return false;
        
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i) != otherList.mList.get(i))
                return false;
        }
        return true;
    }

    @Override
    public Iterator<Geocache> iterator() {
        return mList.iterator();
    }

    @Override
    public int size() {
        return mList.size();
    }
    
    public Geocache get(int position) {
        return mList.get(position);
    }
}
