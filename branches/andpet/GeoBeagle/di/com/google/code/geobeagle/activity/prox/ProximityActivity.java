package com.google.code.geobeagle.activity.prox;

import com.google.code.geobeagle.CacheFilter;
import com.google.code.geobeagle.GeocacheFactory;
import com.google.code.geobeagle.LocationControlBuffered;
import com.google.code.geobeagle.LocationControlDi;
import com.google.code.geobeagle.activity.main.GeoUtils;
import com.google.code.geobeagle.database.CachesProviderArea;
import com.google.code.geobeagle.database.CachesProviderCount;
import com.google.code.geobeagle.database.CachesProviderRadius;
import com.google.code.geobeagle.database.DbFrontend;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.FrameLayout;

public class ProximityActivity extends Activity {

    private ProximityView mProximityView;
    private ProximityPainter mProximityPainter;

    class ProximityDrawer {
        SurfaceHolder mSurfaceHolder;
        public ProximityDrawer(SurfaceHolder holder) {
            mSurfaceHolder = holder;
        }
        public void doDraw() {
            Canvas c = null;
            try {
                c = mSurfaceHolder.lockCanvas(null);
                if (c == null) {
                    Log.w(this.getClass().getName(), 
                          "run(): lockCanvas returned null");
                    return;
                }
                mProximityPainter.draw(c);
            } finally {
                // do this in a finally so that if an exception is thrown
                // during the above, we don't leave the Surface in an
                // inconsistent state
                if (c != null) {
                    mSurfaceHolder.unlockCanvasAndPost(c);
                }
            }
        }
    }

    //Why doesn't compass orientation work with the newer SensorEventListener ??
    @SuppressWarnings("deprecation")
    class SurfaceHolderCallback implements SurfaceHolder.Callback, SensorListener,
    LocationListener {
        
        private ProximityDrawer mProximityDrawer;
        private final SensorManager mSensorManager;
        //private final Sensor mCompassSensor;
        
        public SurfaceHolderCallback() {
            mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
            //mCompassSensor = mSensorManager.getDefaultSensor(SensorManager.SENSOR_ORIENTATION);
            mSensorManager.registerListener(this,   
                    SensorManager.SENSOR_ORIENTATION,  
                    SensorManager.SENSOR_DELAY_UI);
                    //mSensorManager.registerListener(this, mCompassSensor, SensorManager.SENSOR_DELAY_UI);
            mProximityDrawer = null;  //Not initialized yet

            final LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, this);
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
            mProximityDrawer = new ProximityDrawer(holder);
            mProximityDrawer.doDraw();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.d("GeoBeagle", "surfaceDestroyed called");
            mSensorManager.unregisterListener(this);
        }

        // ***********************************
        // ** Implement SensorListener **
        
        @Override
        public void onAccuracyChanged(int sensor, int accuracy) {
        }

        //public void onSensorChanged(SensorEvent event)  //SensorEventListener
        @Override
        public void onSensorChanged(int sensor, float[] values) {
            if (mProximityDrawer == null)
                return;
            if (sensor == SensorManager.SENSOR_ORIENTATION)
                mProximityPainter.setUserDirection(values[0]);
            mProximityDrawer.doDraw();
        }

        // ***********************************
        // ** Implement LocationListener **

        private static final long RETAIN_GPS_MILLIS = 10000L;
        private long mLastGpsFixTime;
        private boolean mHaveLocation;
        private Location mNetworkLocation;
        private boolean mGpsAvailable;
        private boolean mNetworkAvailable;
        
        @Override
        public void onLocationChanged(Location location) {
            // Log.d("GeoBeagle", "radarview::onLocationChanged");
            mHaveLocation = true;

            final long now = SystemClock.uptimeMillis();
            boolean useLocation = false;
            final String provider = location.getProvider();
            if (LocationManager.GPS_PROVIDER.equals(provider)) {
                // Use GPS if available
                mLastGpsFixTime = SystemClock.uptimeMillis();
                useLocation = true;
            } else if (LocationManager.NETWORK_PROVIDER.equals(provider)) {
                // Use network provider if GPS is getting stale
                useLocation = now - mLastGpsFixTime > RETAIN_GPS_MILLIS;
                if (mNetworkLocation == null) {
                    mNetworkLocation = new Location(location);
                } else {
                    mNetworkLocation.set(location);
                }

                mLastGpsFixTime = 0L;
            }
            if (useLocation) {
                mProximityPainter.setUserLocation(location.getLatitude(),
                        location.getLongitude(), location.getAccuracy());
                if (mProximityDrawer != null)
                    mProximityDrawer.doDraw();
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            //Log.d("GeoBeagle", "onStatusChanged " + provider + ", " + status);
            if (LocationManager.GPS_PROVIDER.equals(provider)) {
                switch (status) {
                    case LocationProvider.AVAILABLE:
                        mGpsAvailable = true;
                        break;
                    case LocationProvider.OUT_OF_SERVICE:
                    case LocationProvider.TEMPORARILY_UNAVAILABLE:
                        mGpsAvailable = false;

                        if (mNetworkLocation != null && mNetworkAvailable) {
                            // Fallback to network location
                            mLastGpsFixTime = 0L;
                            onLocationChanged(mNetworkLocation);
                        }

                        break;
                }

            } else if (LocationManager.NETWORK_PROVIDER.equals(provider)) {
                switch (status) {
                    case LocationProvider.AVAILABLE:
                        mNetworkAvailable = true;
                        break;
                    case LocationProvider.OUT_OF_SERVICE:
                    case LocationProvider.TEMPORARILY_UNAVAILABLE:
                        mNetworkAvailable = false;
                        break;
                }
            }
        }
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        final LocationControlBuffered locationControlBuffered = LocationControlDi
                .create(locationManager);
        Location location = locationControlBuffered.getLocation();
        
        GeocacheFactory geocacheFactory = new GeocacheFactory();
        DbFrontend dbFrontend = new DbFrontend(this, geocacheFactory);
        CacheFilter cacheFilter = new CacheFilter(this);
        CachesProviderArea cachesProviderArea = new CachesProviderArea(dbFrontend, cacheFilter);
        //CachesProviderRadius cachesProviderRadius = new CachesProviderRadius(cachesProviderArea);
        CachesProviderCount cachesProviderCount = new CachesProviderCount(cachesProviderArea, 5, 10);
        //cachesProviderRadius.setRadius(0.01);
        
        mProximityPainter = new ProximityPainter(cachesProviderCount);
        mProximityPainter.setUserLocation(location.getLatitude(), location.getLongitude(), location.getAccuracy());
        
        mProximityView = new ProximityView(this);
        setContentView(mProximityView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        SurfaceHolder holder = mProximityView.getHolder();
        holder.addCallback(new SurfaceHolderCallback());
    }
}
