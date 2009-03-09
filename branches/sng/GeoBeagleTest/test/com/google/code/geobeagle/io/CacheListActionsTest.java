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

import com.google.code.geobeagle.CacheListActions;
import com.google.code.geobeagle.data.CacheListData;
import com.google.code.geobeagle.io.Database.CacheWriter;
import com.google.code.geobeagle.io.Database.SQLiteWrapper;
import com.google.code.geobeagle.ui.CacheListDelegate;

import android.content.Context;
import android.content.Intent;
import android.widget.SimpleAdapter;

import junit.framework.TestCase;

public class CacheListActionsTest extends TestCase {

    public void testActionDelete() {
        CacheListData cacheListData = createMock(CacheListData.class);
        Database database = createMock(Database.class);
        CacheWriter cacheWriter = createMock(CacheWriter.class);
        SQLiteWrapper sqliteWrapper = createMock(SQLiteWrapper.class);
        SimpleAdapter simpleAdapter = createMock(SimpleAdapter.class);
        
        sqliteWrapper.openWritableDatabase(database);
        expect(database.createCacheWriter(sqliteWrapper)).andReturn(cacheWriter);
        cacheListData.delete(17);
        expect(cacheListData.getId(17)).andReturn("GC123");
        cacheWriter.deleteCache("GC123");
        sqliteWrapper.close();
        simpleAdapter.notifyDataSetChanged();

        replay(simpleAdapter);
        replay(sqliteWrapper);
        replay(cacheListData);
        replay(cacheWriter);
        replay(database);
        CacheListActions.Action action = new CacheListActions.Delete(database, sqliteWrapper, cacheListData, null);
        action.act(17, simpleAdapter);
        verify(sqliteWrapper);
        verify(cacheListData);
        verify(cacheWriter);
        verify(database);
        verify(simpleAdapter);
    }

    public void testActionView() {
        CacheListData cacheListData = createMock(CacheListData.class);
        Intent intent = createMock(Intent.class);
        Context context = createMock(Context.class);

        expect(cacheListData.getCoordinatesIdAndName(34)).andReturn("a cache");
        expect(intent.setAction(CacheListDelegate.SELECT_CACHE)).andReturn(intent);
        expect(intent.putExtra("location", (CharSequence)"a cache")).andReturn(intent);
        context.startActivity(intent);

        replay(cacheListData);
        replay(context);
        replay(intent);
        CacheListActions.Action action = new CacheListActions.View(cacheListData, context, intent);
        action.act(34, null);
        verify(cacheListData);
        verify(context);
        verify(intent);
    }

}
