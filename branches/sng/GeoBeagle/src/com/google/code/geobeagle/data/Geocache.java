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

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Geocache or letterbox description, id, and coordinates.
 */
public class Geocache implements Parcelable {
    public static final Parcelable.Creator<Geocache> CREATOR = new Parcelable.Creator<Geocache>() {
        public Geocache createFromParcel(Parcel in) {
            return new Geocache(in);
        }

        public Geocache[] newArray(int size) {
            return new Geocache[size];
        }
    };

    public final static int PROVIDER_MYLOCATION = -1;
    public final static int PROVIDER_ATLASQUEST = 0;
    public final static int PROVIDER_GROUNDSPEAK = 1;

    private final int mContentSelectorIndex;
    private final CharSequence mId;
    private final double mLatitude;
    private final double mLongitude;
    private final CharSequence mName;

    // Use Groundspeak if no provider specified.
    public Geocache(CharSequence id, CharSequence name, double latitude, double longitude) {
        this(PROVIDER_GROUNDSPEAK, id, name, latitude, longitude);
    }

    public Geocache(int contentSelectorIndex, CharSequence id, CharSequence name, double latitude,
            double longitude) {
        mContentSelectorIndex = contentSelectorIndex;
        mId = id;
        mName = name;
        mLatitude = latitude;
        mLongitude = longitude;
    }

    public Geocache(Parcel in) {
        Bundle bundle = in.readBundle();
        mContentSelectorIndex = bundle.getInt("contentSelectorIndex");
        mId = bundle.getCharSequence("id");
        mLatitude = bundle.getDouble("latitude");
        mLongitude = bundle.getDouble("longitude");
        mName = bundle.getCharSequence("name");
    }

    public int describeContents() {
        return 0;
    }

    public int getContentIndex() {
        return mContentSelectorIndex;
    }

    public CharSequence getCoordinatesIdAndName() {
        return Util.formatDegreesAsDecimalDegreesString(mLatitude) + ", "
                + Util.formatDegreesAsDecimalDegreesString(mLongitude) + " (" + getIdAndName()
                + ")";
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

    public void writeToParcel(Parcel out, int flags) {
        Bundle bundle = new Bundle();
        bundle.putInt("contentSelectorIndex", mContentSelectorIndex);
        bundle.putCharSequence("id", mId);
        bundle.putCharSequence("name", mName);
        bundle.putDouble("latitude", mLatitude);
        bundle.putDouble("longitude", mLongitude);
        out.writeBundle(bundle);
    }
}
