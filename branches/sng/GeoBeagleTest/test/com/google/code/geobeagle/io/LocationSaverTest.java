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
import com.google.code.geobeagle.data.di.DestinationFactory;
import com.google.code.geobeagle.io.Database.CacheWriter;
import com.google.code.geobeagle.io.Database.SQLiteWrapper;

import junit.framework.TestCase;

public class LocationSaverTest extends TestCase {

    public void testSave() {
        Database database = createMock(Database.class);
        SQLiteWrapper sqliteWrapper = createMock(SQLiteWrapper.class);
        CacheWriter writer = createMock(CacheWriter.class);
        DestinationFactory destinationFactory = createMock(DestinationFactory.class);
        Destination destination = createMock(Destination.class);

        sqliteWrapper.openWritableDatabase(database);
        expect(database.createCacheWriter(sqliteWrapper)).andReturn(writer);
        writer.startWriting();
        expect(destinationFactory.create("122 32.3423 83 32.3221 (LB12345)"))
                .andReturn(destination);
        expect(destination.getFullId()).andReturn("LB12345");
        expect(destination.getName()).andReturn("");
        expect(destination.getLatitude()).andReturn(122.0);
        expect(destination.getLongitude()).andReturn(37.0);
        writer.insertAndUpdateCache("LB12345", "", 122, 37, "intent");
        writer.stopWriting();
        sqliteWrapper.close();

        replay(database);
        replay(sqliteWrapper);
        replay(writer);
        replay(destination);
        replay(destinationFactory);
        new LocationSaver(database, destinationFactory, null, sqliteWrapper)
                .saveLocation("122 32.3423 83 32.3221 (LB12345)");
        verify(database);
        verify(writer);
        verify(sqliteWrapper);
        verify(destinationFactory);
        verify(destination);
    }
}
