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

package com.google.code.geobeagle.activity.cachelist.model;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.LocationControlBuffered;
import com.google.code.geobeagle.activity.cachelist.presenter.BearingFormatter;
import com.google.code.geobeagle.formatting.DistanceFormatter;

import android.location.Location;
import android.util.FloatMath;

import java.util.Comparator;

public class GeocacheVector {
    public static class LocationComparator implements Comparator<GeocacheVector> {
        public int compare(GeocacheVector geocacheVector1, GeocacheVector geocacheVector2) {
            final float d1 = geocacheVector1.getDistance();
            final float d2 = geocacheVector2.getDistance();
            if (d1 < d2)
                return -1;
            if (d1 > d2)
                return 1;
            return 0;
        }
    }

    // From http://www.anddev.org/viewtopic.php?p=20195.
    public static float calculateDistanceFast(float lat1, float lon1, float lat2, float lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        final float sinDLat = FloatMath.sin((float)(dLat / 2));
        final float sinDLon = FloatMath.sin((float)(dLon / 2));
        float a = sinDLat * sinDLat + FloatMath.cos((float)Math.toRadians(lat1))
                * FloatMath.cos((float)Math.toRadians(lat2)) * sinDLon * sinDLon;
        float c = (float)(2 * Math.atan2(FloatMath.sqrt(a), FloatMath.sqrt(1 - a)));
        return 6371000 * c;
    }

    // TODO: distance formatter shouldn't be in every object.
    private final Geocache mGeocache;
    private float mDistance;
    private final LocationControlBuffered mLocationControlBuffered;

    float getDistance() {
        return mDistance;
    }

    public void setDistance(float f) {
        mDistance = f;
    }

    public GeocacheVector(Geocache geocache, LocationControlBuffered locationControlBuffered) {
        mGeocache = geocache;
        mLocationControlBuffered = locationControlBuffered;
    }

    public float getDistanceFast() {
        Location here = mLocationControlBuffered.getLocation();
        return calculateDistanceFast((float)here.getLatitude(), (float)here.getLongitude(),
                (float)mGeocache.getLatitude(), (float)mGeocache.getLongitude());
    }

    public CharSequence getFormattedDistance(DistanceFormatter distanceFormatter,
            BearingFormatter relativeBearingFormatter) {
        // Use the slower, more accurate distance for display.
        final float[] distanceAndBearing = mGeocache
                .calculateDistanceAndBearing(mLocationControlBuffered.getLocation());
        if (distanceAndBearing[0] == -1) {
            return "";
        }
        final float azimuth = mLocationControlBuffered.getAzimuth();

        final CharSequence formattedDistance = distanceFormatter
                .formatDistance(distanceAndBearing[0]);
        final String formattedBearing = relativeBearingFormatter.formatBearing(distanceAndBearing[1],
                azimuth);
        return formattedDistance + " " + formattedBearing;
    }

    public Geocache getGeocache() {
        return mGeocache;
    }

    public CharSequence getId() {
        return mGeocache.getId();
    }

    public CharSequence getIdAndName() {
        return mGeocache.getIdAndName();
    }

}
