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

package com.google.code.geobeagle.database;

import com.google.code.geobeagle.CacheType;
import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.GeocacheFactory;
import com.google.code.geobeagle.Tags;
import com.google.code.geobeagle.GeocacheFactory.Source;
import com.google.code.geobeagle.activity.main.Util;

/**
 * @author sng
 */
public class CacheWriter {
    public static final String SQLS_CLEAR_EARLIER_LOADS[] = {
            Database.SQL_DELETE_OLD_CACHES, Database.SQL_DELETE_OLD_GPX,
            Database.SQL_RESET_DELETE_ME_CACHES, Database.SQL_RESET_DELETE_ME_GPX
    };
    private final SourceNameTranslator mDbToGeocacheAdapter;
    private final ISQLiteDatabase mSqlite;
    private final GeocacheFactory mGeocacheFactory;
    private final DbFrontend mDbFrontend;

    CacheWriter(ISQLiteDatabase sqlite, DbFrontend dbFrontend,
            SourceNameTranslator dbToGeocacheAdapter, GeocacheFactory geocacheFactory) {
        mSqlite = sqlite;
        mDbToGeocacheAdapter = dbToGeocacheAdapter;
        mGeocacheFactory = geocacheFactory;
        mDbFrontend = dbFrontend;
    }

    public void clearCaches(String source) {
        mGeocacheFactory.flushCache();
        mDbFrontend.flushTotalCount();
        mSqlite.execSQL(Database.SQL_CLEAR_CACHES, source);
    }

    /**
     * Deletes any cache/gpx entries marked delete_me, then marks all remaining
     * gpx-based caches, and gpx entries with delete_me = 1.
     */
    public void clearEarlierLoads() {
        for (String sql : CacheWriter.SQLS_CLEAR_EARLIER_LOADS) {
            mSqlite.execSQL(sql);
        }
    }

    public void deleteCache(CharSequence id) {
        mGeocacheFactory.flushGeocache(id);
        mDbFrontend.flushTotalCount();
        mSqlite.execSQL(Database.SQL_DELETE_CACHE, id);
    }

    /** Checks if the geocache is different from the previously existing one.
     * @return True if the geocache was updated in the database.
     */
    public boolean conditionallyWriteCache(CharSequence id, CharSequence name, double latitude,
            double longitude, Source sourceType, String sourceName, CacheType cacheType,
            int difficulty, int terrain, int container) {
        Geocache geocache = mDbFrontend.loadCacheFromId(id.toString());
        if (geocache != null
                && geocache.getName().equals(name)
                && Util.approxEquals(geocache.getLatitude(), latitude)
                && Util.approxEquals(geocache.getLongitude(), longitude)
                && geocache.getSourceType().equals(sourceType)
                && geocache.getSourceName().equals(sourceName)
                && geocache.getCacheType() == cacheType
                && geocache.getDifficulty() == difficulty
                && geocache.getTerrain() == terrain
                && geocache.getContainer() == container)
            return false;

        /*
        if (geocache != null) {
            if (!geocache.getName().equals(name))
                Log.d("GeoBeagle", "CacheWriter updating "+id+" because of name");
            if (!Util.approxEquals(geocache.getLatitude(), latitude))
                Log.d("GeoBeagle", "CacheWriter updating "+id+" because of lat");
            if (!Util.approxEquals(geocache.getLongitude(), longitude))
                Log.d("GeoBeagle", "CacheWriter updating "+id+" because of long");
            if (!geocache.getSourceType().equals(sourceType))
                Log.d("GeoBeagle", "CacheWriter updating "+id+" because of sourceType");
            if (!geocache.getSourceName().equals(sourceName))
                Log.d("GeoBeagle", "CacheWriter updating "+id+" because of sourceName");
            if (geocache.getCacheType() != cacheType)
                Log.d("GeoBeagle", "CacheWriter updating "+id+" because of type");
            if (geocache.getDifficulty() != difficulty)
                Log.d("GeoBeagle", "CacheWriter updating "+id+" because of diff");
            if (geocache.getTerrain() != terrain)
                Log.d("GeoBeagle", "CacheWriter updating "+id+" because of terrain");
            if (geocache.getContainer() != container)
                Log.d("GeoBeagle", "CacheWriter updating "+id+" because of container");
        } else {
            Log.d("GeoBeagle", "CacheWriter creating cache "+id);
        }
         */
        
        insertAndUpdateCache(id, name, latitude, longitude, sourceType, 
                sourceName, cacheType, difficulty, terrain, container);
        return true;
    }
    
    /** Unconditionally update the geocache in the database */
    public void insertAndUpdateCache(CharSequence id, CharSequence name, double latitude,
            double longitude, Source sourceType, String sourceName, CacheType cacheType,
            int difficulty, int terrain, int container) {
        mGeocacheFactory.flushGeocache(id);
        mSqlite.execSQL(Database.SQL_REPLACE_CACHE, id, name, new Double(latitude), new Double(
                longitude), mDbToGeocacheAdapter.sourceTypeToSourceName(sourceType, sourceName),
                cacheType.toInt(), difficulty, terrain, container);
    }

    public void updateTag(CharSequence id, int tag, boolean set) {
        mDbFrontend.setGeocacheTag(id, tag, set);
    }

    public boolean isLockedFromUpdating(CharSequence id) {
        return mDbFrontend.geocacheHasTag(id, Tags.LOCKED_FROM_OVERWRITING);
    }
    
    /**
     * Return True if the gpx is already loaded. Mark this gpx and its caches in
     * the database to protect them from being nuked when the load is complete.
     * 
     * @param gpxName
     * @param gpxTime
     * @return
     */
    public boolean isGpxAlreadyLoaded(String gpxName, String gpxTime) {
        // TODO:countResults is slow; replace with a query, and moveToFirst.
        boolean gpxAlreadyLoaded = mSqlite.countResults(Database.TBL_GPX,
                Database.SQL_MATCH_NAME_AND_EXPORTED_LATER, gpxName, gpxTime) > 0;
        if (gpxAlreadyLoaded) {
            mSqlite.execSQL(Database.SQL_CACHES_DONT_DELETE_ME, gpxName);
            mSqlite.execSQL(Database.SQL_GPX_DONT_DELETE_ME, gpxName);
        }
        return gpxAlreadyLoaded;
    }

    public void startWriting() {
        mSqlite.beginTransaction();
    }

    public void stopWriting() {
        // TODO: abort if no writes--otherwise sqlite is unhappy.
        mSqlite.setTransactionSuccessful();
        mSqlite.endTransaction();
    }

    public void writeGpx(String gpxName, String pocketQueryExportTime) {
        mSqlite.execSQL(Database.SQL_REPLACE_GPX, gpxName, pocketQueryExportTime);
    }
}
