
package com.google.code.geobeagle.io.di;

import com.google.code.geobeagle.LocationControl;
import com.google.code.geobeagle.Locations;
import com.google.code.geobeagle.data.di.DestinationFactory;
import com.google.code.geobeagle.io.CacheReader;
import com.google.code.geobeagle.io.CacheWriter;
import com.google.code.geobeagle.io.Database;
import com.google.code.geobeagle.io.LocationBookmarksSql;
import com.google.code.geobeagle.io.CacheReader.WhereFactory;
import com.google.code.geobeagle.io.Database.ISQLiteDatabase;
import com.google.code.geobeagle.io.Database.OpenHelperDelegate;
import com.google.code.geobeagle.ui.ErrorDisplayer;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseDI {

    public static class CacheReaderCursorFactory {
        public CacheReader.CacheReaderCursor create(Cursor cursor) {
            return new CacheReader.CacheReaderCursor(cursor);
        }
    }

    public static class GeoBeagleSqliteOpenHelper extends SQLiteOpenHelper {
        private final Database.OpenHelperDelegate mOpenHelperDelegate;

        public GeoBeagleSqliteOpenHelper(Context context,
                Database.OpenHelperDelegate openHelperDelegate) {
            super(context, Database.DATABASE_NAME, null, Database.DATABASE_VERSION);
            mOpenHelperDelegate = openHelperDelegate;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            mOpenHelperDelegate.onCreate(new SQLiteWrapper(db));
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            mOpenHelperDelegate.onUpgrade(new SQLiteWrapper(db), oldVersion, newVersion);
        }
    }

    public static class SQLiteWrapper implements ISQLiteDatabase {
        private SQLiteDatabase mSQLiteDatabase;

        public SQLiteWrapper(SQLiteDatabase db) {
            mSQLiteDatabase = db;
        }

        public void beginTransaction() {
            mSQLiteDatabase.beginTransaction();
        }

        public void close() {
            mSQLiteDatabase.close();
        }

        public int countResults(String table, String selection, String... selectionArgs) {
            Cursor cursor = mSQLiteDatabase.query(table, null, selection, selectionArgs, null,
                    null, null, null);
            int count = cursor.getCount();
            cursor.close();
            return count;
        }

        public void endTransaction() {
            mSQLiteDatabase.endTransaction();
        }

        public void execSQL(String sql) {
            mSQLiteDatabase.execSQL(sql);
        }

        public void execSQL(String sql, Object... bindArgs) {
            mSQLiteDatabase.execSQL(sql, bindArgs);
        }

        public void openReadableDatabase(Database database) {
            mSQLiteDatabase = database.getReadableDatabase();
        }

        public void openWritableDatabase(Database database) {
            mSQLiteDatabase = database.getWritableDatabase();
        }

        public Cursor query(String table, String[] columns, String selection, String groupBy,
                String having, String orderBy, String limit, String... selectionArgs) {
            return mSQLiteDatabase.query(table, columns, selection, selectionArgs, groupBy,
                    orderBy, having, limit);
        }

        public Cursor rawQuery(String sql, String[] selectionArgs) {
            return mSQLiteDatabase.rawQuery(sql, selectionArgs);
        }

        public void setTransactionSuccessful() {
            mSQLiteDatabase.setTransactionSuccessful();
        }
    }

    public static Database create(Context context) {
        final OpenHelperDelegate openHelperDelegate = new Database.OpenHelperDelegate();
        final GeoBeagleSqliteOpenHelper sqliteOpenHelper = new GeoBeagleSqliteOpenHelper(context,
                openHelperDelegate);
        return new Database(sqliteOpenHelper);
    }

    public static LocationBookmarksSql create(LocationControl locationControl, Database database,
            DestinationFactory destinationFactory, ErrorDisplayer errorDisplayer) {
        final Locations locations = new Locations();
        final SQLiteWrapper sqliteWrapper = new SQLiteWrapper(null);
        final CacheReader cacheReader = createCacheReader(sqliteWrapper);
        return new LocationBookmarksSql(cacheReader, locations, database, sqliteWrapper,
                destinationFactory, errorDisplayer, locationControl);
    }

    public static CacheReader createCacheReader(SQLiteWrapper sqliteWrapper) {
        final WhereFactory whereFactory = new CacheReader.WhereFactory();
        final CacheReaderCursorFactory cacheReaderCursorFactory = new CacheReaderCursorFactory();
        return new CacheReader(sqliteWrapper, whereFactory, cacheReaderCursorFactory);
    }

    public static CacheWriter createCacheWriter(SQLiteWrapper sqliteWrapper) {
        return new CacheWriter(sqliteWrapper);
    }

}
