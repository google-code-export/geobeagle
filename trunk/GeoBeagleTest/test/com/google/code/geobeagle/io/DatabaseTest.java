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

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import com.google.code.geobeagle.io.Database.ISQLiteDatabase;
import com.google.code.geobeagle.io.Database.OpenHelperDelegate;

import org.junit.Test;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;

public class DatabaseTest {

    static class DesktopSQLiteDatabase implements ISQLiteDatabase {
        Writer mWriter;

        DesktopSQLiteDatabase() throws IOException {
            File db = new File("GeoBeagle.db");
            db.delete();
        }

        public void beginTransaction() {
        }

        public int countResults(String table, String sql, String... args) {
            return 0;
        }

        public String dumpSchema() {
            return exec(".schema");
        }

        public String dumpTable(String table) {
            return exec("SELECT * FROM " + table);
        }

        public void endTransaction() {
        }

        public void execSQL(String s, Object... bindArg1) {
            if (bindArg1.length > 0)
                throw new UnsupportedOperationException("bindArgs not yet supported");
            System.out.print(exec(s));
        }

        public Cursor query(String table, String[] columns, String selection, String groupBy,
                String having, String orderBy, String limit, String... selectionArgs) {
            return null;
        }

        public Cursor query(String table, String[] columns, String selection,
                String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
            return null;
        }

        public void setTransactionSuccessful() {
        }
    }

    // Previous schemas.
    final static String schema6 = "CREATE TABLE CACHES (Id VARCHAR PRIMARY KEY,"
            + " Description VARCHAR Latitude DOUBLE, Longitude DOUBLE, Source VARCHAR)";
    final static String schema7 = "CREATE TABLE CACHES (Id VARCHAR PRIMARY KEY, "
            + "Description VARCHAR, Latitude DOUBLE, Longitude DOUBLE, Source VARCHAR); "
            + "CREATE INDEX IDX_LATITUDE on CACHES (Latitude); "
            + "CREATE INDEX IDX_LONGITUDE on CACHES (Longitude); "
            + "CREATE INDEX IDX_SOURCE on CACHES (Source); ";

    /**
     * <pre>
     * 
     * version 8
     * same as version 7 but rebuilds everything because a released version mistakenly puts 
     * *intent* into imported caches.
     * 
     * version 9
     * fixes bug where INDEX wasn't being created on upgrade.
     * 
     * </pre>
     * 
     * @throws IOException
     */

    private static String convertStreamToString(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line + "\n");
        }
        is.close();

        return sb.toString();
    }

    static String currentSchema() {
        String currentSchema = SQL(Database.SQL_CREATE_CACHE_TABLE)
                + SQL(Database.SQL_CREATE_GPX_TABLE) + SQL(Database.SQL_CREATE_IDX_LATITUDE)
                + SQL(Database.SQL_CREATE_IDX_LONGITUDE) + SQL(Database.SQL_CREATE_IDX_SOURCE);
        return currentSchema;
    }

    private static String exec(String s) {
        String output = null;
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("sqlite3", "GeoBeagle.db", s);
            processBuilder.redirectErrorStream(true);
            InputStream shellIn = null;
            Process shell = processBuilder.start();
            shellIn = shell.getInputStream();
            int result = shell.waitFor();
            output = convertStreamToString(shellIn);
            if (result != 0)
                throw (new RuntimeException(output));
        } catch (InterruptedException e) {
            throw (new RuntimeException(e + "\n" + output));
        } catch (IOException e) {
            throw (new RuntimeException(e + "\n" + output));
        }
        return output;
    }

    private static String SQL(String s) {
        return s + ";\n";
    }

    @Test
    public void testDatabaseGetReableDatabase() {
        SQLiteDatabase sqlite = createMock(SQLiteDatabase.class);
        SQLiteOpenHelper sqliteOpenHelper = createMock(SQLiteOpenHelper.class);

        expect(sqliteOpenHelper.getReadableDatabase()).andReturn(sqlite);

        replay(sqlite);
        replay(sqliteOpenHelper);
        Database database = new Database(sqliteOpenHelper);
        assertEquals(sqlite, database.getReadableDatabase());
        verify(sqliteOpenHelper);
        verify(sqlite);
    }

    public void testDatabaseGetWritableDatabase() {
        SQLiteDatabase sqlite = createMock(SQLiteDatabase.class);
        SQLiteOpenHelper sqliteOpenHelper = createMock(SQLiteOpenHelper.class);

        expect(sqliteOpenHelper.getWritableDatabase()).andReturn(sqlite);

        replay(sqlite);
        replay(sqliteOpenHelper);
        Database database = new Database(sqliteOpenHelper);
        assertEquals(sqlite, database.getWritableDatabase());
        verify(sqliteOpenHelper);
        verify(sqlite);
    }

    public void testOnCreate() throws IOException {
        DesktopSQLiteDatabase db = new DesktopSQLiteDatabase();
        OpenHelperDelegate openHelperDelegate = new OpenHelperDelegate();
        openHelperDelegate.onCreate(db);
        String schema = db.dumpSchema();

        assertEquals(currentSchema(), schema);
    }

    public void testUpgradeFrom6() throws IOException {
        DesktopSQLiteDatabase db = new DesktopSQLiteDatabase();
        db.execSQL(schema6);
        db.execSQL("INSERT INTO CACHES (Id, Source) VALUES (\"GCABC\", \"intent\")");
        OpenHelperDelegate openHelperDelegate = new OpenHelperDelegate();
        openHelperDelegate.onUpgrade(db, 6, Database.DATABASE_VERSION);
        String schema = db.dumpSchema();

        assertEquals(currentSchema(), schema);

        String data = db.dumpTable("CACHES");
        assertEquals("", data);
    }

    public void testUpgradeFrom8() throws IOException {
        DesktopSQLiteDatabase db = new DesktopSQLiteDatabase();
        db.execSQL(schema7);
        db.execSQL("INSERT INTO CACHES (Id, Source) VALUES (\"GCABC\", \"intent\")");
        OpenHelperDelegate openHelperDelegate = new OpenHelperDelegate();
        openHelperDelegate.onUpgrade(db, 8, Database.DATABASE_VERSION);
        String schema = db.dumpSchema();

        // Need to blow away all data from v8.
        String data = db.dumpTable("CACHES");
        assertEquals("", data);

        assertEquals(currentSchema(), schema);

    }

    public void testUpgradeFrom9() throws IOException {
        DesktopSQLiteDatabase db = new DesktopSQLiteDatabase();
        db.execSQL(schema7);
        db.execSQL("INSERT INTO CACHES (Id, Source) VALUES (\"GCABC\", \"intent\")");
        db.execSQL("INSERT INTO CACHES (Id, Source) VALUES (\"GC123\", \"foo.gpx\")");

        OpenHelperDelegate openHelperDelegate = new OpenHelperDelegate();
        openHelperDelegate.onUpgrade(db, 9, Database.DATABASE_VERSION);
        String schema = db.dumpSchema();

        assertEquals(currentSchema(), schema);
        String data = db.dumpTable("CACHES");
        assertEquals("GCABC||||intent|1\nGC123||||foo.gpx|1\n", data);
    }
}
