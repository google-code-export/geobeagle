
package com.google.code.geobeagle.ui.cachelist;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import com.google.code.geobeagle.data.GeocacheVector;
import com.google.code.geobeagle.data.GeocacheVectors;
import com.google.code.geobeagle.io.CacheWriter;
import com.google.code.geobeagle.io.Database;
import com.google.code.geobeagle.io.DatabaseDI.SQLiteWrapper;

import org.junit.Test;

public class ContextActionDeleteTest {

    @Test
    public void testActionDelete() {
        Database database = createMock(Database.class);
        CacheWriter cacheWriter = createMock(CacheWriter.class);
        SQLiteWrapper sqliteWrapper = createMock(SQLiteWrapper.class);
        GeocacheListAdapter geocacheListAdapter = createMock(GeocacheListAdapter.class);
        GeocacheVectors geocacheVectors = createMock(GeocacheVectors.class);
        GeocacheVector geocacheVector = createMock(GeocacheVector.class);

        sqliteWrapper.openWritableDatabase(database);
        geocacheVectors.remove(17);
        expect(geocacheVectors.get(17)).andReturn(geocacheVector);
        expect(geocacheVector.getId()).andReturn("GC123");
        cacheWriter.deleteCache("GC123");
        sqliteWrapper.close();
        geocacheListAdapter.notifyDataSetChanged();

        replay(geocacheVector);
        replay(geocacheVectors);
        replay(geocacheListAdapter);
        replay(sqliteWrapper);
        replay(cacheWriter);
        replay(database);
        new ContextActionDelete(database, geocacheListAdapter, sqliteWrapper, cacheWriter,
                geocacheVectors, null).act(17);
        verify(sqliteWrapper);
        verify(cacheWriter);
        verify(database);
        verify(geocacheListAdapter);
        verify(geocacheVectors);
        verify(geocacheVector);
    }

}
