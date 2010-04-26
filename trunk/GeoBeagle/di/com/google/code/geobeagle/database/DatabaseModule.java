package com.google.code.geobeagle.database;

import com.google.code.geobeagle.database.DatabaseDI.GeoBeagleSqliteOpenHelper;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import roboguice.config.AbstractAndroidModule;
import roboguice.inject.ContextScoped;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseModule extends AbstractAndroidModule {

    @Override
    protected void configure() {
    }

    @Provides
    @ContextScoped
    ISQLiteDatabase sqliteDatabaseProvider(Context context) {
        final SQLiteOpenHelper mSqliteOpenHelper = new GeoBeagleSqliteOpenHelper(context);
        final SQLiteDatabase sqDb = mSqliteOpenHelper.getWritableDatabase();
        return new DatabaseDI.SQLiteWrapper(sqDb);
    }
}
