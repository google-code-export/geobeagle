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

import com.google.code.geobeagle.data.Geocache;
import com.google.code.geobeagle.io.di.DatabaseDI;

import junit.framework.TestCase;

public class LocationSaverTest extends TestCase {

    public void testSave() {
        Database database = createMock(Database.class);
        DatabaseDI.SQLiteWrapper sqliteWrapper = createMock(DatabaseDI.SQLiteWrapper.class);
        CacheWriter writer = createMock(CacheWriter.class);
        Geocache geocache = createMock(Geocache.class);

        sqliteWrapper.openWritableDatabase(database);
        writer.startWriting();
        expect(geocache.getId()).andReturn("LB12345");
        expect(geocache.getName()).andReturn("");
        expect(geocache.getLatitude()).andReturn(122.0);
        expect(geocache.getLongitude()).andReturn(37.0);
        writer.insertAndUpdateCache("LB12345", "", 122, 37, "intent");
        writer.stopWriting();
        sqliteWrapper.close();

        replay(database);
        replay(sqliteWrapper);
        replay(writer);
        replay(geocache);
        new LocationSaver(database, sqliteWrapper, writer).saveLocation(geocache);
        verify(database);
        verify(writer);
        verify(sqliteWrapper);
        verify(geocache);
    }
}
