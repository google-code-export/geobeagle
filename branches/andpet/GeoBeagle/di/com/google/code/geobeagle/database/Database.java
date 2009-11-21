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

/**
 * Database version history:
 * 12: Adds tables LABELS and CACHELABELS
 */
public class Database {
    public static final String DATABASE_NAME = "GeoBeagle.db";
    public static final int DATABASE_VERSION = 12;
    public static final String S0_COLUMN_CACHE_TYPE = "CacheType INTEGER NOT NULL Default 0";
    public static final String S0_COLUMN_CONTAINER = "Container INTEGER NOT NULL Default 0";
    public static final String S0_COLUMN_DELETE_ME = "DeleteMe BOOLEAN NOT NULL Default 1";
    public static final String S0_COLUMN_DIFFICULTY = "Difficulty INTEGER NOT NULL Default 0";
    public static final String S0_COLUMN_TERRAIN = "Terrain INTEGER NOT NULL Default 0";
    public static final String S0_INTENT = "intent";
    public static final String SQL_CACHES_DONT_DELETE_ME = "UPDATE CACHES SET DeleteMe = 0 WHERE Source = ?";
    public static final String SQL_CLEAR_CACHES = "DELETE FROM CACHES WHERE Source=?";
    public static final String SQL_CREATE_CACHE_TABLE_V08 = "CREATE TABLE CACHES ("
            + "Id VARCHAR PRIMARY KEY, Description VARCHAR, "
            + "Latitude DOUBLE, Longitude DOUBLE, Source VARCHAR);";
    public static final String SQL_CREATE_CACHE_TABLE_V10 = "CREATE TABLE CACHES ("
            + "Id VARCHAR PRIMARY KEY, Description VARCHAR, "
            + "Latitude DOUBLE, Longitude DOUBLE, Source VARCHAR, " + S0_COLUMN_DELETE_ME + ");";
    public static final String SQL_CREATE_CACHE_TABLE_V11 = "CREATE TABLE CACHES ("
            + "Id VARCHAR PRIMARY KEY, Description VARCHAR, "
            + "Latitude DOUBLE, Longitude DOUBLE, Source VARCHAR, " + S0_COLUMN_DELETE_ME + ", "
            + S0_COLUMN_CACHE_TYPE + ", " + S0_COLUMN_CONTAINER + ", " + S0_COLUMN_DIFFICULTY
            + ", " + S0_COLUMN_TERRAIN + ");";
    public static final String SQL_CREATE_GPX_TABLE_V10 = "CREATE TABLE GPX ("
            + "Name VARCHAR PRIMARY KEY NOT NULL, ExportTime DATETIME NOT NULL, DeleteMe BOOLEAN NOT NULL);";
    //V12:
    public static final String SQL_CREATE_LABELS_TABLE_V12 = "CREATE TABLE LABELS ("
        + "Id VARCHAR PRIMARY KEY NOT NULL, Name VARCHAR NOT NULL, Locked BOOLEAN NOT NULL);";
    public static final String SQL_CREATE_CACHELABELS_TABLE_V12 = "CREATE TABLE CACHELABELS ("
        + "CacheId VARCHAR NOT NULL, LabelId INTEGER NOT NULL);";
    
    public static final String SQL_CREATE_IDX_CACHELABELS = "CREATE INDEX IDX_CACHELABELS on CACHELABELS (CacheId, LabelId);";
    public static final String SQL_CREATE_IDX_LATITUDE = "CREATE INDEX IDX_LATITUDE on CACHES (Latitude);";
    public static final String SQL_CREATE_IDX_LONGITUDE = "CREATE INDEX IDX_LONGITUDE on CACHES (Longitude);";
    public static final String SQL_CREATE_IDX_SOURCE = "CREATE INDEX IDX_SOURCE on CACHES (Source);";
    public static final String SQL_DELETE_CACHE = "DELETE FROM CACHES WHERE Id=?";
    public static final String SQL_DELETE_OLD_CACHES = "DELETE FROM CACHES WHERE DeleteMe = 1";
    public static final String SQL_DELETE_OLD_GPX = "DELETE FROM GPX WHERE DeleteMe = 1";
    public static final String SQL_DELETE_CACHELABEL = "DELETE FROM CACHELABELS WHERE CacheId = ? AND LabelId = ?";

    
    public static final String SQL_DROP_CACHE_TABLE = "DROP TABLE IF EXISTS CACHES";
    public static final String SQL_GPX_DONT_DELETE_ME = "UPDATE GPX SET DeleteMe = 0 WHERE Name = ?";
    public static final String SQL_MATCH_NAME_AND_EXPORTED_LATER = "Name = ? AND ExportTime >= ?";

    public static final String SQL_REPLACE_CACHE = "REPLACE INTO CACHES "
            + "(Id, Description, Latitude, Longitude, Source, DeleteMe, CacheType, Difficulty, Terrain, Container) VALUES (?, ?, ?, ?, ?, 0, ?, ?, ?, ?)";
    public static final String SQL_REPLACE_GPX = "REPLACE INTO GPX (Name, ExportTime, DeleteMe) VALUES (?, ?, 0)";
    public static final String SQL_REPLACE_LABEL = "REPLACE INTO LABELS " + "(Id, Name, Locked) VALUES (?, ?, ?)";
    public static final String SQL_REPLACE_CACHELABEL = "REPLACE INTO CACHELABELS " + "(CacheId, LabelId) VALUES (?, ?)";
    public static final String SQL_RESET_DELETE_ME_CACHES = "UPDATE CACHES SET DeleteMe = 1 WHERE Source != '"
            + S0_INTENT + "'";
    public static final String SQL_RESET_DELETE_ME_GPX = "UPDATE GPX SET DeleteMe = 1";

    public static final String TBL_CACHES = "CACHES";
    public static final String TBL_GPX = "GPX";
    public static final String TBL_LABELS = "LABELS";
    public static final String TBL_CACHELABELS = "CACHELABELS";
}
