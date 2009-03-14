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

import com.google.code.geobeagle.Locations;
import com.google.code.geobeagle.LocationControl;
import com.google.code.geobeagle.io.Database.CacheReader;
import com.google.code.geobeagle.io.Database.SQLiteWrapper;

import junit.framework.TestCase;

public class LocationBookmarksSqlTest extends TestCase {

    private Database mDatabase;

    public void setUp() {
        mDatabase = createMock(Database.class);
    }

    public void testGetDescriptionsAndLocations() {
        Locations locations = createMock(Locations.class);

        LocationBookmarksSql locationBookmarksSql = new LocationBookmarksSql(null,
                locations, null, null, null, null, null);
        assertEquals(locations, locationBookmarksSql.getDescriptionsAndLocations());
    }

    public void testReadBookmarksCursorOpenError() {
        SQLiteWrapper sqliteWrapper = createMock(SQLiteWrapper.class);
        LocationControl locationControl = createMock(LocationControl.class);
        CacheReader cacheReader = createMock(CacheReader.class);
        Locations locations = createMock(Locations.class);
        sqliteWrapper.openWritableDatabase(mDatabase);
        expect(locationControl.getLocation()).andReturn(null);
        expect(cacheReader.open(null)).andReturn(true);
        expect(cacheReader.getCache()).andReturn("GC1234");
        expect(cacheReader.moveToNext()).andReturn(false);
        cacheReader.close();
        sqliteWrapper.close();

        replay(mDatabase);
        replay(locationControl);
        replay(sqliteWrapper);
        replay(cacheReader);
        LocationBookmarksSql locationBookmarksSql = new LocationBookmarksSql(cacheReader,
                locations, mDatabase, sqliteWrapper, null, null, locationControl);
        locationBookmarksSql.load();
        verify(mDatabase);
        verify(locationControl);
        verify(sqliteWrapper);
        verify(cacheReader);
    }

    public void testReadBookmarksOne() {
        Locations locations = createMock(Locations.class);
        CacheReader cacheReader = createMock(CacheReader.class);

        locations.clear();
        expect(cacheReader.getCache()).andReturn("122 32.3423 83 32.3221 (LB1234)");
        expect(cacheReader.moveToNext()).andReturn(false);
        locations.add("122 32.3423 83 32.3221 (LB1234)");

        replay(cacheReader);
        replay(locations);
        LocationBookmarksSql locationBookmarksSql = new LocationBookmarksSql(cacheReader,
                locations, null, null, null, null, null);
        locationBookmarksSql.read();
        verify(cacheReader);
        verify(locations);
    }

    public void testReadBookmarksTwo() {
        Locations locations = createMock(Locations.class);
        CacheReader cacheReader = createMock(CacheReader.class);

        locations.clear();
        expect(cacheReader.getCache()).andReturn("122 32.3423 83 32.3221 (LB1234)");
        locations.add("122 32.3423 83 32.3221 (LB1234)");
        expect(cacheReader.moveToNext()).andReturn(true);
        expect(cacheReader.getCache()).andReturn("122 32.3423 83 32.3221 (LB54321)");
        locations.add("122 32.3423 83 32.3221 (LB54321)");
        expect(cacheReader.moveToNext()).andReturn(false);

        replay(cacheReader);
        replay(locations);
        LocationBookmarksSql locationBookmarksSql = new LocationBookmarksSql(cacheReader,
                locations, null, null, null, null, null);
        locationBookmarksSql.read();
        verify(cacheReader);
        verify(locations);
    }

}
