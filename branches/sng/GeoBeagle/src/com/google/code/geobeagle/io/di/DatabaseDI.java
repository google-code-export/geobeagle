package com.google.code.geobeagle.io.di;

import com.google.code.geobeagle.io.CacheReader;
import com.google.code.geobeagle.io.CacheWriter;
import com.google.code.geobeagle.io.Database;
import com.google.code.geobeagle.io.CacheReader.WhereFactory;
import com.google.code.geobeagle.io.Database.OpenHelperDelegate;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseDI {

    public static class GeoBeagleSqliteOpenHelper extends SQLiteOpenHelper {
        private final Database.OpenHelperDelegate mOpenHelperDelegate;
    
        public GeoBeagleSqliteOpenHelper(Context context, Database.OpenHelperDelegate openHelperDelegate) {
            super(context, Database.DATABASE_NAME, null, Database.DATABASE_VERSION);
            mOpenHelperDelegate = openHelperDelegate;
        }
    
        @Override
        public void onCreate(SQLiteDatabase db) {
            mOpenHelperDelegate.onCreate(new DatabaseDI.SQLiteWrapper(db));
        }
    
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            mOpenHelperDelegate.onUpgrade(new DatabaseDI.SQLiteWrapper(db), oldVersion, newVersion);
        }
    }

    public static class SQLiteWrapper implements Database.ISQLiteDatabase {
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
    
        public void endTransaction() {
            mSQLiteDatabase.endTransaction();
        }
    
        public void execSQL(String sql) {
            mSQLiteDatabase.execSQL(sql);
        }
    
        public void execSQL(String sql, Object[] bindArgs) {
            mSQLiteDatabase.execSQL(sql, bindArgs);
        }
    
        public void openReadableDatabase(Database database) {
            mSQLiteDatabase = database.getReadableDatabase();
        }
    
        public void openWritableDatabase(Database database) {
            mSQLiteDatabase = database.getWritableDatabase();
        }
    
        public Cursor query(String table, String[] columns, String selection,
                String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
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
        final GeoBeagleSqliteOpenHelper sqliteOpenHelper = new DatabaseDI.GeoBeagleSqliteOpenHelper(context,
                openHelperDelegate);
        return new Database(sqliteOpenHelper);
    }

    public static CacheWriter createCacheWriter(SQLiteWrapper sqliteWrapper) {
        return new CacheWriter(sqliteWrapper);
    }

    public static CacheReader createCacheReader(SQLiteWrapper sqliteWrapper) {
        final WhereFactory whereFactory = new CacheReader.WhereFactory();
        return new CacheReader(sqliteWrapper, whereFactory);
    }

}
