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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.suppressConstructor;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.cachelist.GeoBeagleTest;
import com.google.code.geobeagle.database.filter.Filter;
import com.google.inject.Provider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

@PrepareForTest({
        Log.class, TagStore.class, ContentValues.class, Toast.class
})
@RunWith(PowerMockRunner.class)
public class TagWriterImplTest extends GeoBeagleTest {

    private Provider<ISQLiteDatabase> databaseProvider;
    private DesktopSQLiteDatabase db;
    private Filter filter;
    private ContentValues contentValues;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        databaseProvider = createMock(Provider.class);
        filter = createMock(Filter.class);
        db = new DesktopSQLiteDatabase();
        db.execSQL(DatabaseTest.currentSchema());
        contentValues = createMock(ContentValues.class);
        suppressConstructor(ContentValues.class);
        contentValues.put("Visible", 0);
        PowerMock.expectNew(ContentValues.class).andReturn(contentValues);

    }

    @Test
    public void testAddDnf() {
        expect(databaseProvider.get()).andReturn(db).anyTimes();
        expect(filter.showBasedOnFoundState(true)).andReturn(true).anyTimes();
        expect(filter.showBasedOnFoundState(false)).andReturn(true).anyTimes();
        replayAll();

        TagStore tagStore = new TagStore(databaseProvider);
        TagWriter tagWriter = new TagWriter(filter, tagStore, null);
        TagReader tagReader = new TagReader(tagStore);
        tagWriter.add("GC123", Tag.DNF, false);
        assertTrue(tagReader.hasTag("GC123", Tag.DNF));
        assertFalse(tagReader.hasTag("GC123", Tag.FOUND));
        verifyAll();
    }

    @Test
    public void testAddMakeInvisible() throws Exception {
        Context context = PowerMock.createMock(Context.class);
        Toast toast = PowerMock.createMock(Toast.class);

        expect(databaseProvider.get()).andReturn(db).anyTimes();
        expect(filter.showBasedOnFoundState(true)).andReturn(false).anyTimes();
        PowerMock.mockStatic(Toast.class);
        expect(Toast.makeText(context, R.string.removing_found_cache_from_cache_list,
                        Toast.LENGTH_LONG)).andReturn(toast);
        toast.show();
        replayAll();

        TagWriter tagWriter = new TagWriter(filter, new TagStore(databaseProvider), context);
        tagWriter.add("GC123", Tag.FOUND, true);
        verifyAll();
    }

    @Test
    public void testAddFound() {
        expect(databaseProvider.get()).andReturn(db).anyTimes();
        expect(filter.showBasedOnFoundState(true)).andReturn(true).anyTimes();
        expect(filter.showBasedOnFoundState(false)).andReturn(true).anyTimes();
        replayAll();

        TagStore tagStore = new TagStore(databaseProvider);
        TagWriter tagWriter = new TagWriter(filter, tagStore, null);
        TagReader tagReader = new TagReader(tagStore);
        tagWriter.add("GC123", Tag.FOUND, false);
        tagWriter.add("GCabc", Tag.FOUND, false);
        assertTrue(tagReader.hasTag("GC123", Tag.FOUND));
        assertFalse(tagReader.hasTag("GC123", Tag.DNF));
        verifyAll();
    }

    @Test
    public void testNoTagsOnStartUp() {
        expect(databaseProvider.get()).andReturn(db).anyTimes();
        expect(filter.showBasedOnFoundState(true)).andReturn(true).anyTimes();
        expect(filter.showBasedOnFoundState(false)).andReturn(true).anyTimes();

        replayAll();
        TagReader tagReader = new TagReader(new TagStore(databaseProvider));
        assertFalse(tagReader.hasTag("GC123", Tag.FOUND));
        assertFalse(tagReader.hasTag("GC123", Tag.DNF));
        verifyAll();
    }
}
