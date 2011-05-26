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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

class DesktopSQLiteDatabase implements ISQLiteDatabase {

    DesktopSQLiteDatabase() {
        File db = new File("GeoBeagle.db");
        db.delete();
    }

    @Override
    public void beginTransaction() {
    }

    @Override
    public void close() {
    }

    @Override
    public int countResults(String table, String sql, String... args) {
        return 0;
    }

    public String dumpSchema() {
        return DesktopSQLiteDatabase.exec(".schema");
    }

    public String dumpTable(String table) {
        return DesktopSQLiteDatabase.exec("SELECT * FROM " + table);
    }

    @Override
    public void endTransaction() {
    }

    @Override
    public void execSQL(String s, Object... bindArg1) {
        if (bindArg1.length > 0)
            throw new UnsupportedOperationException("bindArgs not yet supported");
        System.out.print(DesktopSQLiteDatabase.exec(s));
    }

    @Override
    public Cursor query(String table, String[] columns, String selection, String groupBy,
            String having, String orderBy, String limit, String... selectionArgs) {
        return null;
    }

    @Override
    public Cursor rawQuery(String string, String[] object) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setTransactionSuccessful() {
    }

    @Override
    public boolean isOpen() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void delete(String table, String where, String bindArg) {
        exec("DELETE FROM " + table + " WHERE " + where + "='" + bindArg + "'");
    }

    @Override
    public void insert(String table, String[] columns, Object[] bindArgs) {
        // Assumes len(bindArgs) > 0.
        StringBuilder columnsAsString = new StringBuilder();
        for (String column : columns) {
            columnsAsString.append(", ");
            columnsAsString.append(column);
        }

        StringBuilder values = new StringBuilder();
        for (Object bindArg : bindArgs) {
            values.append(", ");
            values.append("'" + bindArg + "'");
        }
        values.substring(2);
        exec("REPLACE INTO " + table + "( " + columnsAsString.substring(2) + ") VALUES ("
                + values.substring(2) + ")");
    }

    @Override
    public boolean hasValue(String table, String[] columns, String[] selectionArgs) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(" WHERE " + columns[0] + "=");
        stringBuilder.append("'" + selectionArgs[0] + "'");
        for (int ix = 1; ix < columns.length; ix++) {
            stringBuilder.append(" AND ");
            stringBuilder.append(columns[ix]);
            stringBuilder.append("=");
            stringBuilder.append("'" + selectionArgs[ix] + "'");
        }

        String s = "SELECT COUNT(*) FROM " + table + stringBuilder.toString();
        String result = exec(s);
        return Integer.parseInt(result.trim()) != 0;
    }

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
     * version 10
     * adds GPX table
     *
     * version 11
     * adds new CACHES columns: CacheType, Difficulty, Terrain, and Container
     *
     * version 12: 4/21/2010
     * adds new TAGS table:
     * </pre>
     *
     * @throws IOException
     */
    static String convertStreamToString(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line + "\n");
        }
        is.close();

        return sb.toString();
    }

    static String exec(String s) {
        String output = null;
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("sqlite3", "GeoBeagle.db", s);
            processBuilder.redirectErrorStream(true);
            InputStream shellIn = null;
            Process shell = processBuilder.start();
            shellIn = shell.getInputStream();
            int result = shell.waitFor();
            output = DesktopSQLiteDatabase.convertStreamToString(shellIn);
            if (result != 0)
                throw (new RuntimeException(output));
        } catch (InterruptedException e) {
            throw (new RuntimeException(e + "\n" + output));
        } catch (IOException e) {
            throw (new RuntimeException(e + "\n" + output));
        }
        return output;
    }

    @Override
    public Cursor query(String table,
            String[] columns,
            String selection,
            String[] selectionArgs,
            String groupBy,
            String having,
            String orderBy,
            String limit) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void update(String string,
            ContentValues contentValues,
            String whereClause,
            String[] strings) {
        // TODO Auto-generated method stub

    }
}
