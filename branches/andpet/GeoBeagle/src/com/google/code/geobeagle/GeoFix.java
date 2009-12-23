package com.google.code.geobeagle;

import android.location.Location;

/** Wrapper for the class android.location.Location that can't be instantiated directly */
public class GeoFix {
    public static final GeoFix NO_FIX = new GeoFix(0f, 0f, 0d, 0d, 0L, "");
    
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

    /** Returns a String with the lag given that systemTime is the current time. */
    public String getLagString(long systemTime) {
        if (this == NO_FIX)
            return "";
        
        float lagSec = (systemTime - mTime) / 1000f;
        if (lagSec > 3600 * 10) {  // > 10 hour
            int lagHours = (int)(lagSec/3600);
            return lagHours + "h";
        } else if (lagSec > 3600) {  // > 1 hour
            int lagHours = (int)(lagSec/3600);
            int lagMin = (int)(lagSec/60 - lagHours*60);
            if (lagMin > 0)
                return lagHours + "h " + lagMin + "m";
            else
                return lagHours + "h";
        } else if (lagSec > 60) {
            int lagMin = (int)(lagSec/60);
            lagSec = lagSec - lagMin*60;
            //lagSec = ((int)(lagSec*10)) / 10f;  //round
            if ((int)lagSec > 0)
                return lagMin + "m " + ((int)lagSec) + "s";
            else
                return lagMin + "m";
        } else if (lagSec >= 2) {
            return ((int)lagSec) + "s";
        } else {
            //Not enough lag to be worth showing
            return "";
        }
    }
    
    /** @result Approximate distance in meters */
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
