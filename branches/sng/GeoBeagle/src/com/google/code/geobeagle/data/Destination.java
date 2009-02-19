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

package com.google.code.geobeagle.data;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.ResourceProvider;
import com.google.code.geobeagle.Util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Cache or letterbox description, id, and coordinates.
 */
public class Destination {

    public static class DestinationFactory {
        private final Pattern[] mDestinationPatterns;

        public DestinationFactory(Pattern destinationPatterns[]) {
            mDestinationPatterns = destinationPatterns;
        }

        public Destination create(CharSequence location) {
            return new Destination(location, mDestinationPatterns);
        }
    }

    public static CharSequence extractDescription(CharSequence location) {
        return Util.splitCoordsAndDescription(location)[1];
    }

    public static Pattern[] getDestinationPatterns(ResourceProvider resourceProvider) {
        return getDestinationPatterns(resourceProvider.getStringArray(R.array.content_prefixes));
    }

    static Pattern[] getDestinationPatterns(String contentPrefixes[]) {
        Pattern mContentSelectors[] = new Pattern[contentPrefixes.length];
        for (int ix = 0; ix < contentPrefixes.length; ix++) {
            mContentSelectors[ix] = Pattern.compile("(?:" + contentPrefixes[ix] + ")(\\w*)");
        }
        return mContentSelectors;
    }

    private int mContentSelectorIndex;
    private CharSequence mDescription;
    private CharSequence mLocation;
    private CharSequence mId;
    private double mLatitude;
    private double mLongitude;

    public Destination(CharSequence location, Pattern destinationPatterns[]) {
        CharSequence latLonDescription[] = Util.splitLatLonDescription(location);
        try {
            mLatitude = Util.parseCoordinate(latLonDescription[0]);
            mLongitude = Util.parseCoordinate(latLonDescription[1]);
        } catch (NumberFormatException numberFormatException) {
        }
        mDescription = latLonDescription[2];
        mLocation = location;

        for (mContentSelectorIndex = destinationPatterns.length - 1; mContentSelectorIndex >= 0; mContentSelectorIndex--) {
            Matcher matcher = destinationPatterns[mContentSelectorIndex].matcher(mDescription);
            if (matcher.find()) {
                mId = matcher.group(1);
                return;
            }
        }
    }

    public int getContentIndex() {
        return mContentSelectorIndex;
    }

    public CharSequence getLocation() {
        return mLocation;
    }
    public CharSequence getDescription() {
        return mDescription;
    }

    public CharSequence getId() {
        return mId;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }
}
