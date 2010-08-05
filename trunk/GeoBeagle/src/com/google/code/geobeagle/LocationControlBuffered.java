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

package com.google.code.geobeagle;

import com.google.code.geobeagle.activity.cachelist.model.GeocacheVector;
import com.google.code.geobeagle.activity.cachelist.presenter.DistanceSortStrategy;
import com.google.code.geobeagle.activity.cachelist.presenter.NullSortStrategy;
import com.google.code.geobeagle.activity.cachelist.presenter.SortStrategy;
import com.google.code.geobeagle.location.LocationControl;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class LocationControlBuffered implements LocationListener {
    public static class GpsDisabledLocation implements IGpsLocation {
        @Override
        public float distanceTo(IGpsLocation dest) {
            return Float.MAX_VALUE;
        }

        @Override
        public float distanceToGpsDisabledLocation(GpsDisabledLocation gpsLocation) {
            return Float.MAX_VALUE;
        }

        @Override
        public float distanceToGpsEnabledLocation(GpsEnabledLocation gpsEnabledLocation) {
            return Float.MAX_VALUE;
        }
    }

    public static class GpsEnabledLocation implements IGpsLocation {
        private final float mLatitude;
        private final float mLongitude;

        public GpsEnabledLocation(float latitude, float longitude) {
            mLatitude = latitude;
            mLongitude = longitude;
        }

        @Override
        public float distanceTo(IGpsLocation gpsLocation) {
            return gpsLocation.distanceToGpsEnabledLocation(this);
        }

        @Override
        public float distanceToGpsDisabledLocation(GpsDisabledLocation gpsLocation) {
            return Float.MAX_VALUE;
        }

        @Override
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

    public LocationControlBuffered(LocationControl locationControl,
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

    @Override
    public void onLocationChanged(Location location) {
        mLocation = mLocationControl.getLocation();
        if (location == null) {
            mGpsLocation = mGpsDisabledLocation;
        } else {
            mGpsLocation = new GpsEnabledLocation((float)location.getLatitude(), (float)location
                    .getLongitude());
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
    }

    public void setAzimuth(float azimuth) {
        mAzimuth = azimuth;
    }

    public float getAzimuth() {
        return mAzimuth;
    }
}
