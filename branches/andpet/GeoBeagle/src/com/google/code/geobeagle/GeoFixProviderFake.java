package com.google.code.geobeagle;

import android.content.SharedPreferences;

import java.util.ArrayList;

public class GeoFixProviderFake implements GeoFixProvider {
    /** How often to report a new position */
    private final int mReportPeriodMillisec = 2000;
    private final double mLatStart = 35.4583162355423;
    private final double mLonStart = 139.62289574623108;
    /** The period of the fix list loop */
    private final long mLoopTime = 15000;

    private GeoFix mCurrentGeoFix;

    private boolean mUpdatesEnabled = false;
    
    private ArrayList<Refresher> mObservers = new ArrayList<Refresher>();
    /** A number of GeoFixes with values relative some starting point.
     * The first fix must have relative time 0 */
    private static GeoFix[] mFixes = {
        //float accuracy, double altitude,
        //double latitude, double longitude, long time, String provider
        
        new GeoFix(192, 40, 0, 0, 0, "gps"),
        new GeoFix(128, 40, 0.001, -0.0001, 5000, "gps"),        
    };

    private class FixTimer extends Thread {

        @Override
        public void run() {
            while (mUpdatesEnabled) {
                try {
                    Thread.sleep(mReportPeriodMillisec);
                    if (mUpdatesEnabled) {
                        mCurrentGeoFix = getGeoFixAtTime(System.currentTimeMillis());
                        notifyObservers();
                    }
                } catch (InterruptedException e) {
                    return;
                }
            }
        }        
    }
    private FixTimer mFixTimer;
    
    private GeoFix getGeoFixAtTime(long time) {
        long relTime = time % mLoopTime;
        //Initialize to avoid compiler warning
        GeoFix lastFix = mFixes[0];
        GeoFix nextFix = mFixes[0];
        float ratio = 0;
        
        if (relTime > mFixes[mFixes.length - 1].getTime()) {
            lastFix = mFixes[mFixes.length - 1];
            nextFix = mFixes[0];
            ratio = ((relTime-lastFix.getTime()) / 
                    (mLoopTime-lastFix.getTime()));
        } else {
            for (int i = 0; i < mFixes.length; i++) {
                if (mFixes[i].getTime() == relTime) {
                    lastFix = mFixes[i];
                    nextFix = mFixes[i];
                    ratio = 0;
                    break;
                } else if (mFixes[i].getTime() > relTime) {
                    lastFix = mFixes[i-1];
                    nextFix = mFixes[i];
                    ratio = ((relTime-lastFix.getTime()) / 
                             (nextFix.getTime()-lastFix.getTime()));
                    break;
                }
            }
        }

        //float accuracy, double altitude,
        //double latitude, double longitude, long time, String provider
        return new GeoFix(lastFix.getAccuracy(),
                lastFix.getAltitude() + (nextFix.getAltitude()-lastFix.getAltitude())*ratio,
                mLatStart + lastFix.getLatitude() + (nextFix.getLatitude()-lastFix.getLatitude())*ratio,
                mLonStart + lastFix.getLongitude() + (nextFix.getLongitude()-lastFix.getLongitude())*ratio,
                time, (String)lastFix.getProvider());
    }

    public void addObserver(Refresher refresher) {
        if (!mObservers.contains(refresher))
            mObservers.add(refresher);
    }

    private void notifyObservers() {
        for (Refresher refresher : mObservers) {
            refresher.refresh();
        }
    }

    @Override
    public float getAzimuth() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public GeoFix getLocation() {
        return mCurrentGeoFix;
    }

    @Override
    public boolean isProviderEnabled() {
        return true;
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        mUpdatesEnabled = false;
    }

    @Override
    public void onResume(SharedPreferences sharedPreferences) {
        mUpdatesEnabled = true;
        mFixTimer = new FixTimer();
        mFixTimer.start();
    }
    
}
