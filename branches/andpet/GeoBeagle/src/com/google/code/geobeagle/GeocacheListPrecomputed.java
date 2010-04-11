package com.google.code.geobeagle;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;

/** An ordered list of geocache objects, with support for
 *  comparing with another such list.
 * 
 * A future implementation can fetch 
 * the geocaches from the database when first needed */
public class GeocacheListPrecomputed extends AbstractList<Geocache> {
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
    public GeocacheListPrecomputed(AbstractList<Geocache> list, Geocache extra) {
        mList = new ArrayList<Geocache>(list);
        mList.add(extra);
    }

    @Override
    public Iterator<Geocache> iterator() {
        return mList.iterator();
    }

    @Override
    public int size() {
        return mList.size();
    }
    
    @Override
    public Geocache get(int position) {
        return mList.get(position);
    }
}
