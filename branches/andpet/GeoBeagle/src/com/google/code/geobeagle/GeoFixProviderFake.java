package com.google.code.geobeagle;

import android.os.Handler;

import java.util.ArrayList;

//Doesn't have support for faking azimuth at the moment
/** Change LocationControlDi.create() to return an instance of this class 
 * to use fake locations everywhere but in the Map view. */
public class GeoFixProviderFake implements GeoFixProvider {
    
    public static class FakeDataset {
        /** How often to report a new position */
        public final int mReportPeriodMillisec;

        /** The fake positions will be interpreted to be 
         * given relative to this location */
        private final double mLatStart;
        private final double mLonStart;

        /** The period of the fix list loop */
        private final long mLoopTime;
        
        /** A number of GeoFixes with values relative some starting point.
         * The first fix must have relative time 0 */
        private GeoFix[] mFixes;
        
        public FakeDataset(int reportPeriodMillisec,
                double latStart, double lonStart,
                long loopTime, GeoFix[] fixes) {
            mReportPeriodMillisec = reportPeriodMillisec;
            mLatStart = latStart;
            mLonStart = lonStart;
            mLoopTime = loopTime;
            mFixes = fixes;
        }
        
        public GeoFix getGeoFixAtTime(long time) {
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
    }

    //This particular location is in Yokohama south of Tokyo. 
    //Customize to your neighborhood...
    public static FakeDataset YOKOHAMA = new FakeDataset(2000, 
            35.4583162355423, 139.62289574623108, 10000,
            new GeoFix[] { new GeoFix(192, 40, 0, 0, 0, "gps"),
                           new GeoFix(128, 40, 0.001, -0.0001, 5000, "gps"),        
                           new GeoFix(8, 40, 0.002, -0.0001, 7000, "gps"),
    });
    //A single location
    public static FakeDataset TOKYO = new FakeDataset(10000, 
            35.690448, 139.756225, 10000,
            new GeoFix[] { new GeoFix(16, 10, 0, 0, 0, "gps"),
    });
    
    private final FakeDataset mFakeDataset;
    
    private GeoFix mCurrentGeoFix;

    private boolean mUpdatesEnabled = false;
    
    private final Handler mHandler;
    
    private ArrayList<Refresher> mObservers = new ArrayList<Refresher>();

    private class FixTimer extends Thread {
        @Override
        public void run() {
            while (mUpdatesEnabled) {
                try {
                    if (mUpdatesEnabled) {
                        mCurrentGeoFix = mFakeDataset.getGeoFixAtTime(System.currentTimeMillis());
                        mHandler.post(mObserverNotifier);
                    }
                    Thread.sleep(mFakeDataset.mReportPeriodMillisec);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }        
    }
    private FixTimer mFixTimer;

    public GeoFixProviderFake(FakeDataset fakeDataset) {
        //Create here to assure that main thread gets to execute notifications:
        mHandler = new Handler();
        mFakeDataset = fakeDataset;
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
    private final Runnable mObserverNotifier = new Runnable() {
        @Override
        public void run() {
            notifyObservers();
        }
    };

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
        mUpdatesEnabled = false;
    }

    @Override
    public void onResume() {
        mUpdatesEnabled = true;
        mFixTimer = new FixTimer();
        mFixTimer.start();
    }
    
}
