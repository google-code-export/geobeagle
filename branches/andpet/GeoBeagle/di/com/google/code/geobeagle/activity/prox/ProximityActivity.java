package com.google.code.geobeagle.activity.prox;

import com.google.code.geobeagle.CacheFilter;
import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.GeocacheFactory;
import com.google.code.geobeagle.LocationAndDirection;
import com.google.code.geobeagle.LocationControlDi;
import com.google.code.geobeagle.Refresher;
import com.google.code.geobeagle.database.CachesProviderArea;
import com.google.code.geobeagle.database.CachesProviderCount;
import com.google.code.geobeagle.database.DbFrontend;

import android.app.Activity;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.SurfaceHolder;

public class ProximityActivity extends Activity implements SurfaceHolder.Callback {

    class DataCollector implements Refresher {
        @Override
        public void forceRefresh() {
            refresh();
        }

        @Override
        public void refresh() {
            mProximityPainter.setUserDirection(mLocationAndDirection.getAzimuth());
            Location location = mLocationAndDirection.getLocation();
            mProximityPainter.setUserLocation(location.getLatitude(),
                    location.getLongitude(), location.getAccuracy());
        }
    }
    
    private ProximityView mProximityView;
    ProximityPainter mProximityPainter;
    DataCollector mDataCollector;
    private AnimatorThread mAnimatorThread;
    private boolean mStartWhenSurfaceCreated = false;  //TODO: Needed?
    private DbFrontend mDbFrontend;
    private LocationAndDirection mLocationAndDirection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GeocacheFactory geocacheFactory = new GeocacheFactory();
        mDbFrontend = new DbFrontend(this, geocacheFactory);
        CacheFilter cacheFilter = new CacheFilter(this);
        CachesProviderArea cachesProviderArea = new CachesProviderArea(mDbFrontend, cacheFilter);
        //CachesProviderRadius cachesProviderRadius = new CachesProviderRadius(cachesProviderArea);
        CachesProviderCount cachesProviderCount = new CachesProviderCount(cachesProviderArea, 5, 10);
        //cachesProviderRadius.setRadius(0.01);
        
        mProximityPainter = new ProximityPainter(cachesProviderCount);

        mProximityView = new ProximityView(this);
        setContentView(mProximityView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        SurfaceHolder holder = mProximityView.getHolder();
        holder.addCallback(this); 
        mDataCollector = new DataCollector();
        mLocationAndDirection = LocationControlDi.create(this);
        mLocationAndDirection.addObserver(mDataCollector);
        Location location = mLocationAndDirection.getLocation();
        mProximityPainter.setUserLocation(location.getLatitude(), location.getLongitude(), location.getAccuracy());
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        Geocache geocache = getIntent().<Geocache> getParcelableExtra("geocache");
        mProximityPainter.setSelectedGeocache(geocache);
                
        final SharedPreferences sharedPreferences = PreferenceManager
        .getDefaultSharedPreferences(this);
        mLocationAndDirection.onResume(sharedPreferences);
        if (mAnimatorThread == null)
            mStartWhenSurfaceCreated = true;
        else
            mAnimatorThread.start();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        mLocationAndDirection.onPause();
        if (mAnimatorThread != null) {
            AnimatorThread.IThreadStoppedListener listener = new
            AnimatorThread.IThreadStoppedListener() {
                @Override
                public void OnThreadStopped() {
                    mDbFrontend.closeDatabase();
                }
            };
            mAnimatorThread.stop(listener);
        }
    }
    
    
    // ***********************************
    // ** Implement SurfaceHolder.Callback **
    
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        Log.d("GeoBeagle", "surfaceChanged called ("+width+"x"+height+")");
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("GeoBeagle", "surfaceCreated called");
        mAnimatorThread = new AnimatorThread(holder, mProximityPainter);
        if (mStartWhenSurfaceCreated) {
            mStartWhenSurfaceCreated = false;
            mAnimatorThread.start();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d("GeoBeagle", "surfaceDestroyed called");
    }

}
