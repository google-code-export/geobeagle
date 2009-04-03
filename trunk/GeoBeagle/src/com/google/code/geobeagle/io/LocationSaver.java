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

import com.google.code.geobeagle.data.Geocache;
import com.google.code.geobeagle.io.DatabaseDI.SQLiteWrapper;

public class LocationSaver {
    private final CacheWriter mCacheWriter;
    private final Database mDatabase;
    private final SQLiteWrapper mSQLiteWrapper;

    public LocationSaver(Database database, SQLiteWrapper sqliteWrapper, CacheWriter cacheWriter) {
        mDatabase = database;
        mSQLiteWrapper = sqliteWrapper;
        mCacheWriter = cacheWriter;
    }

    public void saveLocation(Geocache geocache) {
        mSQLiteWrapper.openWritableDatabase(mDatabase);
        // TODO: catch errors on open
        mCacheWriter.startWriting();
        mCacheWriter.insertAndUpdateCache(geocache.getId(), geocache.getName(), geocache
                .getLatitude(), geocache.getLongitude(), geocache.getSourceType(), geocache
                .getSourceName());
        mCacheWriter.stopWriting();
        mSQLiteWrapper.close();
    }

}
