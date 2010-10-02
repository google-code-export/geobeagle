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

import android.content.ContentValues;
import android.database.Cursor;

public interface ISQLiteDatabase {
    void beginTransaction();

    void close();

    int countResults(String table, String sql, String... args);

    void delete(String table, String where, String bindArg);

    void endTransaction();

    void execSQL(String s, Object... bindArg1);

    boolean hasValue(String table, String[] selection, String[] selectionArgs);

    void insert(String table, String[] columns, Object[] bindArgs);

    boolean isOpen();

    Cursor query(String table,
            String[] columns,
            String selection,
            String groupBy,
            String having,
            String orderBy,
            String limit,
            String... selectionArgs);

    Cursor query(String table,
            String[] columns,
            String selection,
            String[] selectionArgs,
            String groupBy,
            String having,
            String orderBy,
            String limit);

    Cursor rawQuery(String string, String[] object);

    void setTransactionSuccessful();

    void update(String string, ContentValues contentValues, String whereClause, String[] strings);
}
