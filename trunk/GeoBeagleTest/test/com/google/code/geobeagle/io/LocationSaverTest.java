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

import com.google.code.geobeagle.data.Destination;
import com.google.code.geobeagle.data.Destination.DestinationFactory;
import com.google.code.geobeagle.io.DatabaseFactory.CacheWriter;

import android.database.sqlite.SQLiteDatabase;

import junit.framework.TestCase;

public class LocationSaverTest extends TestCase {

    private DatabaseFactory mFactory;
    private SQLiteDatabase mSqlite;

    public void setUp() {
        mFactory = createMock(DatabaseFactory.class);
        mSqlite = createMock(SQLiteDatabase.class);
    }

    public void testSaveBookmarksOpenError() {
        expect(mFactory.openOrCreateCacheDatabase()).andReturn(null);

        replay(mFactory);
        LocationSaver locationSaver = new LocationSaver(mFactory, null, null);
        locationSaver.saveLocation(null);
        verify(mFactory);
    }

    public void testSave() {
        CacheWriter writer = createMock(CacheWriter.class);
        DestinationFactory destinationFactory = createMock(DestinationFactory.class);
        Destination destination = createMock(Destination.class);

        expect(mFactory.openOrCreateCacheDatabase()).andReturn(mSqlite);
        expect(mFactory.createCacheWriter(mSqlite, null)).andReturn(writer);
        writer.startWriting();
        expect(destinationFactory.create("122 32.3423 83 32.3221 (LB12345)"))
                .andReturn(destination);
        expect(destination.getFullId()).andReturn("LB12345");
        expect(destination.getName()).andReturn("");
        expect(destination.getLatitude()).andReturn(122.0);
        expect(destination.getLongitude()).andReturn(37.0);
        expect(writer.write("LB12345", "", 122, 37, "intent")).andReturn(true);
        writer.stopWriting();
        mSqlite.close();

        replay(mFactory);
        replay(mSqlite);
        replay(writer);
        replay(destination);
        replay(destinationFactory);
        new LocationSaver(mFactory, destinationFactory, null).saveLocation("122 32.3423 83 32.3221 (LB12345)");
        verify(mFactory);
        verify(writer);
        verify(mSqlite);
        verify(destinationFactory);
        verify(destination);
    }
}
