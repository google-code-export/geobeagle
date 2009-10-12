package com.google.code.geobeagle.database;

import com.google.code.geobeagle.Geocache;

import android.util.Log;

import java.util.ArrayList;

public class CachesProviderCount implements ICachesProviderCenter {
    private static final double MAX_RADIUS = 180;
    /** Maximum number of times a search is allowed to call the underlying 
     * CachesProvider before yielding a best-effort result */
    public static final int MAX_ITERATIONS = 7;
    private static final float DISTANCE_MULTIPLIER = 1.414f;
    
    private CachesProviderRadius mCachesProviderRadius;
    /** The least acceptable number of caches */
    private int mMinCount;
    /* The max acceptable number of caches */
    private int mMaxCount;
    private double mRadius;
    private ArrayList<Geocache> mCaches;
    
    /** Number of caches within mRadius */
    private int mCount;
    /** True if mCount has been calculated for the current values */
    private boolean mIsCountValid;
    /** Used for hasChanged() / setChanged() */
    private boolean mHasChanged = true;
    
    public CachesProviderCount(ICachesProviderArea area,
            int minCount, int maxCount) {
        mCachesProviderRadius = new CachesProviderRadius(area);
        mMinCount = minCount;
        mMaxCount = maxCount;
        mRadius = 0.04;  //TODO: default value; tweak
        mIsCountValid = false;
    }
    
    @Override
    public void setCenter(double latitude, double longitude) {
        mCachesProviderRadius.setCenter(latitude, longitude);
    }
    
    /** Returns the radius used to get the current set of caches*/
    public double getRadius() {
        if (!mIsCountValid || mCachesProviderRadius.hasChanged()) {
            CountAndRadius car = findRadius(mRadius);
            mCount = car.Count;
            mRadius = car.Radius;
            mIsCountValid = true;
            mCachesProviderRadius.setChanged(false);
        }
        return mRadius;
    }
    
    @Override
    public ArrayList<Geocache> getCaches() {
        if (mCachesProviderRadius.hasChanged()) {
            mCaches = null;
            mIsCountValid = false;
        }
            
        if (mCaches != null)
            return mCaches;

        if (!mIsCountValid) {
            CountAndRadius car = findRadius(mRadius);
            mCount = car.Count;
            mRadius = car.Radius;
            mIsCountValid = true;
        }
        
        mCachesProviderRadius.setRadius(mRadius);
        mCaches = mCachesProviderRadius.getCaches();
        mCachesProviderRadius.setChanged(false);
        return mCaches;
    }

    @Override
    public int getCount() {
        if (!mIsCountValid || mCachesProviderRadius.hasChanged()) {
            CountAndRadius car = findRadius(mRadius);
            mCount = car.Count;
            mRadius = car.Radius;
            mIsCountValid = true;
            mCachesProviderRadius.setChanged(false);
        }

        return mCount;
    }

    private int countHitsUsingRadius(double radius) {
        mCachesProviderRadius.setRadius(radius);
        return mCachesProviderRadius.getCount();
    }

    //TODO: Don't create CountAndRadius instances all the time
    private class CountAndRadius {
        public int Count;
        public double Radius;
        public CountAndRadius(int count, double radius) {
            Count = count;
            Radius = radius;
        }
    }

    /**
     * @param radiusToTry Starting radius (in degrees) in search
     * @return Radius setting that satisfy mMinCount <= count <= mMaxCount
     */
    private CountAndRadius findRadius(double radiusToTry) {
        int count = countHitsUsingRadius(radiusToTry);
        int iterationsLeft = MAX_ITERATIONS - 1;
        Log.d("GeoBeagle", "CachesProviderCount first count = " + count);
        if (count > mMaxCount) {
            while (count > mMaxCount && iterationsLeft > 1) {
                radiusToTry /= DISTANCE_MULTIPLIER;
                count = countHitsUsingRadius(radiusToTry);
                iterationsLeft -= 1;
                Log.d("GeoBeagle", "CachesProviderCount search inward count = " + count);
            }
        }
        else if (count < mMinCount) {
            while (count < mMinCount && radiusToTry < MAX_RADIUS / DISTANCE_MULTIPLIER
                    && iterationsLeft > 1) {
                radiusToTry *= DISTANCE_MULTIPLIER;
                count = countHitsUsingRadius(radiusToTry);
                iterationsLeft -= 1;
                Log.d("GeoBeagle", "CachesProviderCount search outward count = " + count);
            }
        }
        if (count < mMinCount && iterationsLeft > 0)
            return findWithinLimits(radiusToTry, radiusToTry * DISTANCE_MULTIPLIER, 
                    iterationsLeft);
        else if (count > mMaxCount && iterationsLeft > 0)
            return findWithinLimits(radiusToTry / DISTANCE_MULTIPLIER, radiusToTry, 
                    iterationsLeft);
        return new CountAndRadius(count, radiusToTry);  //Value is ok
    }

    private CountAndRadius findWithinLimits(double minRadius, double maxRadius, 
            int iterationsLeft) {
        double radiusToTry = (minRadius + maxRadius) / 2.0;
        int count = countHitsUsingRadius(radiusToTry);
        if (count <= mMaxCount && count >= mMinCount) {
            Log.d("GeoBeagle", "CachesProviderCount.findWithinLimits: Found count = " + count);            
            return new CountAndRadius(count, radiusToTry);
        }
        if (iterationsLeft <= 1) {
            Log.d("GeoBeagle", "CachesProviderCount.findWithinLimits: Giving up with count = " + count);
            return new CountAndRadius(count, radiusToTry);
        }
        if (count > mMaxCount) {
            return findWithinLimits(minRadius, radiusToTry, iterationsLeft - 1);
        }
        return findWithinLimits(radiusToTry, maxRadius, iterationsLeft - 1);
    }

    @Override
    public boolean hasChanged() {
        return mHasChanged || mCachesProviderRadius.hasChanged();
    }

    @Override
    public void setChanged(boolean changed) {
        mHasChanged = changed;
        if (!changed)
            mCachesProviderRadius.setChanged(false);
    }
}
