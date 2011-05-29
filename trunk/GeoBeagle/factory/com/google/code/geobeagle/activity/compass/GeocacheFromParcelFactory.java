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

package com.google.code.geobeagle.activity.compass;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.GeocacheFactory;
import com.google.inject.Inject;

import android.os.Bundle;
import android.os.Parcel;

public class GeocacheFromParcelFactory {
    private final GeocacheFactory mGeocacheFactory;

    @Inject
    public GeocacheFromParcelFactory(GeocacheFactory geocacheFactory) {
        mGeocacheFactory = geocacheFactory;
    }

    public Geocache create(Parcel in) {
        return createFromBundle(in.readBundle());

    }

    public Geocache createFromBundle(Bundle bundle) {
        return mGeocacheFactory.create(bundle.getCharSequence(Geocache.ID), bundle
                .getCharSequence(Geocache.NAME), bundle.getDouble(Geocache.LATITUDE), bundle
                .getDouble(Geocache.LONGITUDE), mGeocacheFactory.sourceFromInt(bundle
                .getInt(Geocache.SOURCE_TYPE)), bundle.getString(Geocache.SOURCE_NAME),
                mGeocacheFactory.cacheTypeFromInt(bundle.getInt(Geocache.CACHE_TYPE)), bundle
                        .getInt(Geocache.DIFFICULTY), bundle.getInt(Geocache.TERRAIN), bundle
                        .getInt(Geocache.CONTAINER), bundle.getBoolean(Geocache.AVAILABLE), bundle
                        .getBoolean(Geocache.ARCHIVED));
    }
}
