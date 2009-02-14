/*
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
import com.google.code.geobeagle.io.LocationBookmarksSql;
import com.google.code.geobeagle.io.DatabaseFactory.CacheReader;
import com.google.code.geobeagle.io.DatabaseFactory.CacheWriter;

import android.database.sqlite.SQLiteDatabase;

import junit.framework.TestCase;

public class LocationBookmarksSqlTest extends TestCase {

    private DatabaseFactory mFactory;
    private SQLiteDatabase mSqlite;

    public void setUp() {
        mFactory = createMock(DatabaseFactory.class);
        mSqlite = createMock(SQLiteDatabase.class);
    }

    public void testReadBookmarksCursorOpenError() {
        CacheReader cacheReader = createMock(CacheReader.class);

        expect(mFactory.openCacheDatabase(null)).andReturn(mSqlite);
        expect(mFactory.createCacheReader(mSqlite)).andReturn(cacheReader);
        expect(cacheReader.open()).andReturn(false);
        mSqlite.close();

        replay(mFactory);
        replay(mSqlite);
        replay(cacheReader);
        LocationBookmarksSql locationBookmarksSql = new LocationBookmarksSql(null, mFactory, null);
        locationBookmarksSql.onResume(null, null);
        verify(mFactory);
        verify(mSqlite);
        verify(cacheReader);
    }

    public void testReadBookmarksDbOpenError() {
        expect(mFactory.openCacheDatabase(null)).andReturn(null);

        replay(mFactory);
        LocationBookmarksSql locationBookmarksSql = new LocationBookmarksSql(null, mFactory, null);
        locationBookmarksSql.onResume(null, null);
        verify(mFactory);
    }

    public void testReadBookmarksEmpty() {
        CacheReader cacheReader = createMock(CacheReader.class);

        expect(mFactory.openCacheDatabase(null)).andReturn(mSqlite);
        expect(mFactory.createCacheReader(mSqlite)).andReturn(cacheReader);
        expect(cacheReader.open()).andReturn(false);
        mSqlite.close();

        replay(mFactory);
        replay(mSqlite);
        replay(cacheReader);
        LocationBookmarksSql locationBookmarksSql = new LocationBookmarksSql(null, mFactory, null);
        locationBookmarksSql.onResume(null, null);
        verify(mFactory);
        verify(mSqlite);
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
        LocationBookmarksSql locationBookmarksSql = new LocationBookmarksSql(
                descriptionsAndLocations, null, null);
        locationBookmarksSql.readBookmarks(cacheReader);
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
        LocationBookmarksSql locationBookmarksSql = new LocationBookmarksSql(
                descriptionsAndLocations, null, null);
        locationBookmarksSql.readBookmarks(cacheReader);
        verify(cacheReader);
        verify(descriptionsAndLocations);
    }

    public void testSaveBookmarksEmpty() {
        expect(mFactory.openOrCreateCacheDatabase(null)).andReturn(mSqlite);
        expect(mFactory.createCacheWriter(mSqlite)).andReturn(null);

        replay(mFactory);
        LocationBookmarksSql locationBookmarksSql = new LocationBookmarksSql(
                new DescriptionsAndLocations(), mFactory, null);
        locationBookmarksSql.onPause(null);
        verify(mFactory);
    }

    public void testSaveBookmarksOpenError() {
        expect(mFactory.openOrCreateCacheDatabase(null)).andReturn(null);

        replay(mFactory);
        LocationBookmarksSql locationBookmarksSql = new LocationBookmarksSql(
                new DescriptionsAndLocations(), mFactory, null);
        locationBookmarksSql.onPause(null);
        verify(mFactory);
    }

    public void testSaveBookmarksWriteError() {
        CacheWriter writer = createMock(CacheWriter.class);

        expect(mFactory.openOrCreateCacheDatabase(null)).andReturn(mSqlite);
        expect(mFactory.createCacheWriter(mSqlite)).andReturn(writer);
        expect(writer.write("122 32.3423 83 32.3221 (LB12345)")).andReturn(false);
        mSqlite.close();

        replay(mFactory);
        replay(writer);
        replay(mSqlite);
        DescriptionsAndLocations descriptionsAndLocations = new DescriptionsAndLocations();
        descriptionsAndLocations.add("LB12345", "122 32.3423 83 32.3221 (LB54321)");
        descriptionsAndLocations.add("LB12345", "122 32.3423 83 32.3221 (LB12345)");
        LocationBookmarksSql locationBookmarksSql = new LocationBookmarksSql(
                descriptionsAndLocations, mFactory, null);
        locationBookmarksSql.onPause(null);
        verify(mFactory);
        verify(writer);
        verify(mSqlite);
    }

    public void testSaveBookmarksTwo() {
        CacheWriter writer = createMock(CacheWriter.class);

        expect(mFactory.openOrCreateCacheDatabase(null)).andReturn(mSqlite);
        expect(mFactory.createCacheWriter(mSqlite)).andReturn(writer);
        expect(writer.write("122 32.3423 83 32.3221 (LB12345)")).andReturn(true);
        expect(writer.write("122 32.3423 83 32.3221 (LB54321)")).andReturn(true);
        mSqlite.close();

        replay(mFactory);
        replay(writer);
        replay(mSqlite);
        DescriptionsAndLocations descriptionsAndLocations = new DescriptionsAndLocations();
        descriptionsAndLocations.add("LB12345", "122 32.3423 83 32.3221 (LB12345)");
        descriptionsAndLocations.add("LB54321", "122 32.3423 83 32.3221 (LB54321)");
        LocationBookmarksSql locationBookmarksSql = new LocationBookmarksSql(
                descriptionsAndLocations, mFactory, null);
        locationBookmarksSql.onPause(null);
        verify(mFactory);
        verify(writer);
        verify(mSqlite);
    }
}
