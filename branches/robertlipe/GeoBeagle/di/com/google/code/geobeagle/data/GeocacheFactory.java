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

import com.google.code.geobeagle.data.Geocache;
import com.google.code.geobeagle.data.GeocacheFromParcelFactory;
import com.google.code.geobeagle.data.Geocache.Source;
import com.google.code.geobeagle.data.Geocache.Source.SourceFactory;

import android.os.Parcel;
import android.os.Parcelable;

public class GeocacheFactory {
    private static SourceFactory mSourceFactory;

    public GeocacheFactory() {
        mSourceFactory = new SourceFactory();
    }

    public static class CreateGeocacheFromParcel implements Parcelable.Creator<Geocache> {
        private final GeocacheFromParcelFactory mGeocacheFromParcelFactory = new GeocacheFromParcelFactory(
                new GeocacheFactory());

        public Geocache createFromParcel(Parcel in) {
            return mGeocacheFromParcelFactory.create(in);
        }

        public Geocache[] newArray(int size) {
            return new Geocache[size];
        }
    }

    public Geocache create(CharSequence id, CharSequence name, double latitude, double longitude,
            Source sourceType, String sourceName) {
        return new Geocache(id, name, latitude, longitude, sourceType, sourceName);
    }

    public Source sourceFromInt(int sourceIx) {
        return mSourceFactory.fromInt(sourceIx);
    }

}
