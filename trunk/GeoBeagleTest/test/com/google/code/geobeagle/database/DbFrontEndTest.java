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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.activity.cachelist.GeoBeagleTest;
import com.google.code.geobeagle.database.DatabaseDI.GeoBeagleSqliteOpenHelper;
import com.google.code.geobeagle.database.DatabaseDI.SQLiteWrapper;
import com.google.code.geobeagle.preferences.PreferencesUpgrader;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

@PrepareForTest( {
        Log.class, DbFrontend.class, DatabaseDI.class
})
@RunWith(PowerMockRunner.class)
public class DbFrontEndTest extends GeoBeagleTest {

    private CacheReader mCacheReader;
    private SQLiteWrapper mDatabase;
    private GeoBeagleSqliteOpenHelper mOpenHelper;
    private PreferencesUpgrader preferencesUpgrader;
    private Context context;
    private Activity activity;

    @Before
    public void setUp() {
        preferencesUpgrader = PowerMock.createMock(PreferencesUpgrader.class);
        context = PowerMock.createMock(Context.class);
        activity = PowerMock.createMock(Activity.class);
        mOpenHelper = PowerMock.createMock(GeoBeagleSqliteOpenHelper.class);
    }

    private void expectOpenDatabase() throws Exception {
        SQLiteDatabase db = PowerMock.createMock(SQLiteDatabase.class);
        mDatabase = PowerMock.createMock(SQLiteWrapper.class);
        mCacheReader = PowerMock.createMock(CacheReader.class);

        PowerMock.suppressConstructor(GeoBeagleSqliteOpenHelper.class);
        PowerMock.expectNew(GeoBeagleSqliteOpenHelper.class, context, preferencesUpgrader)
                .andReturn(mOpenHelper);
        EasyMock.expect(mOpenHelper.getWritableDatabase()).andReturn(db);
        PowerMock.expectNew(DatabaseDI.SQLiteWrapper.class, db).andReturn(mDatabase);
        PowerMock.mockStatic(DatabaseDI.class);
    }

    @Test
    public void testCloseClosedDatabase() {
        EasyMock.expect(activity.getApplicationContext()).andReturn(context);
        final DbFrontend dbFrontend = new DbFrontend(activity, mCacheReader, null, null);
        dbFrontend.closeDatabase();
    }

    @Test
    public void testCloseOpenedDatabase() throws Exception {
        expectOpenDatabase();
        EasyMock.expect(activity.getApplicationContext()).andReturn(context);

        mOpenHelper.close();

        PowerMock.replayAll();
        final DbFrontend dbFrontend = new DbFrontend(activity, mCacheReader, preferencesUpgrader,
                null);
        dbFrontend.openDatabase();
        dbFrontend.closeDatabase();
        PowerMock.verifyAll();
    }

    @Test
    public void testCount() throws Exception {
        Cursor countCursor = PowerMock.createMock(Cursor.class);
        expectOpenDatabase();
        WhereFactoryFixedArea whereFactory = PowerMock.createMock(WhereFactoryFixedArea.class);

        EasyMock.expect(activity.getApplicationContext()).andReturn(context);
        EasyMock.expect(whereFactory.getWhere(mDatabase, 122, 37)).andReturn("foo=bar");
        EasyMock.expect(
                mDatabase.rawQuery(
                        "SELECT COUNT(*) FROM " + Database.TBL_CACHES + " WHERE foo=bar", null))
                .andReturn(countCursor);
        EasyMock.expect(countCursor.moveToFirst()).andReturn(true);
        EasyMock.expect(countCursor.getInt(0)).andReturn(9000);
        countCursor.close();

        PowerMock.replayAll();
        final DbFrontend dbFrontend = new DbFrontend(activity, mCacheReader, preferencesUpgrader,
                null);
        dbFrontend.openDatabase();
        assertEquals(9000, dbFrontend.count(122, 37, whereFactory));
        PowerMock.verifyAll();
    }

    @Test
    public void testCountAll() throws Exception {
        Cursor countCursor = PowerMock.createMock(Cursor.class);
        EasyMock.expect(activity.getApplicationContext()).andReturn(context);
        expectOpenDatabase();
        EasyMock.expect(mDatabase.rawQuery("SELECT COUNT(*) FROM " + Database.TBL_CACHES, null))
                .andReturn(countCursor);
        EasyMock.expect(countCursor.moveToFirst()).andReturn(true);
        EasyMock.expect(countCursor.getInt(0)).andReturn(9000);
        countCursor.close();

        PowerMock.replayAll();
        final DbFrontend dbFrontend = new DbFrontend(activity, mCacheReader, preferencesUpgrader,
                null);
        dbFrontend.openDatabase();
        assertEquals(9000, dbFrontend.countAll());
        PowerMock.verifyAll();
    }

    @Test
    public void testDbFrontEnd() {
        EasyMock.expect(activity.getApplicationContext()).andReturn(context);
        assertNotNull(new DbFrontend(activity, mCacheReader, null, null));
    }

    @Test
    public void testLoadCaches() throws Exception {
        CacheReaderCursor cursor = PowerMock.createMock(CacheReaderCursor.class);
        expectOpenDatabase();
        WhereFactory whereFactory = PowerMock.createMock(WhereFactory.class);
        Geocache cache = PowerMock.createMock(Geocache.class);

        EasyMock.expect(activity.getApplicationContext()).andReturn(context);
        EasyMock.expect(mCacheReader.open(122, 37, whereFactory, null)).andReturn(cursor);
        EasyMock.expect(cursor.getCache()).andReturn(cache);
        EasyMock.expect(cursor.moveToNext()).andReturn(false);
        cursor.close();

        PowerMock.replayAll();
        final DbFrontend dbFrontend = new DbFrontend(activity, mCacheReader, preferencesUpgrader,
                null);
        dbFrontend.openDatabase();
        dbFrontend.loadCaches(122, 37, whereFactory);
        PowerMock.verifyAll();
    }

    @Test
    public void testOpenDatabase() throws Exception {
        expectOpenDatabase();
        EasyMock.expect(activity.getApplicationContext()).andReturn(context);

        PowerMock.replayAll();
        final DbFrontend dbFrontend = new DbFrontend(activity, mCacheReader, preferencesUpgrader,
                null);
        dbFrontend.openDatabase();
        PowerMock.verifyAll();
    }

    @Test
    public void testOpenOpenedDatabase() throws Exception {
        EasyMock.expect(activity.getApplicationContext()).andReturn(context);
        expectOpenDatabase();

        PowerMock.replayAll();
        final DbFrontend dbFrontend = new DbFrontend(activity, mCacheReader, preferencesUpgrader,
                null);
        dbFrontend.openDatabase();
        dbFrontend.openDatabase();
        PowerMock.verifyAll();
    }
}
