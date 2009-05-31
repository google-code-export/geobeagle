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

package com.google.code.geobeagle.xmlimport;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.xmlimport.CacheReader.CacheReaderCursor;

import android.location.Location;

import java.util.ArrayList;

public class GeocachesSql {
    private final CacheReader mCacheReader;
    private final Geocaches mGeocaches;

    GeocachesSql(CacheReader cacheReader, Geocaches geocaches) {
        mCacheReader = cacheReader;
        mGeocaches = geocaches;
    }

    public int getCount() {
        return mCacheReader.getTotalCount();
    }

    public ArrayList<Geocache> getGeocaches() {
        return mGeocaches.getAll();
    }

    public void loadCaches(Location location, WhereFactory whereFactory) {
        CacheReaderCursor cursor = mCacheReader.open(location, whereFactory);
        mGeocaches.clear();
        if (cursor != null) {
            read(cursor);
            cursor.close();
        }
    }

    public void read(CacheReaderCursor cursor) {
        do {
            mGeocaches.add(cursor.getCache());
        } while (cursor.moveToNext());
    }

}
