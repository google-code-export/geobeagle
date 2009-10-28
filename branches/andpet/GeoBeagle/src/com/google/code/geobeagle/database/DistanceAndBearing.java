package com.google.code.geobeagle.database;

import com.google.code.geobeagle.Geocache;

//TODO: Rename to GeocacheVector
public class DistanceAndBearing {

    public interface IDistanceAndBearingProvider {
        DistanceAndBearing getDistanceAndBearing(Geocache cache);      
    }

    private Geocache mGeocache;
    private float mDistance;
    private float mBearing;
    public DistanceAndBearing(Geocache geocache, float distance) {
        mGeocache = geocache;
        mDistance = distance;
    }
    /** Which unit? */
    public float getDistance() {
        return mDistance;
    }
    public float getBearing() {
        return mBearing;
    }
    public Geocache getGeocache() {
        return mGeocache;
    }
    
}
