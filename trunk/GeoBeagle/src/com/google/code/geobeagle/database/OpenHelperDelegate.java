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

public class OpenHelperDelegate {
    public void onCreate(ISQLiteDatabase db) {
        db.execSQL(Database.SQL_CREATE_CACHE_TABLE_V11);
        db.execSQL(Database.SQL_CREATE_GPX_TABLE_V10);
        db.execSQL(Database.SQL_CREATE_TAGS_TABLE_V12);
        db.execSQL(Database.SQL_CREATE_IDX_LATITUDE);
        db.execSQL(Database.SQL_CREATE_IDX_LONGITUDE);
        db.execSQL(Database.SQL_CREATE_IDX_SOURCE);
        db.execSQL(Database.SQL_CREATE_IDX_TAGS);
    }

    public void onUpgrade(ISQLiteDatabase db, int oldVersion) {
        if (oldVersion < 10) {
            db.execSQL("ALTER TABLE CACHES ADD COLUMN " + Database.S0_COLUMN_DELETE_ME);
            db.execSQL(Database.SQL_CREATE_GPX_TABLE_V10);
        }
        if (oldVersion < 11) {
            db.execSQL("ALTER TABLE CACHES ADD COLUMN " + Database.S0_COLUMN_CACHE_TYPE);
            db.execSQL("ALTER TABLE CACHES ADD COLUMN " + Database.S0_COLUMN_CONTAINER);
            db.execSQL("ALTER TABLE CACHES ADD COLUMN " + Database.S0_COLUMN_DIFFICULTY);
            db.execSQL("ALTER TABLE CACHES ADD COLUMN " + Database.S0_COLUMN_TERRAIN);
            // This date has to precede 2000-01-01 (due to a bug in
            // CacheTagSqlWriter.java in v10).
            db.execSQL("UPDATE GPX SET ExportTime = \"1990-01-01\"");
        }
        if (oldVersion < 12) {
            db.execSQL(Database.SQL_CREATE_TAGS_TABLE_V12);
            db.execSQL(Database.SQL_CREATE_IDX_TAGS);
        }
    }
}
