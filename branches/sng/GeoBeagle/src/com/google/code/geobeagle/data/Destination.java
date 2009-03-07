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
 * Cache or letterbox description, id, and coordinates.
 */
public class Destination {
    private int mContentSelectorIndex;
    private CharSequence mDescription;
    private CharSequence mFullId;
    private CharSequence mId;
    private double mLatitude;
    private CharSequence mLocation;
    private double mLongitude;
    private CharSequence mName;

    public Destination(CharSequence location, Pattern destinationPatterns[]) {
        mId = "";
        mFullId = "";
        mName = "";
        CharSequence latLonDescription[] = Util.splitLatLonDescription(location);
        try {
            mLatitude = Util.parseCoordinate(latLonDescription[0]);
            mLongitude = Util.parseCoordinate(latLonDescription[1]);
        } catch (NumberFormatException numberFormatException) {
            // TODO: Looks like this case is unreachable; remove this after the
            // destination input method has been reworked.
        }
        mDescription = latLonDescription[2];
        mLocation = location;

        for (mContentSelectorIndex = destinationPatterns.length - 1; mContentSelectorIndex >= 0; mContentSelectorIndex--) {
            Matcher matcher = destinationPatterns[mContentSelectorIndex].matcher(mDescription);
            if (matcher.find()) {
                mId = matcher.group(1);
                mFullId = matcher.group();
                break;
            }
        }

        if (mId.length() == 0) {
            mName = mDescription;
        } else {
            if (mDescription.length() > mFullId.length() + 2)
                mName = mDescription.subSequence(mFullId.length() + 2, mDescription.length());
        }
    }

    public int getContentIndex() {
        return mContentSelectorIndex;
    }

    public CharSequence getDescription() {
        return mDescription;
    }

    public CharSequence getFullId() {
        return mFullId;
    }

    public CharSequence getId() {
        return mId;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public CharSequence getLocation() {
        return mLocation;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public CharSequence getName() {
        return mName;
    }
}
