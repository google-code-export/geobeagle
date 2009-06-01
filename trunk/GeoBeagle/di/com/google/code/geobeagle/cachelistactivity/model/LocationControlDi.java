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

package com.google.code.geobeagle.cachelistactivity.model;

import com.google.code.geobeagle.cachelistactivity.model.LocationControlBuffered;
import com.google.code.geobeagle.cachelistactivity.model.GeocacheVector.LocationComparator;
import com.google.code.geobeagle.cachelistactivity.model.LocationControlBuffered.GpsDisabledLocation;
import com.google.code.geobeagle.cachelistactivity.model.LocationControlBuffered.GpsEnabledLocation;
import com.google.code.geobeagle.cachelistactivity.model.LocationControlBuffered.IGpsLocation;
import com.google.code.geobeagle.cachelistactivity.presenter.DistanceSortStrategy;
import com.google.code.geobeagle.cachelistactivity.presenter.NullSortStrategy;
import com.google.code.geobeagle.location.LocationControl;
import com.google.code.geobeagle.location.LocationControl.LocationChooser;

import android.location.Location;
import android.location.LocationManager;

public class LocationControlDi {
    public static LocationControlBuffered create(LocationManager locationManager) {
        final LocationChooser locationChooser = new LocationChooser();
        final LocationControl locationControl = new LocationControl(locationManager,
                locationChooser);
        final NullSortStrategy nullSortStrategy = new NullSortStrategy();
        final LocationComparator locationComparator = new LocationComparator();
        final DistanceSortStrategy distanceSortStrategy = new DistanceSortStrategy(
                locationComparator);
        final GpsDisabledLocation gpsDisabledLocation = new GpsDisabledLocation();
        IGpsLocation lastGpsLocation;
        final Location lastKnownLocation = locationManager.getLastKnownLocation("gps");
        if (lastKnownLocation == null)
            lastGpsLocation = gpsDisabledLocation;
        else
            lastGpsLocation = new GpsEnabledLocation((float)lastKnownLocation.getLatitude(),
                    (float)lastKnownLocation.getLongitude());
        return new LocationControlBuffered(locationControl, distanceSortStrategy, nullSortStrategy,
                gpsDisabledLocation, lastGpsLocation, lastKnownLocation);
    }
}
