
package com.google.code.geobeagle.location;

import com.google.code.geobeagle.ui.cachelist.GeocacheVector;
import com.google.code.geobeagle.ui.cachelist.GeocacheVector.DistanceSortStrategy;
import com.google.code.geobeagle.ui.cachelist.GeocacheVector.NullSortStrategy;
import com.google.code.geobeagle.ui.cachelist.GeocacheVector.SortStrategy;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class LocationControlBuffered implements LocationListener {
    public static class GpsDisabledLocation implements IGpsLocation {
        public float distanceTo(IGpsLocation dest) {
            return Float.MAX_VALUE;
        }

        public float distanceToGpsDisabledLocation(GpsDisabledLocation gpsLocation) {
            return Float.MAX_VALUE;
        }

        public float distanceToGpsEnabledLocation(GpsEnabledLocation gpsEnabledLocation) {
            return Float.MAX_VALUE;
        }
    }

    public static class GpsEnabledLocation implements IGpsLocation {
        private final float mLatitude;
        private final float mLongitude;

        GpsEnabledLocation(float latitude, float longitude) {
            mLatitude = latitude;
            mLongitude = longitude;
        }

        public float distanceTo(IGpsLocation gpsLocation) {
            return gpsLocation.distanceToGpsEnabledLocation(this);
        }

        public float distanceToGpsDisabledLocation(GpsDisabledLocation gpsLocation) {
            return Float.MAX_VALUE;
        }

        public float distanceToGpsEnabledLocation(GpsEnabledLocation gpsEnabledLocation) {
            final float calculateDistanceFast = GeocacheVector.calculateDistanceFast(mLatitude,
                    mLongitude, gpsEnabledLocation.mLatitude, gpsEnabledLocation.mLongitude);
            return calculateDistanceFast;
        }
    }

    public static interface IGpsLocation {
        public float distanceTo(IGpsLocation dest);

        float distanceToGpsDisabledLocation(GpsDisabledLocation gpsLocation);

        float distanceToGpsEnabledLocation(GpsEnabledLocation gpsEnabledLocation);
    }

    private final DistanceSortStrategy mDistanceSortStrategy;
    private GpsDisabledLocation mGpsDisabledLocation;
    private IGpsLocation mGpsLocation;
    private Location mLocation;
    private LocationControl mLocationControl;
    private final NullSortStrategy mNullSortStrategy;
    private float mAzimuth;

    LocationControlBuffered(LocationControl locationControl,
            DistanceSortStrategy distanceSortStrategy, NullSortStrategy nullSortStrategy,
            GpsDisabledLocation gpsDisabledLocation, IGpsLocation lastGpsLocation,
            Location lastLocation) {
        mLocationControl = locationControl;
        mDistanceSortStrategy = distanceSortStrategy;
        mNullSortStrategy = nullSortStrategy;
        mGpsDisabledLocation = gpsDisabledLocation;
        mGpsLocation = lastGpsLocation;
        mLocation = lastLocation;
    }

    public IGpsLocation getGpsLocation() {
        return mGpsLocation;
    }

    public Location getLocation() {
        return mLocation;
    }

    public SortStrategy getSortStrategy() {
        if (mLocation == null)
            return mNullSortStrategy;
        return mDistanceSortStrategy;
    }

    public void onLocationChanged(Location location) {
        mLocation = mLocationControl.getLocation();
        if (location == null) {
            mGpsLocation = mGpsDisabledLocation;
        } else {
            mGpsLocation = new GpsEnabledLocation((float)location.getLatitude(), (float)location
                    .getLongitude());
        }
    }

    public void onProviderDisabled(String provider) {
    }

    public void onProviderEnabled(String provider) {
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    public void setAzimuth(float azimuth) {
        mAzimuth = azimuth;
    }

    public float getAzimuth() {
        return mAzimuth;
    }
}
