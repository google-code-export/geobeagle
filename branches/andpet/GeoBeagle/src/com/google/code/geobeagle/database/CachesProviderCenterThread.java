package com.google.code.geobeagle.database;

import com.google.code.geobeagle.GeocacheList;
import com.google.code.geobeagle.GeocacheListPrecomputed;
import com.google.code.geobeagle.IPausable;
import com.google.code.geobeagle.Refresher;

import android.os.Handler;
import android.util.Log;

/** Runs the decorated ICachesProviderCenter asynchronously. 
 * setCenter() therefore takes an extra parameter.
 * 
 * getCaches() and getCount() only returns pre-calculated data, 
 * ensuring quick execution. 
 * 
 * hasChanged() will only return true when the thread has completed calculating 
 * new (and differing) data.
 * 
 * If a new center is set before the geocache list for the previous one finished 
 * calculating, the calculation is finished and then the latest center is used 
 * for a new calculation.
 */
public class CachesProviderCenterThread implements ICachesProvider, IPausable {

    private final ICachesProviderCenter mProvider;

    /** Only replaced from the extra thread */
    private GeocacheList mGeocaches = GeocacheListPrecomputed.EMPTY;
    
    private boolean mHasChanged = true;

    /** The coordinates for which mGeocaches is currently valid */
    private double mCalculatedLatitude;
    private double mCalculatedLongitude;
    /** When current calculation is done, continue with these center coordinates */
    private double mNextLatitude;
    private double mNextLongitude;

    private boolean mIsCalculating = false;
    
    private Thread mThread;

    /** Used to execute listener notification on the main thread */
    private final Handler mHandler;
    
    private Refresher mObserver;

    private class CalculationThread extends Thread {
        private final double mLatitude;
        private final double mLongitude;
        public CalculationThread(double latitude, double longitude) {
            mLatitude = latitude;
            mLongitude = longitude;
        }
        @Override
        public void run() {
            //Log.d("GeoBeagle", "Thread starting to calculate");
            /*
            try {
                sleep(2000);
            } catch (InterruptedException e) {
            }
            Log.d("GeoBeagle", "Thread has slept for 2 sec");
            */
            mProvider.setCenter(mLatitude, mLongitude);
            GeocacheList result = mProvider.getCaches();
            if (result.equals(mGeocaches)) {
                Log.d("GeoBeagle", "Thread finished calculating: the list didn't change");
                registerResult(mLatitude, mLongitude, mGeocaches, false);
            } else {
                Log.d("GeoBeagle", "Thread finished calculating: the list did change");
                registerResult(mLatitude, mLongitude, result, true);
            }
            startCalculation();
        }
    }

    /** Called by the extra thread to atomically update the state */
    private synchronized void registerResult(double latitude, double longitude,
            GeocacheList geocacheList, boolean changed) {
        mCalculatedLatitude = latitude;
        mCalculatedLongitude = longitude;
        mHasChanged = true;
        mGeocaches = geocacheList;
        mIsCalculating = false;  //Thread will die soon
        mThread = null;
        if (changed)
            mHandler.post(mObserverNotifier);
    }
    
    public CachesProviderCenterThread(ICachesProviderCenter provider) {
        mProvider = provider;
        //Create here to assure that main thread gets to execute notifications:
        mHandler = new Handler();
    }

    /** Start an asynchronous calculation and notify a refresher when the 
     * list has changed (if it does change) */
    public synchronized void setCenter(double latitude, double longitude,
            Refresher notifyAfter) {
        mNextLatitude = latitude;
        mNextLongitude = longitude;
        mObserver = notifyAfter;
        startCalculation();
    }
    
    private synchronized void startCalculation() {
        if (mIsCalculating)
            return;  //Will be started when current calculation finishes
        if (mIsPaused)
            return;
        if (mCalculatedLatitude == mNextLatitude
                && mCalculatedLongitude == mNextLongitude)
            return;  //No need to calculate again
        Log.d("GeoBeagle", "startCalculation starts thread for coordinate diff latitude="+
                (mNextLatitude-mCalculatedLatitude) + " longitude=" + (mNextLongitude-mCalculatedLongitude));

        mIsCalculating = true;
        mThread = new CalculationThread(mNextLatitude, mNextLongitude);
        if (true) {
            //mThread.setPriority();
            mThread.start();
        } else {
            Log.d("GeoBeagle", "Running thread inline instead!");
            mThread.run();
        }
            
    }
    
    @Override
    public GeocacheList getCaches() {
        //Don't calculate anything here -- just present previous results
        return mGeocaches;
    }

    @Override
    public int getCount() {
        //Don't calculate anything here -- just present previous results
        return mGeocaches.size();
    }

    @Override
    public boolean hasChanged() {
        return mHasChanged;
    }

    @Override
    public synchronized void resetChanged() {
        mHasChanged = false;
    }

    //Run this on the main thread
    private final Runnable mObserverNotifier = new Runnable() {
        @Override
        public void run() {
            if (mObserver != null)
                mObserver.refresh();
        }
    };

    private boolean mIsPaused = false;
    
    public synchronized void onResume() {
        mIsPaused = false;
        startCalculation();  //There might be a pending update
    }
    
    public void onPause() {
        synchronized (this) {
            mIsPaused = true;
            mObserver = null;  //Don't report any pending result
        }
        try {
            if (mThread != null)
                mThread.join();
        } catch (InterruptedException e) {
        }
    }
}
