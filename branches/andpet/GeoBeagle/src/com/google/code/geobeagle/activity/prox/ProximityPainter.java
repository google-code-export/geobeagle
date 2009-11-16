package com.google.code.geobeagle.activity.prox;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.GeocacheList;
import com.google.code.geobeagle.GeocacheListPrecomputed;
import com.google.code.geobeagle.activity.main.GeoUtils;
import com.google.code.geobeagle.database.CachesProviderCount;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Paint.Style;
import android.graphics.Shader.TileMode;
import android.util.Log;

public class ProximityPainter {
    //private int mUserX;
    /** Where on the display to show the user's location */
    private int mUserY;
    
    /* X center of display */
    private int mCenterX;
    
    /** Higher value means that big distances are compressed relatively more */
    private double mLogScale = 1.25;

    private final int mUserScreenRadius = 25;
    
    private Paint mTextPaint;
    private Paint mDistancePaint;
    private Paint mMainDistancePaint;
    private Paint mCachePaint;
    private Paint mUserPaint;
    private Paint mSpeedPaint;
    private Paint mAccuracyPaint;
    private Paint mCompassNorthPaint;
    private Paint mCompassSouthPaint;
    private Paint mGlowPaint;
    private final CachesProviderCount mCachesProvider;
    private Geocache mSelectedGeocache;

    /** Which direction the device is pointed, in degrees */
    private Parameter mDeviceDirection = new AngularParameter(0.03, 0.3);

    /** Location reading accuracy in meters */
    private Parameter mGpsAccuracy = new ScalarParameter(4.0);

    private Parameter mScaleFactor = new ScalarParameter(12.0);

    /** The speed of the user, measured using GPS */
    private Parameter mUserSpeed = new ScalarParameter(0);
    /** Which way the user is moving */
    private Parameter mUserDirection = new AngularParameter(0.03, 0.3);
    
    private Parameter mLatitude = new ScalarParameter();
    private Parameter mLongitude = new ScalarParameter();
    private Rect mTempBounds = new Rect();
    
    private Parameter[] allParameters = { mDeviceDirection, mGpsAccuracy,
            mScaleFactor, mUserSpeed, mUserDirection, mLatitude, mLongitude };
    
    public ProximityPainter(CachesProviderCount cachesProvider) {
        mCachesProvider = cachesProvider;
        mUserY = 350;
        
        //mUseImerial = mSharedPreferences.getBoolean("imperial", false);

        mTextPaint = new Paint();
        mTextPaint.setColor(Color.GREEN);

        mDistancePaint = new Paint();
        mDistancePaint.setARGB(255, 0, 96, 0);
        mDistancePaint.setStyle(Style.STROKE);
        mDistancePaint.setStrokeWidth(2);
        mDistancePaint.setAntiAlias(true);
        
        mMainDistancePaint = new Paint(mDistancePaint);
        mMainDistancePaint.setARGB(255, 0, 200, 0);
        mMainDistancePaint.setStrokeWidth(4);
        
        mCachePaint = new Paint();
        mCachePaint.setARGB(255, 200, 200, 248);
        mCachePaint.setStyle(Style.STROKE);
        mCachePaint.setStrokeWidth(2);
        mCachePaint.setAntiAlias(true);

        int userColor = Color.argb(255, 216, 176, 128);
        //mGlowShader = new RadialGradient(0, 0, 
        //        (int)mGpsAccuracy.get(), 0x2000ff00, 0xc000ff00, TileMode.CLAMP);
        mAccuracyPaint = new Paint();
        //mAccuracyPaint.setShader(mGlowShader);
        mAccuracyPaint.setStrokeWidth(6);
        mAccuracyPaint.setStyle(Style.STROKE);
        mAccuracyPaint.setColor(userColor);
        float[] intervals = { 20, 10};
        mAccuracyPaint.setPathEffect(new DashPathEffect(intervals, 5));
        mAccuracyPaint.setAntiAlias(true);
        
        mUserPaint = new Paint();
        mUserPaint.setColor(userColor);
        mUserPaint.setStyle(Style.STROKE);
        mUserPaint.setStrokeWidth(6);
        mUserPaint.setAntiAlias(true);
        
        mSpeedPaint = new Paint();
        mSpeedPaint.setColor(userColor);
        mSpeedPaint.setStrokeWidth(6);
        mSpeedPaint.setStyle(Style.STROKE);
        mSpeedPaint.setAntiAlias(true);
        
        mCompassNorthPaint = new Paint();
        mCompassNorthPaint.setARGB(255, 255, 0, 0);
        mCompassNorthPaint.setStrokeWidth(3);
        mCompassNorthPaint.setAntiAlias(true);
        mCompassSouthPaint = new Paint();
        mCompassSouthPaint.setARGB(255, 230, 230, 230);
        mCompassSouthPaint.setStrokeWidth(3);
        mCompassSouthPaint.setAntiAlias(true);
        
        mGlowPaint = new Paint();
        mGlowPaint.setStyle(Style.STROKE);
    }
    
    public void setUserLocation(double latitude, double longitude, float accuracy) {
        mLatitude.set(latitude);
        mLongitude.set(longitude);
        mGpsAccuracy.set(accuracy);
        mCachesProvider.setCenter(latitude, longitude);
    }

    public void setSelectedGeocache(Geocache geocache) {
        mSelectedGeocache = geocache;
    }
    
    public void draw(Canvas canvas) {
        canvas.drawColor(Color.BLACK); //Clear screen

        mCenterX = canvas.getWidth() / 2;
        //The maximum pixel distance from user point that's visible on screen
        int maxScreenRadius = (int)Math.ceil(Math.sqrt(mCenterX*mCenterX + mUserY*mUserY));
        int accuracyScreenRadius = transformDistanceToScreen(mGpsAccuracy.get());
        double direction = mDeviceDirection.get();

        //Draw accuracy blur field
        if (accuracyScreenRadius > 0) {
            //TODO: Wasting objects!
            /*
            mGlowShader = new RadialGradient(mCenterX, mUserY,
                    accuracyScreenRadius, 0x80ffff00, 0x20ffff00, TileMode.CLAMP);
            mAccuracyPaint.setShader(mGlowShader);
            */
            canvas.drawCircle(mCenterX, mUserY, accuracyScreenRadius, mAccuracyPaint);
        }
        
        //North
        int x1 = xRelativeUser(maxScreenRadius, Math.toRadians(270-direction));
        int y1 = yRelativeUser(maxScreenRadius, Math.toRadians(270-direction));
        canvas.drawLine(mCenterX, mUserY, x1, y1, mCompassNorthPaint);
        //South
        int x2 = xRelativeUser(maxScreenRadius, Math.toRadians(90-direction));
        int y2 = yRelativeUser(maxScreenRadius, Math.toRadians(90-direction));
        canvas.drawLine(mCenterX, mUserY, x2, y2, mCompassSouthPaint);
        //West-east
        int x3 = xRelativeUser(maxScreenRadius, Math.toRadians(180-direction));
        int y3 = yRelativeUser(maxScreenRadius, Math.toRadians(180-direction));
        int x4 = xRelativeUser(maxScreenRadius, Math.toRadians(-direction));
        int y4 = yRelativeUser(maxScreenRadius, Math.toRadians(-direction));
        canvas.drawLine(x3, y3, x4, y4, mDistancePaint);

        //Draw user speed vector
        if (mUserSpeed.get() > 0) {
            int speedRadius = transformDistanceToScreen(mUserSpeed.get()*3.6);
            int x7 = xRelativeUser(speedRadius, Math.toRadians(mUserDirection.get()-direction-90));
            int y7 = yRelativeUser(speedRadius, Math.toRadians(mUserDirection.get()-direction-90));
            canvas.drawLine(mCenterX, mUserY, x7, y7, mSpeedPaint);
        }
        
        int[] distanceMarks = { 10, 20, 50, 100, 500, 1000, 5000, 10000, 50000, 100000, 1000000 };
        String[] distanceTexts = { "10m", "20m", "50m", "100m", "500m", "1km", "5km", "10km", "50km", "100km", "1000km" };
        int lastLeft = mCenterX;
        for (int ix = 0; ix < distanceMarks.length; ix++) {
            int distance = distanceMarks[ix];
            String text = distanceTexts[ix];
            int radius = transformDistanceToScreen(distance);
            if (radius > maxScreenRadius)
                //Not visible anywhere on screen
                break;
            if (distance == 100 || distance == 1000)
                canvas.drawCircle(mCenterX, mUserY, radius, mMainDistancePaint);
            else
                canvas.drawCircle(mCenterX, mUserY, radius, mDistancePaint);
            if (radius > mCenterX) {
                int height = (int)Math.sqrt(radius*radius - mCenterX*mCenterX);
                canvas.drawText(text, 5, mUserY-height-5, mTextPaint);
            } else {
                mTextPaint.getTextBounds(text, 0, text.length(), mTempBounds);
                int left = mCenterX-radius-10;
                if (left + mTempBounds.right > lastLeft)
                    continue;  //Don't draw text that would overlap
                lastLeft = left;
                canvas.drawText(text, left, mUserY, mTextPaint);
            }
        }
        
        GeocacheList caches = mCachesProvider.getCaches();
        //Draw all geocaches and lines to them
        if (mCachesProvider.hasChanged()) {
            Log.d("GeoBeagle", "ProximityPainter drawing " + caches.size() + " caches");
            mCachesProvider.resetChanged();
        }

        if (mSelectedGeocache != null && !caches.contains(mSelectedGeocache)) {
            caches = new GeocacheListPrecomputed(caches, mSelectedGeocache);
        }

        double maxDistanceM = 0;
        for (Geocache geocache : caches) {
            double angle = Math.toRadians(GeoUtils.bearing(mLatitude.get(), mLongitude.get(), 
                    geocache.getLatitude(), geocache.getLongitude()) - direction - 90);
            double distanceM = GeoUtils.distanceKm(mLatitude.get(), mLongitude.get(), 
                    geocache.getLatitude(), geocache.getLongitude()) * 1000;
            if (distanceM > maxDistanceM)
                maxDistanceM = distanceM;
            double screenDist = transformDistanceToScreen(distanceM);
            int cacheScreenRadius = (int)(2*scaleFactorAtDistance(distanceM*2));
            mCachePaint.setStrokeWidth((int)Math.ceil(0.5*scaleFactorAtDistance(distanceM)));
            mCachePaint.setAlpha(255);
            int x = mCenterX + (int)(screenDist * Math.cos(angle));
            int y = mUserY + (int)(screenDist * Math.sin(angle));
            if (geocache == mSelectedGeocache) {
                int glowTh = (int)(mCachePaint.getStrokeWidth() * 2); //(1.0 + Math.abs(Math.sin(mTime))));
                drawGlow(canvas, x, y, (int)(cacheScreenRadius+mCachePaint.getStrokeWidth()/2), glowTh);
            }
            canvas.drawCircle(x, y, cacheScreenRadius, mCachePaint);
            //Lines to geocaches
            if (screenDist > mUserScreenRadius + cacheScreenRadius) {
                //Cache is outside accuracy circle
                int x5 = xRelativeUser(mUserScreenRadius, angle);
                int y5 = yRelativeUser(mUserScreenRadius, angle);
                int x6 = xRelativeUser(screenDist-cacheScreenRadius, angle);
                int y6 = yRelativeUser(screenDist-cacheScreenRadius, angle);
                mCachePaint.setStrokeWidth(Math.min(8, cacheScreenRadius));
                double closeness = 1 - (0.7*screenDist)/maxScreenRadius;
                mCachePaint.setAlpha((int)Math.min(255, 256 * 1.5 * closeness));
                canvas.drawLine(x5, y5, x6, y6, mCachePaint);
            }
        }
        
        mScaleFactor.set(mUserY * Math.log(mLogScale) / Math.log(maxDistanceM/2f));
        
        canvas.drawCircle(mCenterX, mUserY, mUserScreenRadius, mUserPaint);
    }

    /** 
     * @param radius The smallest radius that will glow
     * @param thickness Thickness of glow
     */
    private void drawGlow(Canvas c, int x, int y, int radius, int thickness) {
        int color = Color.argb(255, 200, 200, 248);
        int[] colors = { color, color, 0x00000000};
        float[] positions = { 0, ((float)radius)/(radius+thickness), 1f };
        //float[] positions = { 0, (radius + 0.5f*thickness)/(radius+thickness), 1f };
        //float[] positions = { 0.5f, 0.9f, 1f };
        //TODO: Wastes objects
        Shader glowShader;
        glowShader = new RadialGradient(x, y, radius+thickness*1.5f, colors, positions, TileMode.MIRROR);
        mGlowPaint.setShader(glowShader);
        mGlowPaint.setStrokeWidth(radius);
        c.drawCircle(x, y, radius+thickness/2, mGlowPaint);
    }
    
    /** angle is in radians */
    private int xRelativeUser(double distance, double angle) {
        return mCenterX + (int)(distance * Math.cos(angle));
    }
    /** angle is in radians */
    private int yRelativeUser(double distance, double angle) {
        return mUserY + (int)(distance * Math.sin(angle));
    }
    
    /** Return the distance in pixels for a real-world distance in meters
     * Argument must be positive */
    private int transformDistanceToScreen(double meters) {
        int distance = (int)(mScaleFactor.get() * myLog(meters));
        return Math.max(20, distance - 40);
    }

    /** At distance 'meters', draw a meter as these many pixels */
    private double scaleFactorAtDistance(double meters) {
        if (meters < 6)
            meters = 1;
        else
            meters -= 5;
        return (int)(2000.0/(mScaleFactor.get() * myLog(meters)));
    }

    /** Returns logarithm of x in base mLogScale */
    private double myLog(double x) {
        return Math.log(x) / Math.log(mLogScale);
    }
    
    /** bearing 0 is north, bearing 90 is east */
    public void setUserDirection(double degrees) {
        mDeviceDirection.set(degrees);
    }

    /** bearing 0 is north, bearing 90 is east */
    public void setUserMovement(double bearing, double speed) {
        mUserSpeed.set(speed);
        mUserDirection.set(bearing);
    }

    /** Seconds */
    private double mTime;
    
    /** Update animations timeDelta seconds (preferably much less than a sec) */
    public void advanceTime(double timeDelta) {
        mTime += timeDelta;
        for (Parameter param : allParameters) {
            param.update(timeDelta);
        }
    }
}
