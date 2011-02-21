package com.google.code.geobeagle.activity.main;

import com.google.code.geobeagle.R;
import com.google.inject.Inject;
import com.google.inject.Provider;

import android.app.Activity;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.LocationManager;
import android.util.Log;
import android.widget.TextView;

class SatelliteCountListener implements GpsStatus.Listener {
    private final Provider<LocationManager> locationManagerProvider;
    private final Provider<Activity> activityProvider;

    @Inject
    SatelliteCountListener(Provider<Activity> activityProvider,
            Provider<LocationManager> locationManagerProvider) {
        this.locationManagerProvider = locationManagerProvider;
        this.activityProvider = activityProvider;
    }

    @Override
    public void onGpsStatusChanged(int event) {
        Log.d("GeoBeagle", "SSSSSSSTATUS CHANGED");
        GpsStatus gpsStatus = locationManagerProvider.get().getGpsStatus(null);
        int satelliteCount = 0;
        for (@SuppressWarnings("unused") GpsSatellite gpsSatellite : gpsStatus.getSatellites()) {
            satelliteCount++;
        }
        ((TextView)activityProvider.get().findViewById(R.id.satellite_count))
                .setText(satelliteCount + " SATs");
    }

}