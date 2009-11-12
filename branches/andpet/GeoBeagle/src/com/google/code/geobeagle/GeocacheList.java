package com.google.code.geobeagle;

import java.util.ArrayList;
import java.util.AbstractCollection;
import java.util.Iterator;

/** An ordered list of geocache objects, with support for
 *  comparing with another such list.
 * 
 * A future implementation can fetch 
 * the geocaches from the database when first needed */
public class GeocacheList extends AbstractCollection<Geocache> {
    private final ArrayList<Geocache> mList;

    public GeocacheList() {
        mList = new ArrayList<Geocache>();
    }
    
    public GeocacheList(ArrayList<Geocache> list) {
        mList = list;
    }
    
    /** 'Special' constructor used by ProximityPainter */
    public GeocacheList(GeocacheList list, Geocache extra) {
        mList = new ArrayList<Geocache>(list);
        mList.add(extra);
    }

    //TODO: Is this method used or the generic Object.equals ?
    public boolean equals(GeocacheList otherList) {
        if (otherList == this || otherList.mList == mList)
            return true;
        
        if (mList.size() != otherList.mList.size())
            return false;
        
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i) == otherList.mList.get(i))
                continue;
            //TODO: This is a secondary test that shouldn't be necessary
            //(GeocacheFactory should produce the same object)
            if (mList.get(i).getId() == otherList.mList.get(i))
                continue;
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
