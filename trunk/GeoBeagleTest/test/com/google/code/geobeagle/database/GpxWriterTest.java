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

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import com.google.code.geobeagle.activity.cachelist.GeoBeagleTest;
import com.google.code.geobeagle.xmlimport.SyncCollectingParameter;
import com.google.inject.Provider;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import android.database.Cursor;

@RunWith(PowerMockRunner.class)
public class GpxWriterTest extends GeoBeagleTest {
    private GpxTableWriterGpxFiles gpxTableWriterGpxFiles;
    private Provider<ISQLiteDatabase> sqliteProvider;
    private ISQLiteDatabase sqlite;
    private Cursor cursor;
    private Capture<String[]> capturedArgument;
    private SyncCollectingParameter syncCollectingParameter;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        sqliteProvider = createMock(Provider.class);
        sqlite = createMock(ISQLiteDatabase.class);
        syncCollectingParameter = createMock(SyncCollectingParameter.class);
        gpxTableWriterGpxFiles = new GpxTableWriterGpxFiles(sqliteProvider, syncCollectingParameter);
        cursor = createMock(Cursor.class);
        capturedArgument = new Capture<String[]>();
    }

    @Test
    public void testEmpty() {
        syncCollectingParameter.Log("  initial sync");
        expect(sqliteProvider.get()).andReturn(sqlite);
        expect(
                sqlite.rawQuery(EasyMock.eq(Database.SQL_GET_EXPORT_TIME),
                        EasyMock.capture(capturedArgument))).andReturn(cursor);
        expect(cursor.moveToFirst()).andReturn(false);
        cursor.close();
        replayAll();
        assertFalse(gpxTableWriterGpxFiles.isGpxAlreadyLoaded("my.gpx", "2000-04-30 12:30:15"));
        verifyAll();

        assertEquals(capturedArgument.getValue()[0], "my.gpx");
    }

    @Test
    public void testOld() {
        syncCollectingParameter.Log("  no changes since 05-01 12:30");

        expect(sqliteProvider.get()).andReturn(sqlite);
        expect(
                sqlite.rawQuery(EasyMock.eq(Database.SQL_GET_EXPORT_TIME),
                        EasyMock.capture(capturedArgument))).andReturn(cursor);
        expect(cursor.moveToFirst()).andReturn(true);
        expect(cursor.getString(0)).andReturn("2000-05-01 12:30:15");
        cursor.close();
        replayAll();
        assertTrue(gpxTableWriterGpxFiles.isGpxAlreadyLoaded("my.gpx", "2000-04-30 12:30:15"));
    }

    @Test
    public void testIdentical() {
        syncCollectingParameter.Log("  no changes since 05-01 12:30");
        expect(sqliteProvider.get()).andReturn(sqlite);
        expect(
                sqlite.rawQuery(EasyMock.eq(Database.SQL_GET_EXPORT_TIME),
                        EasyMock.capture(capturedArgument))).andReturn(cursor);
        expect(cursor.moveToFirst()).andReturn(true);
        expect(cursor.getString(0)).andReturn("2000-05-01 12:30:15");
        cursor.close();
        replayAll();
        assertTrue(gpxTableWriterGpxFiles.isGpxAlreadyLoaded("my.gpx", "2000-05-01 12:30:15"));
    }

    @Test
    public void testParseException() {
        expect(sqliteProvider.get()).andReturn(sqlite);
        expect(
                sqlite.rawQuery(EasyMock.eq(Database.SQL_GET_EXPORT_TIME),
                        EasyMock.capture(capturedArgument))).andReturn(cursor);
        expect(cursor.moveToFirst()).andReturn(true);
        expect(cursor.getString(0)).andReturn("2000-05-01 12:30:15");
        cursor.close();
        replayAll();

        assertFalse(gpxTableWriterGpxFiles.isGpxAlreadyLoaded("my.gpx", "xxx"));
    }

    @Test
    public void testUpdateNeeded() {
        syncCollectingParameter.Log("04-19 12:30 --> 04-30 12:30");
        expect(sqliteProvider.get()).andReturn(sqlite);
        expect(
                sqlite.rawQuery(EasyMock.eq(Database.SQL_GET_EXPORT_TIME),
                        EasyMock.capture(capturedArgument))).andReturn(cursor);
        expect(cursor.moveToFirst()).andReturn(true);
        expect(cursor.getString(0)).andReturn("2000-04-19 12:30:15");
        cursor.close();
        replayAll();
        assertFalse(gpxTableWriterGpxFiles.isGpxAlreadyLoaded("my.gpx", "2000-04-30 12:30:15"));
    }

}
