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

import com.google.code.geobeagle.LocationControlBuffered.GpsDisabledLocation;
import com.google.code.geobeagle.LocationControlBuffered.GpsEnabledLocation;
import com.google.code.geobeagle.LocationControlBuffered.IGpsLocation;
import com.google.code.geobeagle.activity.cachelist.presenter.DistanceSortStrategy;
import com.google.code.geobeagle.activity.cachelist.presenter.NullSortStrategy;
import com.google.code.geobeagle.location.LocationControl;
import com.google.inject.Inject;
import com.google.inject.Provider;

import android.location.Location;
import android.location.LocationManager;

public class LocationControlBufferedProvider implements Provider<LocationControlBuffered> {
    private final LocationManager locationManager;
    private final LocationControl locationControl;
    private final NullSortStrategy nullSortStrategy;
    private final DistanceSortStrategy distanceSortStrategy;
    private final GpsDisabledLocation gpsDisabledLocation;

    @Inject
    public LocationControlBufferedProvider(LocationManager locationManager,
            LocationControl locationControl, NullSortStrategy nullSortStrategy,
            DistanceSortStrategy distanceSortStrategy, GpsDisabledLocation gpsDisabledLocation) {
        this.locationManager = locationManager;
        this.locationControl = locationControl;
        this.nullSortStrategy = nullSortStrategy;
        this.distanceSortStrategy = distanceSortStrategy;
        this.gpsDisabledLocation = gpsDisabledLocation;
    }

    @Override
    public LocationControlBuffered get() {

        IGpsLocation lastGpsLocation;
        final Location lastKnownLocation = locationManager.getLastKnownLocation("gps");
        if (lastKnownLocation == null)
            lastGpsLocation = gpsDisabledLocation;
        else
            lastGpsLocation = new GpsEnabledLocation((float)lastKnownLocation.getLatitude(),
                    (float)lastKnownLocation.getLongitude());
        return new LocationControlBuffered(locationControl, distanceSortStrategy, nullSortStrategy,
                gpsDisabledLocation, lastGpsLocation, lastKnownLocation);        }
    
}
