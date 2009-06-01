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

package com.google.code.geobeagle.mainactivity;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.GeocacheFactory;

import android.os.Bundle;
import android.os.Parcel;

public class GeocacheFromParcelFactory {
    private final GeocacheFactory mGeocacheFactory;

    public GeocacheFromParcelFactory(GeocacheFactory geocacheFactory) {
        mGeocacheFactory = geocacheFactory;
    }

    public Geocache create(Parcel in) {
        return createFromBundle(in.readBundle());

    }

    public Geocache createFromBundle(Bundle bundle) {
        return mGeocacheFactory.create(bundle.getCharSequence("id"),
                bundle.getCharSequence("name"), bundle.getDouble("latitude"), bundle
                        .getDouble("longitude"), mGeocacheFactory.sourceFromInt(bundle
                        .getInt("sourceType")), bundle.getString("sourceName"));
    }
}
