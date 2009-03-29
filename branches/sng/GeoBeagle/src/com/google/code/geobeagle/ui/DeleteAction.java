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

package com.google.code.geobeagle.ui;

import com.google.code.geobeagle.data.GeocacheVectors;
import com.google.code.geobeagle.io.CacheWriter;
import com.google.code.geobeagle.io.Database;
import com.google.code.geobeagle.io.DatabaseDI.SQLiteWrapper;

public class DeleteAction implements Action {
    private final CacheWriter mCacheWriter;
    private final Database mDatabase;
    private final SQLiteWrapper mSQLiteWrapper;
    private final GeocacheVectors mGeocacheVectors;

    DeleteAction(Database database, SQLiteWrapper sqliteWrapper, CacheWriter cacheWriter,
            GeocacheVectors geocacheVectors, ErrorDisplayer errorDisplayer) {
        mGeocacheVectors = geocacheVectors;
        mDatabase = database;
        mSQLiteWrapper = sqliteWrapper;
        mCacheWriter = cacheWriter;
    }

    public void act(int position, GeocacheListAdapter geocacheListAdapter) {
        // TODO: pull sqliteDatabase and then cachewriter up to top level so
        // they're shared.
        mSQLiteWrapper.openWritableDatabase(mDatabase);
        mCacheWriter.deleteCache(mGeocacheVectors.get(position).getId());
        mSQLiteWrapper.close();

        mGeocacheVectors.remove(position);
        geocacheListAdapter.notifyDataSetChanged();
    }
}
