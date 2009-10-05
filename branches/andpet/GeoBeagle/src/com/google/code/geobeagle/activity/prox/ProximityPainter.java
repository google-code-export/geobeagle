package com.google.code.geobeagle.activity.prox;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.activity.main.GeoUtils;
import com.google.code.geobeagle.database.CachesProvider;
import com.google.code.geobeagle.database.CachesProviderCount;
import com.google.code.geobeagle.database.ICachesProviderCenter;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Paint.Style;
import android.util.Log;

import java.util.ArrayList;

public class ProximityPainter {
    
    //private int mUserX;
    /** Where on the display to show the user's location */
    private int mUserY;
    
    /* X center of display */
    private int mCenterX;
    
    private double mLatitude;
    private double mLongitude;
    /** Which direction the device is pointed, in degrees */
    private double mDirection = 0;
    
    /** Location reading accuracy in meters */
    private float mGpsAccuracy = 8;
    /** Higher value means that big distances are compressed relatively more */
    private double mLogScale = 1.3;
    private double mScaleFactor = 12.0;

    private Paint mTextPaint;
    private Paint mDistancePaint;
    private Paint mCachePaint;
    private Paint mUserPaint;
    private Paint mCompassNorthPaint;
    private Paint mCompassSouthPaint;
    //private Shader mUserShader;
    private final CachesProviderCount mCachesProvider;
    //private ArrayList<Geocache> mCaches = new ArrayList<Geocache>();
    
    /** This class contains all parameters needed to render the proximity view.
     */
    class ProximityParameters {
        private double mLatitude;
        private double mLongitude;
        /** Which direction the device is pointed, in degrees */
        private double mDeviceDirection = 0;
        /** Location reading accuracy in meters */
        private float mGpsAccuracy = 8;
        /** Higher value means that big distances are compressed relatively more */
        private double mLogScale = 1.3;
        private final double mScaleFactor = 12.0;
        private ArrayList<Geocache> mCaches;
        /** The last point in time when we got a GPS reading */
        private long mLastPositioningTime;
        /** The speed of the user, measured using GPS */
        private double mUserSpeed;
        /** Which way the user is moving */
        private double mSpeedDirection;
    }

    /** Contains the value of a single parameter. This abstraction allows for 
     * animating arbitrary parameters smoothly to their current value. */
    class Parameter {
        double mValue;
        double mTargetValue;
        /** Delta per second for mValue. Updated to move mValue towards mTargetValue */
        double mChangePerSec;
    }
    //private Parameter mDeviceDirection;
    
    public ProximityPainter(CachesProviderCount cachesProvider) {
        mCachesProvider = cachesProvider;
        mUserY = 350;
        
        //mUseImerial = mSharedPreferences.getBoolean("imperial", false);

        mTextPaint = new Paint();
        mTextPaint.setColor(Color.GREEN);

        mDistancePaint = new Paint();
        mDistancePaint.setARGB(255, 0, 96, 0);
        mDistancePaint.setStyle(Style.STROKE);
        
        mCachePaint = new Paint();
        mCachePaint.setARGB(128, 255, 0, 0);
        mCachePaint.setStyle(Style.STROKE);
        mCachePaint.setStrokeWidth(2);
        
        mUserPaint = new Paint();
        mUserPaint.setARGB(128, 0, 255, 0); //half transparent
        mUserPaint.setStyle(Style.STROKE);
        mUserPaint.setStrokeWidth(7);
        
        mCompassNorthPaint = new Paint();
        mCompassNorthPaint.setARGB(192, 255, 0, 0);
        mCompassNorthPaint.setStrokeWidth(2);
        mCompassSouthPaint = new Paint();
        mCompassSouthPaint.setARGB(192, 230, 230, 230);
        mCompassSouthPaint.setStrokeWidth(2);
    }
    
    public void setUserLocation(double latitude, double longitude, float accuracy) {
        Log.d("GeoBeagle", "setUserLocation with accuracy = " + accuracy);
        mLatitude = latitude;
        mLongitude = longitude;
        mGpsAccuracy = accuracy;
        mCachesProvider.setCenter(latitude, longitude);
        //TODO: What unit is 'radius'??
        double radius = mCachesProvider.getRadius();
        //mScaleFactor = 
        mScaleFactor = mUserY * Math.log(mLogScale) / Math.log(radius*100000);
    }
    
    public void draw(Canvas canvas) {
        canvas.drawColor(Color.BLACK); //Clear screen
        
        mCenterX = canvas.getWidth() / 2;
        int screenHeight = canvas.getHeight();
        int accuracyScreenRadius = transformDistanceToScreen(mGpsAccuracy);

        //North
        int x1 = xRelativeUser(screenHeight, Math.toRadians(270-mDirection));
        int y1 = yRelativeUser(screenHeight, Math.toRadians(270-mDirection));
        canvas.drawLine(mCenterX, mUserY, x1, y1, mCompassNorthPaint);
        //South
        int x2 = xRelativeUser(screenHeight, Math.toRadians(90-mDirection));
        int y2 = yRelativeUser(screenHeight, Math.toRadians(90-mDirection));
        canvas.drawLine(mCenterX, mUserY, x2, y2, mCompassSouthPaint);
        //West-east
        int x3 = xRelativeUser(screenHeight, Math.toRadians(180-mDirection));
        int y3 = yRelativeUser(screenHeight, Math.toRadians(180-mDirection));
        int x4 = xRelativeUser(screenHeight, Math.toRadians(-mDirection));
        int y4 = yRelativeUser(screenHeight, Math.toRadians(-mDirection));
        canvas.drawLine(x3, y3, x4, y4, mDistancePaint);
        
        int[] distanceMarks = { 10, 20, 50, 100, 500, 1000, 5000 };
        String[] distanceTexts = { "10m", "20m", "50m", "100m", "500m", "1km", "5km" };
        for (int ix = 0; ix < distanceMarks.length; ix++) {
            int distance = distanceMarks[ix];
            String text = distanceTexts[ix];
            int radius = transformDistanceToScreen(distance);
            canvas.drawCircle(mCenterX, mUserY, radius, mDistancePaint);
            if (radius > mCenterX) {
                int height = (int)Math.sqrt(radius*radius - mCenterX*mCenterX);
                canvas.drawText(text, 5, mUserY-height, mTextPaint);
            } else {
                canvas.drawText(text, mCenterX-radius, mUserY, mTextPaint);
            }
        }
        
        for (Geocache geocache : mCachesProvider.getCaches()) {
            double angle = Math.toRadians(GeoUtils.bearing(mLatitude, mLongitude, 
                    geocache.getLatitude(), geocache.getLongitude()) - mDirection);
            double distanceM = GeoUtils.distanceKm(mLatitude, mLongitude, 
                    geocache.getLatitude(), geocache.getLongitude()) * 1000;
            double screenDist = transformDistanceToScreen(distanceM);
            int cacheScreenRadius = (int)scaleFactorAtDistance(distanceM);
            mCachePaint.setStrokeWidth((int)Math.ceil(0.5*scaleFactorAtDistance(distanceM)));
            int x = mCenterX + (int)(screenDist * Math.cos(angle));
            int y = mUserY + (int)(screenDist * Math.sin(angle));
            canvas.drawCircle(x, y, cacheScreenRadius, mCachePaint);
            if (screenDist > accuracyScreenRadius + cacheScreenRadius) {
                int x5 = xRelativeUser(accuracyScreenRadius, angle);
                int y5 = yRelativeUser(accuracyScreenRadius, angle);
                int x6 = xRelativeUser(screenDist-cacheScreenRadius, angle);
                int y6 = yRelativeUser(screenDist-cacheScreenRadius, angle);
                //Cache is outside accuracy circle
                canvas.drawLine(x5, y5, x6, y6, mCachePaint);
            }
        }
        canvas.drawCircle(mCenterX, mUserY, accuracyScreenRadius, mUserPaint);
    }

    /** angle is in radians */
    private int xRelativeUser(double distance, double angle) {
        return mCenterX + (int)(distance * Math.cos(angle));
    }
    /** angle is in radians */
    private int yRelativeUser(double distance, double angle) {
        return mUserY + (int)(distance * Math.sin(angle));
    }
    
    /** Return the distance in pixels for a real-world distance in meters */
    private int transformDistanceToScreen(double meters) {
        if (meters < 6)
            meters = 1;
        else
            meters -= 5;
        return (int)(mScaleFactor * myLog(meters));
    }

    /** At distance 'meters', draw a meter as these many pixels */
    private double scaleFactorAtDistance(double meters) {
        if (meters < 6)
            meters = 1;
        else
            meters -= 5;
        return (int)(2000.0/(mScaleFactor * myLog(meters)));
    }

    /** Returns logarithm of x in base mLogScale */
    private double myLog(double x) {
        return Math.log(x) / Math.log(mLogScale);
    }
    
    public void setUserDirection(float degrees) {
        mDirection = degrees;
    }
}
