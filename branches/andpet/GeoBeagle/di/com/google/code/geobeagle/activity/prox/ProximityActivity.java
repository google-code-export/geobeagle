package com.google.code.geobeagle.activity.prox;

import com.google.code.geobeagle.CacheFilter;
import com.google.code.geobeagle.GeoFix;
import com.google.code.geobeagle.GeoFixProvider;
import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.GeocacheFactory;
import com.google.code.geobeagle.LocationControlDi;
import com.google.code.geobeagle.Refresher;
import com.google.code.geobeagle.activity.filterlist.FilterTypeCollection;
import com.google.code.geobeagle.database.CachesProviderDb;
import com.google.code.geobeagle.database.CachesProviderCount;
import com.google.code.geobeagle.database.DbFrontend;

import android.app.Activity;
import android.os.Bundle;
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
            mProximityPainter.setUserDirection(mGeoFixProvider.getAzimuth());
            GeoFix location = mGeoFixProvider.getLocation();
            mProximityPainter.setUserLocation(location.getLatitude(),
                    location.getLongitude(), location.getAccuracy());
        }
    }
    
    private ProximityView mProximityView;
    ProximityPainter mProximityPainter;
    DataCollector mDataCollector;
    private AnimatorThread mAnimatorThread;
    private boolean mStartWhenSurfaceCreated = false;  //TODO: Is mStartWhenSurfaceCreated needed?
    private DbFrontend mDbFrontend;
    private GeoFixProvider mGeoFixProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GeocacheFactory geocacheFactory = new GeocacheFactory();
        mDbFrontend = new DbFrontend(this, geocacheFactory);
        final FilterTypeCollection filterTypeCollection = new FilterTypeCollection(this);
        final CacheFilter cacheFilter = filterTypeCollection.getActiveFilter();
        CachesProviderDb cachesProviderArea = new CachesProviderDb(mDbFrontend, cacheFilter);
        CachesProviderCount cachesProviderCount = new CachesProviderCount(cachesProviderArea, 5, 10);
        
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
        mGeoFixProvider = LocationControlDi.create(this);
        mGeoFixProvider.addObserver(mDataCollector);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        String id = getIntent().getStringExtra("geocacheId");
        Geocache geocache = mDbFrontend.loadCacheFromId(id);
        mProximityPainter.setSelectedGeocache(geocache);
                
        mGeoFixProvider.onResume();
        
        GeoFix location = mGeoFixProvider.getLocation();
        mProximityPainter.setUserLocation(location.getLatitude(), location.getLongitude(), location.getAccuracy());
        
        if (mAnimatorThread == null)
            mStartWhenSurfaceCreated = true;
        else
            mAnimatorThread.start();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        mGeoFixProvider.onPause();
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
