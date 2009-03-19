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

import com.google.code.geobeagle.LocationControl;
import com.google.code.geobeagle.Locations;
import com.google.code.geobeagle.io.CacheReader.CacheReaderCursor;
import com.google.code.geobeagle.io.di.DatabaseDI.SQLiteWrapper;

import junit.framework.TestCase;

public class LocationBookmarksSqlTest extends TestCase {

    private Database mDatabase;

    @Override
    public void setUp() {
        mDatabase = createMock(Database.class);
    }

    public void testGetDescriptionsAndLocations() {
        Locations locations = createMock(Locations.class);

        LocationBookmarksSql locationBookmarksSql = new LocationBookmarksSql(null, locations, null,
                null, null, null, null);
        assertEquals(locations, locationBookmarksSql.getDescriptionsAndLocations());
    }

    public void testLoad() {
        SQLiteWrapper sqliteWrapper = createMock(SQLiteWrapper.class);
        LocationControl locationControl = createMock(LocationControl.class);
        CacheReader cacheReader = createMock(CacheReader.class);
        Locations locations = createMock(Locations.class);
        CacheReaderCursor cursor = createMock(CacheReaderCursor.class);

        sqliteWrapper.openWritableDatabase(mDatabase);
        expect(locationControl.getLocation()).andReturn(null);
        expect(cacheReader.open(null)).andReturn(cursor);
        expect(cursor.getCache()).andReturn("GC1234");
        expect(cursor.moveToNext()).andReturn(false);
        cursor.close();
        sqliteWrapper.close();

        replay(mDatabase);
        replay(locationControl);
        replay(sqliteWrapper);
        replay(cacheReader);
        replay(cursor);
        LocationBookmarksSql locationBookmarksSql = new LocationBookmarksSql(cacheReader,
                locations, mDatabase, sqliteWrapper, null, null, locationControl);
        locationBookmarksSql.load();
        verify(mDatabase);
        verify(locationControl);
        verify(sqliteWrapper);
        verify(cacheReader);
        verify(cursor);
    }

    public void testReadOne() {
        Locations locations = createMock(Locations.class);
        CacheReaderCursor cursor = createMock(CacheReaderCursor.class);

        locations.clear();
        expect(cursor.getCache()).andReturn("122 32.3423 83 32.3221 (LB1234)");
        expect(cursor.moveToNext()).andReturn(false);
        locations.add("122 32.3423 83 32.3221 (LB1234)");

        replay(locations);
        replay(cursor);
        LocationBookmarksSql locationBookmarksSql = new LocationBookmarksSql(null, locations, null,
                null, null, null, null);
        locationBookmarksSql.read(cursor);
        verify(locations);
        verify(cursor);
    }

    public void testReadTwo() {
        Locations locations = createMock(Locations.class);
        CacheReaderCursor cursor = createMock(CacheReaderCursor.class);

        locations.clear();
        expect(cursor.getCache()).andReturn("122 32.3423 83 32.3221 (LB1234)");
        locations.add("122 32.3423 83 32.3221 (LB1234)");
        expect(cursor.moveToNext()).andReturn(true);
        expect(cursor.getCache()).andReturn("122 32.3423 83 32.3221 (LB54321)");
        locations.add("122 32.3423 83 32.3221 (LB54321)");
        expect(cursor.moveToNext()).andReturn(false);

        replay(locations);
        replay(cursor);
        LocationBookmarksSql locationBookmarksSql = new LocationBookmarksSql(null, locations, null,
                null, null, null, null);
        locationBookmarksSql.read(cursor);
        verify(locations);
        verify(cursor);
    }

}
