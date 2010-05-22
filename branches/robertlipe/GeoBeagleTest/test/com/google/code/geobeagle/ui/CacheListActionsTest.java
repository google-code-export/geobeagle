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

package com.google.code.geobeagle.ui;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import com.google.code.geobeagle.data.Geocache;
import com.google.code.geobeagle.data.GeocacheVector;
import com.google.code.geobeagle.data.GeocacheVectors;
import com.google.code.geobeagle.io.CacheWriter;
import com.google.code.geobeagle.io.Database;
import com.google.code.geobeagle.io.DatabaseDI.SQLiteWrapper;

import android.content.Context;
import android.content.Intent;

import junit.framework.TestCase;

public class CacheListActionsTest extends TestCase {

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
        Action action = new DeleteAction(database, sqliteWrapper, cacheWriter, geocacheVectors,
                null);
        action.act(17, geocacheListAdapter);
        verify(sqliteWrapper);
        verify(cacheWriter);
        verify(database);
        verify(geocacheListAdapter);
        verify(geocacheVectors);
        verify(geocacheVector);
    }

    public void testActionView() {
        Intent intent = createMock(Intent.class);
        Context context = createMock(Context.class);
        GeocacheVectors geocacheVectors = createMock(GeocacheVectors.class);
        GeocacheVector geocacheVector = createMock(GeocacheVector.class);
        Geocache geocache = createMock(Geocache.class);

        expect(geocacheVectors.get(34)).andReturn(geocacheVector);
        expect(geocacheVector.getGeocache()).andReturn(geocache);
        expect(intent.setAction(CacheListDelegate.SELECT_CACHE)).andReturn(intent);
        expect(intent.putExtra("geocache", geocache)).andReturn(intent);
        context.startActivity(intent);

        replay(geocacheVectors);
        replay(geocacheVector);
        replay(context);
        replay(intent);
        Action action = new ViewAction(geocacheVectors, context, intent);
        action.act(34, null);
        verify(context);
        verify(intent);
        verify(geocacheVectors);
        verify(geocacheVector);
    }

}
