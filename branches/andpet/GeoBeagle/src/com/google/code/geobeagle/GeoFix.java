package com.google.code.geobeagle;

import android.location.Location;

/** Wrapper for the class android.location.Location that can't be instantiated directly */
public class GeoFix {
    private final float mAccuracy;
    private final double mAltitude;
    private final double mLatitude;
    private final double mLongitude;
    private final long mTime;
    private final String mProvider;
    
    public GeoFix(Location location) {
        mAccuracy = location.getAccuracy();
        mAltitude = location.getAltitude();
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
        mTime = location.getTime();
        mProvider = location.getProvider();
    }
    
    public GeoFix(float accuracy, double altitude,
            double latitude, double longitude, long time, String provider) {
        mAccuracy = accuracy;
        mAltitude = altitude;
        mLatitude = latitude;
        mLongitude = longitude;
        mTime = time;
        mProvider = provider;
    }
    
    public float getAccuracy() {
        return mAccuracy;
    }
    
    public double getAltitude() {
        return mAltitude;
    }
    
    public double getLatitude() {
        return mLatitude;
    }
    
    public double getLongitude() {
        return mLongitude;
    }
    
    public long getTime() {
        return mTime;
    }
    
    public float distanceTo(GeoFix fix) {
        float results[] = { 0 };
        Location.distanceBetween(mLatitude, mLongitude, 
                fix.getLatitude(), fix.getLongitude(), results);
        return results[0];
    }

    public CharSequence getProvider() {
        return mProvider;
    }
}
