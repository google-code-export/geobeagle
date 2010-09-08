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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.inject.Provider;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)

public class TagWriterImplTest {

    @SuppressWarnings("unchecked")
    @Test
    public void testTags() {
        Provider<ISQLiteDatabase> databaseProvider = PowerMock.createMock(Provider.class);

        DesktopSQLiteDatabase db = new DesktopSQLiteDatabase();
        EasyMock.expect(databaseProvider.get()).andReturn(db).anyTimes();

        PowerMock.replayAll();
        db.execSQL(DatabaseTest.currentSchema());
        final TagWriterImpl tagWriterImpl = new TagWriterImpl(databaseProvider, null);
        assertFalse(tagWriterImpl.hasTag("GC123", Tag.FOUND));
        assertFalse(tagWriterImpl.hasTag("GC123", Tag.DNF));
        tagWriterImpl.add("GC123", Tag.FOUND);
        tagWriterImpl.add("GCabc", Tag.FOUND);
        assertTrue(tagWriterImpl.hasTag("GC123", Tag.FOUND));
        assertFalse(tagWriterImpl.hasTag("GC123", Tag.DNF));
        tagWriterImpl.add("GC123", Tag.DNF);
        assertTrue(tagWriterImpl.hasTag("GC123", Tag.DNF));
        assertFalse(tagWriterImpl.hasTag("GC123", Tag.FOUND));
    }

}
