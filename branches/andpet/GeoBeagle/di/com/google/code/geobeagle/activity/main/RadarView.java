/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.code.geobeagle.activity.main;

/*
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.google.code.geobeagle.GeoFix;
import com.google.code.geobeagle.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class RadarView extends View {

    private Paint mGridPaint;
    private Paint mErasePaint;
    private float mOrientation;
    private double mTargetLat;
    private double mTargetLon;

    private static float KM_PER_METERS = 0.001f;
    private static float METERS_PER_KM = 1000f;

    /**
     * These are the list of choices for the radius of the outer circle on the
     * screen when using metric units. All items are in kilometers. This array
     * is used to choose the scale of the radar display.
     */
    private static double mMetricScaleChoices[] = {
            100 * KM_PER_METERS, 200 * KM_PER_METERS, 400 * KM_PER_METERS, 1, 2, 4, 8, 20, 40, 100,
            200, 400, 1000, 2000, 4000, 10000, 20000, 40000, 80000
    };

    /**
     * Once the scale is chosen, this array is used to convert the number of
     * kilometers on the screen to an integer. (Note that for short distances we
     * use meters, so we multiply the distance by {@link #METERS_PER_KM}. (This
     * array is for metric measurements.)
     */
    private static float mMetricDisplayUnitsPerKm[] = {
            METERS_PER_KM, METERS_PER_KM, METERS_PER_KM, METERS_PER_KM, METERS_PER_KM, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f
    };

    /**
     * This array holds the formatting string used to display the distance to
     * the target. (This array is for metric measurements.)
     */
    private static String mMetricDisplayFormats[] = {
            "%.0fm", "%.0fm", "%.0fm", "%.0fm", "%.0fm", "%.1fkm", "%.1fkm", "%.0fkm", "%.0fkm",
            "%.0fkm", "%.0fkm", "%.0fkm", "%.0fkm", "%.0fkm", "%.0fkm", "%.0fkm", "%.0fkm",
            "%.0fkm", "%.0fkm"
    };

    private static float KM_PER_FEET = 0.0003048f;
    private static float KM_PER_MILES = 1.609344f;
    private static float FEET_PER_KM = 3280.8399f;
    private static float MILES_PER_KM = 0.621371192f;

    /**
     * These are the list of choices for the radius of the outer circle on the
     * screen when using standard units. All items are in kilometers. This array
     * is used to choose the scale of the radar display.
     */
    private static double mEnglishScaleChoices[] = {
            100 * KM_PER_FEET, 200 * KM_PER_FEET, 400 * KM_PER_FEET, 1000 * KM_PER_FEET,
            1 * KM_PER_MILES, 2 * KM_PER_MILES, 4 * KM_PER_MILES, 8 * KM_PER_MILES,
            20 * KM_PER_MILES, 40 * KM_PER_MILES, 100 * KM_PER_MILES, 200 * KM_PER_MILES,
            400 * KM_PER_MILES, 1000 * KM_PER_MILES, 2000 * KM_PER_MILES, 4000 * KM_PER_MILES,
            10000 * KM_PER_MILES, 20000 * KM_PER_MILES, 40000 * KM_PER_MILES, 80000 * KM_PER_MILES
    };

    /**
     * Once the scale is chosen, this array is used to convert the number of
     * kilometers on the screen to an integer. (Note that for short distances we
     * use meters, so we multiply the distance by {@link #YARDS_PER_KM}. (This
     * array is for standard measurements.)
     */
    private static float mEnglishDisplayUnitsPerKm[] = {
            FEET_PER_KM, FEET_PER_KM, FEET_PER_KM, FEET_PER_KM, MILES_PER_KM, MILES_PER_KM,
            MILES_PER_KM, MILES_PER_KM, MILES_PER_KM, MILES_PER_KM, MILES_PER_KM, MILES_PER_KM,
            MILES_PER_KM, MILES_PER_KM, MILES_PER_KM, MILES_PER_KM, MILES_PER_KM, MILES_PER_KM,
            MILES_PER_KM, MILES_PER_KM
    };

    /**
     * This array holds the formatting string used to display the distance to
     * the target. (This array is for standard measurements.)
     */
    private static String mEnglishDisplayFormats[] = {
            "%.0fft", "%.0fft", "%.0fft", "%.0fft", "%.1fmi", "%.1fmi", "%.1fmi", "%.1fmi",
            "%.0fmi", "%.0fmi", "%.0fmi", "%.0fmi", "%.0fmi", "%.0fmi", "%.0fmi", "%.0fmi",
            "%.0fmi", "%.0fmi", "%.0fmi", "%.0fmi"
    };

    private boolean mHaveLocation = false; // True when we have our location
    private TextView mDistanceView;
    private double mDistance; // Distance to target, in KM
    private double mBearing; // Bearing to target, in degrees

    // Ratio of the distance to the target to the radius of the outermost ring
    // on the radar screen
    private float mDistanceRatio;
    private Bitmap mBlip; // The bitmap used to draw the target

    // True if the display should use metric units; false if the display should
    // use standard units
    private boolean mUseMetric;

    private TextView mBearingView;
    private TextView mAccuracyView;
    private TextView mLagView;
    private final String mDegreesSymbol;
    private Path mCompassPath;
    private final Paint mCompassPaint;
    private final Paint mArrowPaint;
    private final Path mArrowPath;
    private GeoFix mMyLocation;

    public RadarView(Context context) {
        this(context, null);
    }

    public RadarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mDegreesSymbol = context.getString(R.string.degrees_symbol);

        // Paint used for the rings and ring text
        mGridPaint = new Paint();
        mGridPaint.setColor(0xFF00FF00);
        mGridPaint.setAntiAlias(true);
        mGridPaint.setStyle(Style.STROKE);
        mGridPaint.setStrokeWidth(1.0f);
        mGridPaint.setTextSize(10.0f);
        mGridPaint.setTextAlign(Align.CENTER);

        mCompassPaint = new Paint();
        mCompassPaint.setColor(0xFF00FF00);
        mCompassPaint.setAntiAlias(true);
        mCompassPaint.setStyle(Style.STROKE);
        mCompassPaint.setStrokeWidth(1.0f);
        mCompassPaint.setTextSize(10.0f);
        mCompassPaint.setTextAlign(Align.CENTER);

        // Paint used to erase the rectangle behind the ring text
        mErasePaint = new Paint();
        mErasePaint.setColor(0xFF191919);
        mErasePaint.setStyle(Style.FILL);

        // Paint used for the arrow
        mArrowPaint = new Paint();
        mArrowPaint.setColor(Color.WHITE);
        mArrowPaint.setAntiAlias(true);
        mArrowPaint.setStyle(Style.STROKE);
        mArrowPaint.setStrokeWidth(16);
        mArrowPaint.setAlpha(228);
        mArrowPath = new Path();

        mBlip = ((BitmapDrawable)getResources().getDrawable(R.drawable.blip)).getBitmap();
        mCompassPath = new Path();
    }

    /**
     * Sets the target to track on the radar
     * 
     * @param latE6 Latitude of the target, multiplied by 1,000,000
     * @param lonE6 Longitude of the target, multiplied by 1,000,000
     */
    public void setTarget(int latE6, int lonE6) {
        mTargetLat = latE6 / (double)GeoUtils.MILLION;
        mTargetLon = lonE6 / (double)GeoUtils.MILLION;
    }

    /**
     * Sets the view that we will use to report distance
     * 
     * @param t The text view used to report distance
     */
    public void setDistanceView(TextView d, TextView b, TextView a,
            TextView lag) {
        mDistanceView = d;
        mBearingView = b;
        mAccuracyView = a;
        mLagView = lag;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int center = Math.min(getHeight(), getWidth()) / 2;
        int radius = center - 8;

        // Draw the rings
        final Paint gridPaint = mGridPaint;
        gridPaint.setAlpha(100);

        canvas.drawCircle(center, center, radius, gridPaint);
        canvas.drawCircle(center, center, radius * 3 / 4, gridPaint);
        canvas.drawCircle(center, center, radius >> 1, gridPaint);
        canvas.drawCircle(center, center, radius >> 2, gridPaint);

        int blipRadius = (int)(mDistanceRatio * radius);

        // Draw horizontal and vertical lines
        canvas.drawLine(center, center - (radius >> 2) + 6, center, center - radius - 6, gridPaint);
        canvas.drawLine(center, center + (radius >> 2) - 6, center, center + radius + 6, gridPaint);
        canvas.drawLine(center - (radius >> 2) + 6, center, center - radius - 6, center, gridPaint);
        canvas.drawLine(center + (radius >> 2) - 6, center, center + radius + 6, center, gridPaint);

        if (mHaveLocation) {
            double northAngle = Math.toRadians(-mOrientation) - (Math.PI / 2);
            float northX = (float)Math.cos(northAngle);
            float northY = (float)Math.sin(northAngle);
            final int compassLength = radius >> 2;
            float tipX = northX * compassLength, tipY = northY * compassLength;
            float baseX = northY * 8, baseY = -northX * 8;

            double bearingToTarget = mBearing - mOrientation;
            double drawingAngle = Math.toRadians(bearingToTarget) - (Math.PI / 2);

            float cos = (float)Math.cos(drawingAngle);
            float sin = (float)Math.sin(drawingAngle);

            mArrowPath.reset();
            mArrowPath.moveTo(center - cos * radius, center - sin * radius);
            mArrowPath.lineTo(center + cos * radius, center + sin * radius);

            final double arrowRight = drawingAngle + Math.PI / 2;
            final double arrowLeft = drawingAngle - Math.PI / 2;
            mArrowPath.moveTo(center + (float)Math.cos(arrowRight) * radius, center
                    + (float)Math.sin(arrowRight) * radius);
            mArrowPath.lineTo(center + cos * radius, center + sin * radius);
            mArrowPath.lineTo(center + (float)Math.cos(arrowLeft) * radius, center
                    + (float)Math.sin(arrowLeft) * radius);

            canvas.drawPath(mArrowPath, mArrowPaint);

            drawCompassArrow(canvas, center, mCompassPaint, tipX, tipY, baseX, baseY, Color.RED);
            drawCompassArrow(canvas, center, mCompassPaint, -tipX, -tipY, baseX, baseY, Color.GRAY);

            gridPaint.setAlpha(255);
            canvas.drawBitmap(mBlip, center + (cos * blipRadius) - 8, center + (sin * blipRadius)
                    - 8, gridPaint);
        }
    }

    private void drawCompassArrow(Canvas canvas, int center, final Paint gridPaint, float tipX,
            float tipY, float baseX, float baseY, int color) {
        gridPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        gridPaint.setColor(color);
        gridPaint.setAlpha(255);
        mCompassPath.reset();
        mCompassPath.moveTo(center + baseX, center + baseY);
        mCompassPath.lineTo(center + tipX, center + tipY);
        mCompassPath.lineTo(center - baseX, center - baseY);
        mCompassPath.close();
        canvas.drawPath(mCompassPath, gridPaint);
        gridPaint.setStyle(Paint.Style.STROKE);
    }

    public void setLocation(GeoFix location, float azimuth) {
        // Log.d("GeoBeagle", "radarview::onLocationChanged");
        mHaveLocation = true;
        mMyLocation = location;
        mOrientation = azimuth;

        double lat = location.getLatitude();
        double lon = location.getLongitude();
        mDistance = GeoUtils.distanceKm(lat, lon, mTargetLat, mTargetLon);
        mBearing = GeoUtils.bearing(lat, lon, mTargetLat, mTargetLon);

        updateDistance(mDistance);
        double bearingToTarget = mBearing - mOrientation;
        updateBearing(bearingToTarget);
        postInvalidate();
    }

    /**
     * Called when we no longer have a valid location.
     */
    public void handleUnknownLocation() {
        mHaveLocation = false;
        mDistanceView.setText("");
        mAccuracyView.setText("");
        mLagView.setText("");
        mBearingView.setText("");
    }

    /**
     * Update state to reflect whether we are using metric or standard units.
     * 
     * @param useMetric True if the display should use metric units
     */
    public void setUseImperial(boolean useImperial) {
        mUseMetric = !useImperial;
        if (mHaveLocation) {
            updateDistance(mDistance);
        }
        invalidate();
    }

    private void updateBearing(double bearing) {
        bearing = (bearing + 720) % 360;
        if (mHaveLocation) {
            final String sBearing = ((int)bearing / 5) * 5 + mDegreesSymbol;

            mBearingView.setText(sBearing);
        }
    }

    /**
     * Update our state to reflect a new distance to the target. This may
     * require choosing a new scale for the radar rings.
     * 
     * @param distanceKm The new distance to the target
     * @param bearing
     */
    private void updateDistance(double distanceKm) {
        final double[] scaleChoices;
        final float[] displayUnitsPerKm;
        final String[] displayFormats;
        String distanceStr = null;

        if (mUseMetric) {
            scaleChoices = mMetricScaleChoices;
            displayUnitsPerKm = mMetricDisplayUnitsPerKm;
            displayFormats = mMetricDisplayFormats;
        } else {
            scaleChoices = mEnglishScaleChoices;
            displayUnitsPerKm = mEnglishDisplayUnitsPerKm;
            displayFormats = mEnglishDisplayFormats;
        }

        final int count = scaleChoices.length;
        for (int i = 0; i < count; i++) {
            if (distanceKm < scaleChoices[i] || i == (count - 1)) {
                String format = displayFormats[i];
                double distanceDisplay = distanceKm * displayUnitsPerKm[i];
                mDistanceRatio = (float)(mDistance / scaleChoices[i]);
                distanceStr = String.format(format, distanceDisplay);
                break;
            }
        }
        mDistanceView.setText(distanceStr);

        String accuracyStr = formatDistance(mMyLocation.getAccuracy(), scaleChoices, 
                displayUnitsPerKm, displayFormats);
        mAccuracyView.setText(accuracyStr);


        mLagView.setText(mMyLocation.getLagString(System.currentTimeMillis()));
    }

    private static String formatDistance(float accuracy, final double[] scaleChoices, 
            final float[] displayUnitsPerKm, final String[] displayFormats) {
        if (accuracy == 0.0)
            return "";
        int count = scaleChoices.length;
        for (int i = 0; i < count; i++) {
            final float myLocationAccuracyKm = accuracy / 1000;
            if (myLocationAccuracyKm < scaleChoices[i] || i == (count - 1)) {
                final String format = displayFormats[i];
                return "+/-" + String.format(format, myLocationAccuracyKm * displayUnitsPerKm[i]);
            }
        }
        return "";
    }

}
