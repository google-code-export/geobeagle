package com.google.code.geobeagle.database;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.activity.cachelist.model.GeocacheVector;
import com.google.code.geobeagle.activity.cachelist.model.GeocacheVector.LocationComparator;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;

//TODO: Use this class from prox.DataCollector to determine the real outer limit
//TODO: Use this class to sort the list view. Remove SortStrategy etc.
/** Wraps another CachesProvider to make it sorted. Geocaches closer to 
 * the provided center come first in the getCaches list. 
 * Until setCenter has been called, the list will not be sorted. */
public class CachesProviderSorted implements ICachesProviderCenter {

    private final CachesProvider mCachesProvider;
    private boolean mHasChanged = true;
    private double mLatitude;
    private double mLongitude;
    private ArrayList<Geocache> mSortedList = null;
    private LocationComparator mLocationComparator;
    private boolean isInitialized = false;

    public CachesProviderSorted(CachesProvider cachesProvider) {
        mCachesProvider = cachesProvider;
        mLocationComparator = new LocationComparator();
        isInitialized = false;
    }

    /** Updates mSortedList to a sorted version of the current underlying cache list*/
    private void sort() {
        final ArrayList<Geocache> unsortedList = mCachesProvider.getCaches();
        ArrayList<GeocacheVector> geocacheVectors = new ArrayList<GeocacheVector>(unsortedList.size());
        for (Geocache geocache : unsortedList) {
            GeocacheVector geocacheVector = new GeocacheVector(geocache, null);
            geocacheVector.setDistance(geocache.getDistanceTo(mLatitude, mLongitude));
            geocacheVectors.add(geocacheVector);
        }
        Collections.sort(geocacheVectors, mLocationComparator);
        
        mSortedList = new ArrayList<Geocache>(unsortedList.size());
        for (GeocacheVector geocacheVector : geocacheVectors) {
            mSortedList.add(geocacheVector.getGeocache());
        }
    }
    
    @Override
    public ArrayList<Geocache> getCaches() {
        if (!isInitialized) {
            return mCachesProvider.getCaches();
        }
        if (mSortedList == null || mCachesProvider.hasChanged()) {
            sort();
        }
        return mSortedList;
    }

    @Override
    public int getCount() {
        return mCachesProvider.getCount();
    }

    @Override
    public boolean hasChanged() {
        return mHasChanged || mCachesProvider.hasChanged();
    }

    @Override
    public void setChanged(boolean changed) {
        mHasChanged = changed;
        if (!changed)
            mCachesProvider.setChanged(changed);
    }

    @Override
    public void setCenter(double latitude, double longitude) {
        //TODO: Not good enough to compare doubles with '=='?
        if (isInitialized && latitude == mLatitude && longitude == mLongitude)
            return;
        mLatitude = latitude;
        mLongitude = longitude;
        mHasChanged = true;
        isInitialized = true;
    }

    public double getFurthestCacheDistance() {
        if (!isInitialized) {
            Log.e("GeoBeagle", "getFurthestCacheDistance called before setCenter");
            return 0;
        }
        if (mSortedList == null || mCachesProvider.hasChanged()) {
            sort();
        }
        if (mSortedList.size() == 0)
            return 0;
        return mSortedList.get(mSortedList.size()-1).getDistanceTo(mLatitude, mLongitude);
    }
}
