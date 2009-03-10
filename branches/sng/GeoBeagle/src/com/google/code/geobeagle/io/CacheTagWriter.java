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

package com.google.code.geobeagle.io;

import com.google.code.geobeagle.io.Database.CacheWriter;

public class CacheTagWriter {
    public final CacheWriter mCacheWriter;
    private boolean mFound;
    private CharSequence mId;
    private double mLatitude;
    private double mLongitude;
    private CharSequence mName;
    private String mSource;

    public CacheTagWriter(CacheWriter cacheWriter) {
        mCacheWriter = cacheWriter;
    }

    public void clear() {
        mId = mName = mSource = null;
        mLatitude = mLongitude = 0;
        mFound = false;
    }

    public void clearAllImportedCaches() {
        mCacheWriter.clearAllImportedCaches();
    }

    public void id(CharSequence id) {
        mId = id;
    }

    public void latitudeLongitude(String latitude, String longitude) {
        mLatitude = Double.parseDouble(latitude);
        mLongitude = Double.parseDouble(longitude);
    }

    public void name(String name) {
        mName = name;
    }

    public void source(String source) {
        mSource = source;
    }

    public void startWriting() {
        mCacheWriter.startWriting();
    }

    public void stopWriting() {
        mCacheWriter.stopWriting();
    }

    public void symbol(String symbol) {
        mFound = symbol.equals("Geocache Found");
    }

    public void write() {
        if (!mFound)
            mCacheWriter.insertAndUpdateCache(mId, mName, mLatitude, mLongitude, mSource);
    }
}
