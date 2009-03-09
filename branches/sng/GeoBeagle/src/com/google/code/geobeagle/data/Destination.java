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

import com.google.code.geobeagle.Util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Destination or letterbox description, id, and coordinates.
 */
public class Destination {
    public static Destination create(CharSequence location, Pattern destinationPatterns[]) {
        int contentSelectorIndex;
        double latitude = 0;
        double longitude = 0;

        CharSequence fullId = "";
        CharSequence name = "";
        CharSequence latLonDescription[] = Util.splitLatLonDescription(location);
        try {
            latitude = Util.parseCoordinate(latLonDescription[0]);
            longitude = Util.parseCoordinate(latLonDescription[1]);
        } catch (NumberFormatException numberFormatException) {
            // TODO: Looks like this case is unreachable; remove this after the
            // destination input method has been reworked.
        }

        CharSequence description = latLonDescription[2];

        for (contentSelectorIndex = destinationPatterns.length - 1; contentSelectorIndex >= 0; contentSelectorIndex--) {
            Matcher matcher = destinationPatterns[contentSelectorIndex].matcher(description);
            if (matcher.find()) {
                fullId = matcher.group();
                break;
            }
        }

        if (fullId.length() == 0) {
            name = description;
        } else {
            if (description.length() > fullId.length() + 2)
                name = description.subSequence(fullId.length() + 2, description.length());
        }
        return new Destination(contentSelectorIndex, fullId, name, latitude, longitude);

    }

    private final int mContentSelectorIndex;
    private final CharSequence mId;
    private final double mLatitude;
    private final double mLongitude;
    private final CharSequence mName;

    public Destination(int contentSelectorIndex, CharSequence id, CharSequence name,
            double latitude, double longitude) {
        mContentSelectorIndex = contentSelectorIndex;
        mId = id;
        mName = name;
        mLatitude = latitude;
        mLongitude = longitude;
    }

    public int getContentIndex() {
        return mContentSelectorIndex;
    }

    public CharSequence getCoordinatesIdAndName() {
        return mLatitude + ", " + mLongitude + " (" + getIdAndName() + ")";
    }

    public CharSequence getId() {
        return mId;
    }

    public CharSequence getIdAndName() {
        if (mId.length() == 0)
            return mName;
        else if (mName.length() == 0)
            return mId;
        else
            return mId + ": " + mName;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public CharSequence getName() {
        return mName;
    }

    public CharSequence getShortId() {
        if (mId.length() > 2)
            return mId.subSequence(2, mId.length());
        else
            return "";
    }
}
