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

package com.google.code.geobeagle;

import com.google.code.geobeagle.data.CacheListData;
import com.google.code.geobeagle.io.Database;
import com.google.code.geobeagle.io.Database.CacheWriter;
import com.google.code.geobeagle.io.Database.SQLiteWrapper;
import com.google.code.geobeagle.ui.CacheListDelegate;
import com.google.code.geobeagle.ui.ErrorDisplayer;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.widget.SimpleAdapter;

public class CacheListActions {

    public static CacheListActions.Action[] create(ListActivity parent, Database database,
            SQLiteWrapper sqliteWrapper, CacheListData cacheListData, ErrorDisplayer errorDisplayer) {
        final Intent intent = new Intent(parent, GeoBeagle.class);
        return new CacheListActions.Action[] {
                new CacheListActions.Delete(database, sqliteWrapper, cacheListData, errorDisplayer),
                new CacheListActions.View(cacheListData, parent, intent)
        };
    }

    public static interface Action {
        public void act(int position, SimpleAdapter simpleAdapter);
    }

    public static class Delete implements Action {
        private final CacheListData mCacheListData;
        private final Database mDatabase;
        private final SQLiteWrapper mSQLiteWrapper;

        public Delete(Database database, SQLiteWrapper sqliteWrapper, CacheListData cacheListData,
                ErrorDisplayer errorDisplayer) {
            mCacheListData = cacheListData;
            mDatabase = database;
            mSQLiteWrapper = sqliteWrapper;
        }

        public void act(int position, SimpleAdapter simpleAdapter) {
            // TODO: pull sqliteDatabase and then cachewriter up to top level so
            // they're shared.
            mSQLiteWrapper.openWritableDatabase(mDatabase);
            CacheWriter cacheWriter = mDatabase.createCacheWriter(mSQLiteWrapper);
            cacheWriter.deleteCache(mCacheListData.getId(position));
            mSQLiteWrapper.close();

            mCacheListData.delete(position);

            simpleAdapter.notifyDataSetChanged();
        }
    }

    public static class View implements Action {
        private final CacheListData mCacheListData;
        private final Context mContext;
        private final Intent mIntent;

        public View(CacheListData cacheListData, Context context, Intent intent) {
            mCacheListData = cacheListData;
            mContext = context;
            mIntent = intent;
        }

        public void act(int position, SimpleAdapter simpleAdapter) {
            mIntent.putExtra("location", mCacheListData.getCoordinatesIdAndName(position)).setAction(
                    CacheListDelegate.SELECT_CACHE);
            mContext.startActivity(mIntent);
        }
    }
}
