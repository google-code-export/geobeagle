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

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.ResourceProvider;
import com.google.code.geobeagle.location.CombinedLocationManager;
import com.google.code.geobeagle.location.LocationControlBuffered;
import com.google.code.geobeagle.ui.MeterView.MeterFormatter;
import com.google.code.geobeagle.ui.Misc.Time;

import android.content.Context;
import android.graphics.Canvas;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author sng Displays the GPS status (mAccuracy, availability, etc).
 */
public class GpsStatusWidget extends LinearLayout implements LocationListener {

    public static class GpsStatusWidgetDelegate {
        private final CombinedLocationManager mCombinedLocationManager;
        private final MeterFader mMeterFader;
        private final MeterWrapper mMeterWrapper;
        private final TextView mProvider;
        private final ResourceProvider mResourceProvider;
        private final TextView mStatus;
        private final TextLagUpdater mTextLagUpdater;

        public GpsStatusWidgetDelegate(CombinedLocationManager combinedLocationManager,
                MeterFader meterFader, MeterWrapper meterWrapper, TextView provider,
                ResourceProvider resourceProvider, TextView status, TextLagUpdater textLagUpdater) {
            mCombinedLocationManager = combinedLocationManager;
            mMeterFader = meterFader;
            mMeterWrapper = meterWrapper;
            mProvider = provider;
            mStatus = status;
            mTextLagUpdater = textLagUpdater;
            mResourceProvider = resourceProvider;
        }

        public void onLocationChanged(Location location) {
            // Log.v("GeoBeagle", "GpsStatusWidget onLocationChanged " +
            // location);
            if (location == null)
                return;

            if (!mCombinedLocationManager.isProviderEnabled()) {
                mMeterWrapper.setDisabled();
                mTextLagUpdater.setDisabled();
                return;
            }
            mProvider.setText(location.getProvider());
            mMeterWrapper.setAccuracy(location.getAccuracy());
            mMeterFader.reset();
            mTextLagUpdater.reset(location.getTime());
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
    }

    public static class MeterFader {
        private long mLastUpdateTime;
        private final MeterView mMeterView;
        private final View mParent;
        private final Time mTime;

        MeterFader(View parent, MeterView meterView, Time time) {
            mLastUpdateTime = -1;
            mMeterView = meterView;
            mParent = parent;
            mTime = time;
        }

        void paint() {
            final long currentTime = mTime.getCurrentTime();
            if (mLastUpdateTime == -1)
                mLastUpdateTime = currentTime;
            long lastUpdateLag = currentTime - mLastUpdateTime;
            mMeterView.setLag(lastUpdateLag);
            if (lastUpdateLag < 1000)
                mParent.postInvalidateDelayed(100);
            // Log.v("GeoBeagle", "painting " + lastUpdateLag);
        }

        public void reset() {
            mLastUpdateTime = -1;
            mParent.postInvalidate();
        }
    }

    static class MeterWrapper {
        private float mAccuracy;
        private final TextView mAccuracyView;
        private float mAzimuth;
        private final MeterView mMeterView;

        public MeterWrapper(MeterView meterView, TextView accuracyView) {
            mAccuracyView = accuracyView;
            mMeterView = meterView;
        }

        public void setAccuracy(float accuracy) {
            mAccuracy = accuracy;
            mAccuracyView.setText((Integer.toString((int)accuracy) + "m").trim());
            mMeterView.set(accuracy, mAzimuth);
        }

        public void setAzimuth(float azimuth) {
            mAzimuth = azimuth;
            mMeterView.set(mAccuracy, azimuth);
        }

        public void setDisabled() {
            mAccuracyView.setText("");
            mMeterView.set(Float.MAX_VALUE, 0);
        }
    }

    public static class TextLagUpdater {
        private long mLastTextLagUpdateTime;
        private final TextView mTextLag;
        private final Time mTime;
        private final CombinedLocationManager mCombinedLocationManager;

        TextLagUpdater(CombinedLocationManager combinedLocationManager, TextView textLag, Time time) {
            mCombinedLocationManager = combinedLocationManager;
            mTextLag = textLag;
            mTime = time;
        }

        static String formatTime(long l) {
            if (l < 60)
                return String.valueOf(l) + "s";
            else if (l < 3600)
                return String.valueOf(l / 60) + "m " + String.valueOf(l % 60) + "s";
            return String.valueOf(l / 3600) + "h " + String.valueOf((l % 3600) / 60) + "m";
        }

        public CharSequence formatLag(long l) {
            return formatTime(l);
        }

        public void reset(long time) {
            mLastTextLagUpdateTime = time;
        }

        public void setDisabled() {
            mTextLag.setText("");
        }

        public void updateTextLag() {
            final long lag = mTime.getCurrentTime() - mLastTextLagUpdateTime;
            if (mCombinedLocationManager.isProviderEnabled())
                mTextLag.setText(formatLag(lag / 1000));
        }
    }

    public static class UpdateGpsWidgetRunnable implements Runnable {
        private final Handler mHandler;
        private final LocationControlBuffered mLocationControlBuffered;
        private final MeterWrapper mMeterWrapper;
        private final TextLagUpdater mTextLagUpdater;

        public UpdateGpsWidgetRunnable(Handler handler,
                LocationControlBuffered locationControlBuffered, MeterWrapper meterWrapper,
                TextLagUpdater textLagUpdater) {
            mMeterWrapper = meterWrapper;
            mLocationControlBuffered = locationControlBuffered;
            mTextLagUpdater = textLagUpdater;
            mHandler = handler;
        }

        public void run() {
            // Update the lag time and the orientation.
            mTextLagUpdater.updateTextLag();
            mMeterWrapper.setAzimuth(mLocationControlBuffered.getAzimuth());
            mHandler.postDelayed(this, 500);
        }
    }

    private final GpsStatusWidgetDelegate mGpsStatusWidgetDelegate;
    private final MeterWrapper mMeterWrapper;
    private final TextLagUpdater mTextLagUpdater;

    public GpsStatusWidget(Context context, LocationControlBuffered locationControlBuffered,
            CombinedLocationManager locationManager, MeterFormatter meterFormatter,
            ResourceProvider resourceProvider, Time time) {
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
        mMeterWrapper = new MeterWrapper(meterView, accuracyView);
        final MeterFader meterFader = new MeterFader(this, meterView, time);
        mTextLagUpdater = new TextLagUpdater(locationManager, lag, time);

        mGpsStatusWidgetDelegate = new GpsStatusWidgetDelegate(locationManager, meterFader,
                mMeterWrapper, provider, resourceProvider, status, mTextLagUpdater);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        mGpsStatusWidgetDelegate.mMeterFader.paint();
        // Log.v("GeoBeagle", "painting " + lastUpdateLag);
        // Log.v("GeoBeagle", "dispatch draw");
        super.dispatchDraw(canvas);
    }

    public GpsStatusWidgetDelegate getGpsStatusWidgetDelegate() {
        return mGpsStatusWidgetDelegate;
    }

    public MeterWrapper getMeterWrapper() {
        return mMeterWrapper;
    }

    public TextLagUpdater getTextLagUpdater() {
        return mTextLagUpdater;
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
