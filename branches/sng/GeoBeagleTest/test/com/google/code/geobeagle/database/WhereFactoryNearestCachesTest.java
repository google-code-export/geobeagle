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

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.database.DatabaseDI.SearchFactory;
import com.google.code.geobeagle.database.WhereFactoryNearestCaches.BoundingBox;
import com.google.code.geobeagle.database.WhereFactoryNearestCaches.Search;
import com.google.code.geobeagle.database.WhereFactoryNearestCaches.WhereStringFactory;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.database.Cursor;
import android.util.Log;

@PrepareForTest( {
    Log.class
})
@RunWith(PowerMockRunner.class)
public class WhereFactoryNearestCachesTest {

    @Test
    public void testGetWhereString() {
        PowerMock.replayAll();
        assertEquals(
                "Latitude > 89.0 AND Latitude < 91.0 AND Longitude > -180.0 AND Longitude < 180.0",
                new WhereStringFactory().getWhereString(90.0, 180.0, 1f));
        PowerMock.verifyAll();
    }

    @Test
    public void testReadOne() {
        Geocaches geocaches = PowerMock.createMock(Geocaches.class);
        CacheReaderCursor cursor = PowerMock.createMock(CacheReaderCursor.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);

        expect(cursor.getCache()).andReturn(geocache);
        expect(cursor.moveToNext()).andReturn(false);
        geocaches.add(geocache);

        PowerMock.replayAll();
        GeocachesSql geocachesSql = new GeocachesSql(null, geocaches);
        geocachesSql.read(cursor);
        PowerMock.verifyAll();
    }

    @Test
    public void testReadTwo() {
        Geocaches geocaches = PowerMock.createMock(Geocaches.class);
        CacheReaderCursor cursor = PowerMock.createMock(CacheReaderCursor.class);
        Geocache geocache1 = PowerMock.createMock(Geocache.class);
        Geocache geocache2 = PowerMock.createMock(Geocache.class);

        expect(cursor.getCache()).andReturn(geocache1);
        geocaches.add(geocache1);
        expect(cursor.moveToNext()).andReturn(true);
        expect(cursor.getCache()).andReturn(geocache2);
        geocaches.add(geocache2);
        expect(cursor.moveToNext()).andReturn(false);

        PowerMock.replayAll();
        GeocachesSql geocachesSql = new GeocachesSql(null, geocaches);
        geocachesSql.read(cursor);
        PowerMock.verifyAll();
    }

    @Test
    public void testBinarySearchUpOne() {
        BoundingBox boundingBox = PowerMock.createMock(BoundingBox.class);

        EasyMock.expect(boundingBox.getCount(1.0f, 50)).andReturn(50);

        PowerMock.replayAll();
        WhereFactoryNearestCaches.SearchUp search = new WhereFactoryNearestCaches.SearchUp(
                boundingBox, 100);
        assertEquals(1.0, search.search(1.0f, 50), 0.0);
        PowerMock.verifyAll();
    }

    @Test
    public void testBinarySearchUpTwo() {
        BoundingBox boundingBox = PowerMock.createMock(BoundingBox.class);

        EasyMock.expect(boundingBox.getCount(1.0f, 50)).andReturn(25);
        EasyMock.expect(boundingBox.getCount(WhereFactoryNearestCaches.DISTANCE_MULTIPLIER, 50))
                .andReturn(50);

        PowerMock.replayAll();
        WhereFactoryNearestCaches.SearchUp search = new WhereFactoryNearestCaches.SearchUp(
                boundingBox, 100);
        assertEquals(WhereFactoryNearestCaches.DISTANCE_MULTIPLIER, search.search(1.0f, 50), 0.0);
        PowerMock.verifyAll();
    }

    @Test
    public void testBinarySearchUpMax() {
        BoundingBox boundingBox = PowerMock.createMock(BoundingBox.class);

        PowerMock.replayAll();
        WhereFactoryNearestCaches.SearchUp search = new WhereFactoryNearestCaches.SearchUp(
                boundingBox, 1.0f);
        assertEquals(1.0, search.search(1.0f, 50), 0.0);
        PowerMock.verifyAll();
    }

    @Test
    public void testBinarySearchDownOne() {
        BoundingBox boundingBox = PowerMock.createMock(BoundingBox.class);

        EasyMock.expect(
                boundingBox.getCount(1.0f / WhereFactoryNearestCaches.DISTANCE_MULTIPLIER, 51))
                .andReturn(25);

        PowerMock.replayAll();
        WhereFactoryNearestCaches.SearchDown search = new WhereFactoryNearestCaches.SearchDown(
                boundingBox, 0);
        assertEquals(1.0, search.search(1.0f, 50), 0.0);
        PowerMock.verifyAll();
    }

    @Test
    public void testBinarySearchDownMin() {
        BoundingBox boundingBox = PowerMock.createMock(BoundingBox.class);

        PowerMock.replayAll();
        WhereFactoryNearestCaches.SearchDown search = new WhereFactoryNearestCaches.SearchDown(
                boundingBox, 1.0f);
        assertEquals(1.0, search.search(1.0f, 50), 0.0);
        PowerMock.verifyAll();
    }

    @Test
    public void testSearchSearchDown() {
        WhereFactoryNearestCaches.SearchDown searchDown = PowerMock
                .createMock(WhereFactoryNearestCaches.SearchDown.class);
        BoundingBox boundingBox = PowerMock.createMock(BoundingBox.class);

        EasyMock.expect(boundingBox.getCount(2.0f, 51)).andReturn(51);
        EasyMock.expect(searchDown.search(2.0f, 50)).andReturn(1.2f);

        PowerMock.replayAll();
        assertEquals(1.2f, new WhereFactoryNearestCaches.Search(boundingBox, searchDown, null)
                .search(2.0f, 50), 0f);
        PowerMock.verifyAll();
    }

    @Test
    public void testGetWhere() {
        SearchFactory searchFactory = PowerMock.createMock(SearchFactory.class);
        ISQLiteDatabase sqliteWrapper = PowerMock.createMock(ISQLiteDatabase.class);
        Search search = PowerMock.createMock(Search.class);
        WhereStringFactory whereStringFactory = PowerMock.createMock(WhereStringFactory.class);

        EasyMock.expect(
                searchFactory.createSearch(122, 37, WhereFactoryNearestCaches.GUESS_MIN,
                        WhereFactoryNearestCaches.GUESS_MAX, sqliteWrapper)).andReturn(search);
        EasyMock.expect(search.search(0.1f, WhereFactoryNearestCaches.MAX_NUMBER_OF_CACHES))
                .andReturn(0.87f);
        EasyMock.expect(whereStringFactory.getWhereString(122, 37, 0.87f)).andReturn("WHERE foo");

        PowerMock.replayAll();
        assertEquals("WHERE foo", new WhereFactoryNearestCaches(searchFactory, whereStringFactory)
                .getWhere(sqliteWrapper, 122, 37));
        PowerMock.verifyAll();
    }

    @Test
    public void testSearchSearchUp() {
        WhereFactoryNearestCaches.SearchUp searchUp = PowerMock
                .createMock(WhereFactoryNearestCaches.SearchUp.class);
        BoundingBox boundingBox = PowerMock.createMock(BoundingBox.class);

        EasyMock.expect(boundingBox.getCount(2.0f, 51)).andReturn(25);
        EasyMock.expect(searchUp.search(2.0f, 50)).andReturn(17.0f);

        PowerMock.replayAll();
        assertEquals(17.0f, new WhereFactoryNearestCaches.Search(boundingBox, null, searchUp)
                .search(2.0f, 50), 0f);
        PowerMock.verifyAll();
    }

    @Test
    public void testBoundingBox() {
        ISQLiteDatabase sqliteWrapper = PowerMock.createMock(ISQLiteDatabase.class);
        WhereStringFactory whereStringFactory = PowerMock.createMock(WhereStringFactory.class);
        Cursor cursor = PowerMock.createMock(Cursor.class);

        PowerMock.mockStatic(Log.class);
        EasyMock.expect(Log.d((String)EasyMock.anyObject(), (String)EasyMock.anyObject()))
                .andReturn(0);
        EasyMock.expect(whereStringFactory.getWhereString(122, 37, 2.2f)).andReturn("WHERE blah");
        EasyMock.expect(
                sqliteWrapper.query(Database.TBL_CACHES, BoundingBox.ID_COLUMN, "WHERE blah", null,
                        null, null, "100")).andReturn(cursor);
        EasyMock.expect(cursor.getCount()).andReturn(27);
        cursor.close();

        PowerMock.replayAll();
        assertEquals(27, new BoundingBox(122, 37, sqliteWrapper, whereStringFactory).getCount(2.2f,
                100));
        PowerMock.verifyAll();
    }

    @Test
    public void testBinarySearchDownTwo() {
        BoundingBox boundingBox = PowerMock.createMock(BoundingBox.class);

        final float search1 = 8.0f / WhereFactoryNearestCaches.DISTANCE_MULTIPLIER;
        EasyMock.expect(boundingBox.getCount(search1, 51)).andReturn(50);
        final float search2 = search1 / WhereFactoryNearestCaches.DISTANCE_MULTIPLIER;
        EasyMock.expect(boundingBox.getCount(search2, 51)).andReturn(25);

        PowerMock.replayAll();
        WhereFactoryNearestCaches.SearchDown search = new WhereFactoryNearestCaches.SearchDown(
                boundingBox, 0);
        assertEquals(search1, search.search(8.0f, 50), 0.0);
        PowerMock.verifyAll();
    }
}
