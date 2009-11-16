package com.google.code.geobeagle.database;

import com.google.code.geobeagle.Clock;
import com.google.code.geobeagle.GeocacheList;

import android.util.Log;

public class CachesProviderCount implements ICachesProviderCenter {
    private static final double MAX_RADIUS = 1; //180;
    /** Maximum number of times a search is allowed to call the underlying 
     * CachesProvider before yielding a best-effort result */
    public static final int MAX_ITERATIONS = 10;
    private static final float DISTANCE_MULTIPLIER = 1.8f;  //1.414f;
    
    private CachesProviderRadius mCachesProviderRadius;
    /** The least acceptable number of caches */
    private int mMinCount;
    /* The max acceptable number of caches */
    private int mMaxCount;
    private double mRadius;
    private GeocacheList mCaches;
    
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
        mRadius = 0.04;
        mIsCountValid = false;
    }
    
    @Override
    public void setCenter(double latitude, double longitude) {
        mCachesProviderRadius.setCenter(latitude, longitude);
    }
    
    @Override
    public GeocacheList getCaches() {
        if (mCachesProviderRadius.hasChanged()) {
            mCaches = null;
            mIsCountValid = false;
        }
            
        if (mCaches != null)
            return mCaches;

        if (!mIsCountValid) {
            Clock clock = new Clock();
            long start = clock.getCurrentTime();
            findRadius(mRadius);
            Log.d("GeoBeagle", "CachesProviderCount calculated in " +
                    (clock.getCurrentTime()-start) + " ms");
            mIsCountValid = true;
        }
        
        mCachesProviderRadius.setRadius(mRadius);
        mCaches = mCachesProviderRadius.getCaches();
        mCachesProviderRadius.resetChanged();
        return mCaches;
    }

    @Override
    public int getCount() {
        if (!mIsCountValid || mCachesProviderRadius.hasChanged()) {
            findRadius(mRadius);
            mIsCountValid = true;
            mCachesProviderRadius.resetChanged();
        }

        return mCount;
    }

    private int countHitsUsingRadius(double radius) {
        mCachesProviderRadius.setRadius(radius);
        return mCachesProviderRadius.getCount();
    }

    /**
     * @param radiusToTry Starting radius (in degrees) in search
     * @return Radius setting that satisfy mMinCount <= count <= mMaxCount
     */
    private void findRadius(double radiusToTry) {
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
        if (count < mMinCount && iterationsLeft > 0) {
            findWithinLimits(radiusToTry, radiusToTry * DISTANCE_MULTIPLIER, 
                    iterationsLeft);
        } else if (count > mMaxCount && iterationsLeft > 0) {
            findWithinLimits(radiusToTry / DISTANCE_MULTIPLIER, radiusToTry, 
                    iterationsLeft);
        } else {
            mCount = count;
            mRadius = radiusToTry;
        }
    }

    private void findWithinLimits(double minRadius, double maxRadius, 
            int iterationsLeft) {
        double radiusToTry = (minRadius + maxRadius) / 2.0;
        int count = countHitsUsingRadius(radiusToTry);
        if (count <= mMaxCount && count >= mMinCount) {
            Log.d("GeoBeagle", "CachesProviderCount.findWithinLimits: Found count = " + count);            
            mCount = count;
            mRadius = radiusToTry;
            return;
        }
        if (iterationsLeft <= 1) {
            Log.d("GeoBeagle", "CachesProviderCount.findWithinLimits: Giving up with count = " + count);
            mCount = count;
            mRadius = radiusToTry;
            return;
        }
        if (count > mMaxCount) {
            findWithinLimits(minRadius, radiusToTry, iterationsLeft - 1);
            return;
        }

        findWithinLimits(radiusToTry, maxRadius, iterationsLeft - 1);
    }

    @Override
    public boolean hasChanged() {
        return mHasChanged || mCachesProviderRadius.hasChanged();
    }

    @Override
    public void resetChanged() {
        mHasChanged = false;
        mCachesProviderRadius.resetChanged();
    }
}
