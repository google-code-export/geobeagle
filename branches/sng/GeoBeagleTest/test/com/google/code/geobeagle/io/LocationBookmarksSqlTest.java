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

import com.google.code.geobeagle.DescriptionsAndLocations;
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
        DescriptionsAndLocations descriptionsAndLocations = createMock(DescriptionsAndLocations.class);

        LocationBookmarksSql locationBookmarksSql = new LocationBookmarksSql(null,
                descriptionsAndLocations, null, null, null, null, null);
        assertEquals(descriptionsAndLocations, locationBookmarksSql.getDescriptionsAndLocations());
    }

    public void testReadBookmarksCursorOpenError() {
        SQLiteWrapper sqliteWrapper = createMock(SQLiteWrapper.class);
        LocationControl locationControl = createMock(LocationControl.class);
        CacheReader cacheReader = createMock(CacheReader.class);
        DescriptionsAndLocations descriptionsAndLocations = createMock(DescriptionsAndLocations.class);
        sqliteWrapper.openReadableDatabase(mDatabase);
        expect(locationControl.getLocation()).andReturn(null);
        expect(cacheReader.open(null)).andReturn(true);
        expect(cacheReader.getCache()).andReturn("GC1234");
        expect(cacheReader.moveToNext()).andReturn(false);
        cacheReader.close();
        expect(cacheReader.getTotalCount()).andReturn(1000);
        sqliteWrapper.close();
        
        replay(mDatabase);
        replay(locationControl);
        replay(sqliteWrapper);
        replay(cacheReader);
        LocationBookmarksSql locationBookmarksSql = new LocationBookmarksSql(cacheReader,
                descriptionsAndLocations, mDatabase, sqliteWrapper, null, null, locationControl);
        locationBookmarksSql.onResume(null);
        verify(mDatabase);
        verify(locationControl);
        verify(sqliteWrapper);
        verify(cacheReader);
    }

    public void testReadBookmarksOne() {
        DescriptionsAndLocations descriptionsAndLocations = createMock(DescriptionsAndLocations.class);
        CacheReader cacheReader = createMock(CacheReader.class);

        descriptionsAndLocations.clear();
        expect(cacheReader.getCache()).andReturn("122 32.3423 83 32.3221 (LB1234)");
        expect(cacheReader.moveToNext()).andReturn(false);
        descriptionsAndLocations.add("LB1234", "122 32.3423 83 32.3221 (LB1234)");

        replay(cacheReader);
        replay(descriptionsAndLocations);
        LocationBookmarksSql locationBookmarksSql = new LocationBookmarksSql(cacheReader,
                descriptionsAndLocations, null, null, null, null, null);
        locationBookmarksSql.readBookmarks();
        verify(cacheReader);
        verify(descriptionsAndLocations);
    }

    public void testReadBookmarksTwo() {
        DescriptionsAndLocations descriptionsAndLocations = createMock(DescriptionsAndLocations.class);
        CacheReader cacheReader = createMock(CacheReader.class);

        descriptionsAndLocations.clear();
        expect(cacheReader.getCache()).andReturn("122 32.3423 83 32.3221 (LB1234)");
        descriptionsAndLocations.add("LB1234", "122 32.3423 83 32.3221 (LB1234)");
        expect(cacheReader.moveToNext()).andReturn(true);
        expect(cacheReader.getCache()).andReturn("122 32.3423 83 32.3221 (LB54321)");
        descriptionsAndLocations.add("LB54321", "122 32.3423 83 32.3221 (LB54321)");
        expect(cacheReader.moveToNext()).andReturn(false);

        replay(cacheReader);
        replay(descriptionsAndLocations);
        LocationBookmarksSql locationBookmarksSql = new LocationBookmarksSql(cacheReader,
                descriptionsAndLocations, null, null, null, null, null);
        locationBookmarksSql.readBookmarks();
        verify(cacheReader);
        verify(descriptionsAndLocations);
    }

}
