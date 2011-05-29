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

import com.google.code.geobeagle.CacheType;
import com.google.code.geobeagle.CacheTypeFactory;
import com.google.code.geobeagle.GeocacheFactory.Source;
import com.google.code.geobeagle.database.CacheSqlWriter;
import com.google.code.geobeagle.database.ClearCachesFromSource;
import com.google.code.geobeagle.database.GpxTableWriter;
import com.google.code.geobeagle.database.GpxTableWriterGpxFiles;
import com.google.code.geobeagle.database.Tag;
import com.google.code.geobeagle.database.TagWriter;
import com.google.inject.Inject;

import android.util.Log;

/**
 * @author sng
 */
public class CacheTagSqlWriter {
    private final CacheTypeFactory mCacheTypeFactory;
    private CacheType mCacheType;
    private final CacheSqlWriter mCacheWriter;
    private final GpxTableWriterGpxFiles mGpxWriter;
    private int mContainer;
    private int mDifficulty;
    private String mGpxName;
    private CharSequence mId;
    private double mLatitude;
    private double mLongitude;
    private CharSequence mName;
    private int mTerrain;
    private final TagWriter mTagWriter;
    private boolean mArchived;
    private boolean mAvailable;
    private boolean mFound;

    @Inject
    public CacheTagSqlWriter(CacheSqlWriter cacheSqlWriter,
            GpxTableWriterGpxFiles gpxTableWriterGpxFiles,
            CacheTypeFactory cacheTypeFactory,
            TagWriter tagWriter) {
        mCacheWriter = cacheSqlWriter;
        mGpxWriter = gpxTableWriterGpxFiles;
        mCacheTypeFactory = cacheTypeFactory;
        mTagWriter = tagWriter;
    }

    public void cacheName(String name) {
        mName = name;
    }

    public void cacheType(String type) {
        mCacheType = mCacheTypeFactory.fromTag(type);
    }

    public void clear() { // TODO: ensure source is not reset
        mId = mName = null;
        mLatitude = mLongitude = 0;
        mCacheType = CacheType.NULL;
        mDifficulty = 0;
        mTerrain = 0;
        mContainer = 0;
        mArchived = false;
        mAvailable = true;
        mFound = false;
    }

    public void container(String container) {
        mContainer = mCacheTypeFactory.container(container);
    }

    public void difficulty(String difficulty) {
        mDifficulty = mCacheTypeFactory.stars(difficulty);
    }

    public void end(ClearCachesFromSource clearCachesFromSource) {
        clearCachesFromSource.clearEarlierLoads();
    }

    public void gpxName(String gpxName) {
        mGpxName = gpxName;
        Log.d("GeoBeagle", this + ": CacheTagSqlWriter:gpxName: " + mGpxName);
    }

    /**
     * @return true if we should load this gpx; false if the gpx is already
     *         loaded.
     */
    public boolean gpxTime(ClearCachesFromSource clearCachesFromSource,
            GpxTableWriter gpxTableWriter,
            String gpxTime) {
        String sqlDate = isoTimeToSql(gpxTime);
        Log.d("GeoBeagle", this + ": CacheTagSqlWriter:gpxTime: " + mGpxName);
        if (gpxTableWriter.isGpxAlreadyLoaded(mGpxName, sqlDate)) {
            return false;
        }
        clearCachesFromSource.clearCaches(mGpxName);
        return true;
    }

    public void id(CharSequence id) {
        mId = id;
    }

    public String isoTimeToSql(String gpxTime) {
        return gpxTime.substring(0, 10) + " " + gpxTime.substring(11, 19);
    }

    public void latitudeLongitude(String latitude, String longitude) {
        mLatitude = Double.parseDouble(latitude);
        mLongitude = Double.parseDouble(longitude);
    }

    public void startWriting() {
        mCacheWriter.startWriting();
    }

    public void stopWriting(boolean successfulGpxImport) {
        mCacheWriter.stopWriting();
        if (successfulGpxImport)
            mGpxWriter.writeGpx(mGpxName);
    }

    public void symbol(String symbol) {
        if (symbol.equals("Geocache Found"))
            mFound = true;
    }

    public void terrain(String terrain) {
        mTerrain = mCacheTypeFactory.stars(terrain);
    }

    public void write(Source source) {
        mCacheWriter.insertAndUpdateCache(mId, mName, mLatitude, mLongitude, source, mGpxName,
                mCacheType, mDifficulty, mTerrain, mContainer, mAvailable, mArchived, mFound);
        if (mFound)
            mTagWriter.add(mId, Tag.FOUND, false);
    }

    public void archived(boolean fArchived) {
        mArchived = fArchived;
    }

    public void available(boolean fAvailable) {
        mAvailable = fAvailable;
    }
}
