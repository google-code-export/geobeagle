/*
 ** Licensed under the Apache License, Version 2.0 (the "License");
 ** you may not use this file except in compliance with the License.
 ** You may obtain a copy of the License at
 **
 **     http://www.apache.org/licenses/LICENSE-2.0
 **
 ** Unless required by applicable law or agreed to in writing, software
 ** distributed under the License is distributed on an "AS IS" BASIS,
 ** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ** See the License for the specific language governing permissions and
 ** limitations under the License.
 */

package com.google.code.geobeagle.ui;

import com.google.code.geobeagle.CombinedLocationManager;
import com.google.code.geobeagle.LocationControlBuffered;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.ResourceProvider;
import com.google.code.geobeagle.ui.Misc.Time;

import android.content.Context;
import android.graphics.Canvas;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author sng Displays the GPS status (mAccuracy, availability, etc).
 */
public class GpsStatusWidget extends LinearLayout implements LocationListener {

    public static class GpsStatusWidgetDelegate {
        private float mAccuracy;
        private final TextView mAccuracyView;
        private CombinedLocationManager mCombinedLocationManager;
        private final TextView mLag;
        private long mLastUpdateTime;
        private final LocationControlBuffered mLocationControlBuffered;
        private long mLocationTime;
        private final MeterView mMeterView;
        private View mParent;
        private final TextView mProvider;
        private final ResourceProvider mResourceProvider;
        private final TextView mStatus;
        private final Time mTime;

        public GpsStatusWidgetDelegate(GpsStatusWidget parent, TextView accuracyView,
                LocationControlBuffered locationControlBuffered,
                CombinedLocationManager combinedLocationManager, TextView lag, MeterView meterView,
                TextView provider, ResourceProvider resourceProvider, TextView status, Time time) {
            mParent = parent;
            mAccuracyView = accuracyView;
            mCombinedLocationManager = combinedLocationManager;
            mLag = lag;
            mLocationControlBuffered = locationControlBuffered;
            mMeterView = meterView;
            mProvider = provider;
            mStatus = status;
            mTime = time;
            mResourceProvider = resourceProvider;
            mLastUpdateTime = 0;
        }

        void fadeMeter() {
            long lastUpdateLag = mTime.getCurrentTime() - mLastUpdateTime;
            mMeterView.setLag(lastUpdateLag);
            if (lastUpdateLag < 1000)
                mParent.postInvalidateDelayed(100);
            // Log.v("GeoBeagle", "painting " + lastUpdateLag);
        }

        public void onLocationChanged(Location location) {
            if (location == null)
                return;

            if (!mCombinedLocationManager.isProviderEnabled()) {
                mLag.setText("");
                mAccuracyView.setText("");
                mMeterView.set(Float.MAX_VALUE, 0);
                return;
            }

            mLastUpdateTime = mTime.getCurrentTime();
            mLocationTime = Math.min(location.getTime(), mLastUpdateTime);
            if (mLastUpdateTime - mLocationTime < 4000)
                mLocationTime = mLastUpdateTime;
            mProvider.setText(location.getProvider());

            mAccuracy = location.getAccuracy();
            mAccuracyView.setText((Integer.toString((int)mAccuracy) + "m").trim());

            updateMeter();
            updateLag();

            mParent.postInvalidate();
        }

        public void onProviderDisabled(String provider) {
            mStatus.setText(provider + " DISABLED");
        }

        public void onProviderEnabled(String provider) {
            mStatus.setText(provider + " ENABLED");
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.OUT_OF_SERVICE:
                    mStatus.setText(provider + " status: "
                            + mResourceProvider.getString(R.string.out_of_service));
                    break;
                case LocationProvider.AVAILABLE:
                    mStatus.setText(provider + " status: "
                            + mResourceProvider.getString(R.string.available));
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    mStatus.setText(provider + " status: "
                            + mResourceProvider.getString(R.string.temporarily_unavailable));
                    break;
            }
        }

        public void updateLag() {
            final long locationLag = mTime.getCurrentTime() - mLocationTime;
            mLag.setText(Long.toString(locationLag / 1000) + "s");
        }

        public void updateMeter() {
            mMeterView.set(mAccuracy, mLocationControlBuffered.getAzimuth());
        }
    }

    public static class UpdateGpsWidgetRunnable implements Runnable {
        private final GpsStatusWidgetDelegate mGpsStatusWidgetDelegate;
        private final Handler mHandler;

        UpdateGpsWidgetRunnable(GpsStatusWidgetDelegate gpsStatusWidgetDelegate, Handler handler) {
            mGpsStatusWidgetDelegate = gpsStatusWidgetDelegate;
            mHandler = handler;
        }

        public void run() {
            // Update the lag time (seconds) and the orientation.
            mGpsStatusWidgetDelegate.updateLag();
            mGpsStatusWidgetDelegate.updateMeter();

            mHandler.postDelayed(this, 500);
        }
    }

    private final GpsStatusWidgetDelegate mGpsStatusWidgetDelegate;

    public GpsStatusWidget(Context context, ResourceProvider resourceProvider,
            MeterView.MeterFormatter meterFormatter, Time time,
            CombinedLocationManager locationManager, LocationControlBuffered locationControlBuffered) {
        super(context);

        final LayoutInflater inflater = (LayoutInflater)context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View gpsWidgetView = inflater.inflate(R.layout.gps_widget, this);

        final TextView accuracyView = (TextView)gpsWidgetView.findViewById(R.id.accuracy);
        final TextView lag = (TextView)gpsWidgetView.findViewById(R.id.lag);
        final TextView provider = (TextView)gpsWidgetView.findViewById(R.id.provider);
        final TextView status = (TextView)gpsWidgetView.findViewById(R.id.status);
        final TextView locationViewer = (TextView)gpsWidgetView.findViewById(R.id.location_viewer);

        final MeterView meterView = new MeterView(locationViewer, meterFormatter);

        mGpsStatusWidgetDelegate = new GpsStatusWidgetDelegate(this, accuracyView,
                locationControlBuffered, locationManager, lag, meterView, provider,
                resourceProvider, status, time);
    }

    public GpsStatusWidgetDelegate getGpsStatusWidgetDelegate() {
        return mGpsStatusWidgetDelegate;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        mGpsStatusWidgetDelegate.fadeMeter();
        Log.v("GeoBeagle", "dispatch draw");
        super.dispatchDraw(canvas);
    };

    public void onLocationChanged(Location location) {
        mGpsStatusWidgetDelegate.onLocationChanged(location);
    }

    public void onProviderDisabled(String provider) {
        mGpsStatusWidgetDelegate.onProviderDisabled(provider);
    }

    public void onProviderEnabled(String provider) {
        mGpsStatusWidgetDelegate.onProviderEnabled(provider);
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        mGpsStatusWidgetDelegate.onStatusChanged(provider, status, extras);
    }
}
