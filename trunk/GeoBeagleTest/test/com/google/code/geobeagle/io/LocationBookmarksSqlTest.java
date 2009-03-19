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

import com.google.code.geobeagle.Geocaches;
import com.google.code.geobeagle.LocationControl;
import com.google.code.geobeagle.data.Geocache;
import com.google.code.geobeagle.io.CacheReader.CacheReaderCursor;
import com.google.code.geobeagle.io.di.DatabaseDI.SQLiteWrapper;

import junit.framework.TestCase;

public class LocationBookmarksSqlTest extends TestCase {

    private Database mDatabase;

    @Override
    public void setUp() {
        mDatabase = createMock(Database.class);
    }

    public void testLoad() {
        SQLiteWrapper sqliteWrapper = createMock(SQLiteWrapper.class);
        LocationControl locationControl = createMock(LocationControl.class);
        CacheReader cacheReader = createMock(CacheReader.class);
        Geocaches geocaches = createMock(Geocaches.class);
        CacheReaderCursor cursor = createMock(CacheReaderCursor.class);
        Geocache geocache = createMock(Geocache.class);

        sqliteWrapper.openWritableDatabase(mDatabase);
        expect(locationControl.getLocation()).andReturn(null);
        expect(cacheReader.open(null)).andReturn(cursor);
        expect(cursor.getCache()).andReturn(geocache);
        expect(cursor.moveToNext()).andReturn(false);
        cursor.close();
        sqliteWrapper.close();

        replay(mDatabase);
        replay(locationControl);
        replay(sqliteWrapper);
        replay(cacheReader);
        replay(cursor);
        GeocachesSql geocachesSql = new GeocachesSql(cacheReader,
                geocaches, mDatabase, sqliteWrapper, null, null, locationControl);
        geocachesSql.load();
        verify(mDatabase);
        verify(locationControl);
        verify(sqliteWrapper);
        verify(cacheReader);
        verify(cursor);
    }

    public void testReadOne() {
        Geocaches geocaches = createMock(Geocaches.class);
        CacheReaderCursor cursor = createMock(CacheReaderCursor.class);
        Geocache geocache = createMock(Geocache.class);

        geocaches.clear();
        expect(cursor.getCache()).andReturn(geocache);
        expect(cursor.moveToNext()).andReturn(false);
        geocaches.add(geocache);

        replay(geocaches);
        replay(cursor);
        GeocachesSql geocachesSql = new GeocachesSql(null, geocaches, null,
                null, null, null, null);
        geocachesSql.read(cursor);
        verify(geocaches);
        verify(cursor);
    }

    public void testReadTwo() {
        Geocaches geocaches = createMock(Geocaches.class);
        CacheReaderCursor cursor = createMock(CacheReaderCursor.class);
        Geocache geocache1 = createMock(Geocache.class);
        Geocache geocache2 = createMock(Geocache.class);

        geocaches.clear();
        expect(cursor.getCache()).andReturn(geocache1);
        geocaches.add(geocache1);
        expect(cursor.moveToNext()).andReturn(true);
        expect(cursor.getCache()).andReturn(geocache2);
        geocaches.add(geocache2);
        expect(cursor.moveToNext()).andReturn(false);

        replay(geocaches);
        replay(cursor);
        GeocachesSql geocachesSql = new GeocachesSql(null, geocaches, null,
                null, null, null, null);
        geocachesSql.read(cursor);
        verify(geocaches);
        verify(cursor);
    }

}
