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

import com.google.code.geobeagle.CacheList;
import com.google.code.geobeagle.CacheListDelegate;
import com.google.code.geobeagle.R;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

public class CacheListDelegateTest extends TestCase {
    SimpleAdapter simpleAdapter = createMock(SimpleAdapter.class);

    public void testCreateSimpleAdapterList() {
        List<CharSequence> locations = new ArrayList<CharSequence>();
        locations.add("Paris");
        locations.add("Spain");

        List<Map<String, Object>> simpleAdapterData = CacheListDelegate
                .createSimpleAdapterData(locations);
        assertNotNull(simpleAdapterData);
        assertEquals(3, simpleAdapterData.size());
        assertEquals(CacheListDelegate.MY_CURRENT_LOCATION, simpleAdapterData.get(0).get("cache"));
        assertEquals("Spain", simpleAdapterData.get(1).get("cache"));
        assertEquals("Paris", simpleAdapterData.get(2).get("cache"));
    }

    public void testCreateSimpleAdapterListEmpty() {
        List<CharSequence> locations = new ArrayList<CharSequence>(0);
        List<Map<String, Object>> simpleAdapterData = CacheListDelegate
                .createSimpleAdapterData(locations);
        assertNotNull(simpleAdapterData);
        assertEquals(1, simpleAdapterData.size());
    }

    public void testOnCreate() {
        LocationBookmarks locationBookmarks = createMock(LocationBookmarks.class);
        ErrorDisplayer errorDisplayer = createMock(ErrorDisplayer.class);
        final ListActivity listActivity = createMock(ListActivity.class);
        final ArrayList<CharSequence> arrayList = new ArrayList<CharSequence>(0);

        listActivity.setContentView(R.layout.cache_list);

        replay(listActivity);
        replay(locationBookmarks);
        replay(errorDisplayer);
        replay(simpleAdapter);
        new CacheListDelegate(listActivity, arrayList, locationBookmarks, errorDisplayer)
                .onCreate();
        verify(listActivity);
        verify(errorDisplayer);
        verify(simpleAdapter);
        verify(locationBookmarks);
    }

    public void testOnListItemClick() {
        final ListActivity listActivity = createMock(ListActivity.class);
        final ArrayList<CharSequence> arrayList = new ArrayList<CharSequence>(0);
        final Intent intent = createMock(Intent.class);

        arrayList.add("England");
        arrayList.add("New York");
        arrayList.add("Paris"); 

        expect(intent.setAction(CacheList.SELECT_CACHE)).andReturn(intent);
        expect(intent.putExtra("location", (CharSequence)"Paris")).andReturn(intent);
        listActivity.startActivity(intent);

        replay(intent);
        replay(listActivity);
        replay(simpleAdapter);
        CacheListDelegate cacheListDelegate = new CacheListDelegate(listActivity,
                arrayList, null, null) {
            protected Intent createIntent(Context context, Class<?> cls) {
                return intent;
            }
        };
        cacheListDelegate.onListItemClick(null, null, 1, 0);
        verify(intent);
        verify(listActivity);
        verify(simpleAdapter);
    }

    public void testOnListItemClickMyLocation() {
        final ListActivity listActivity = createMock(ListActivity.class);
        final ArrayList<CharSequence> arrayList = new ArrayList<CharSequence>(0);
        final Intent intent = createMock(Intent.class);

        arrayList.add("Paris");

        expect(intent.setAction(CacheList.SELECT_CACHE)).andReturn(intent);
        expect(intent.putExtra("location", (CharSequence)null)).andReturn(intent);
        listActivity.startActivity(intent);

        replay(intent);
        replay(listActivity);
        replay(simpleAdapter);
        CacheListDelegate cacheListDelegate = new CacheListDelegate(listActivity,
                arrayList, null, null) {
            protected Intent createIntent(Context context, Class<?> cls) {
                return intent;
            }
        };
        cacheListDelegate.onListItemClick(null, null, 0, 0);
        verify(intent);
        verify(listActivity);
        verify(simpleAdapter);
    }

    public void testOnResume() {
        LocationBookmarks locationBookmarks = createMock(LocationBookmarks.class);
        ErrorDisplayer errorDisplayer = createMock(ErrorDisplayer.class);
        final ListActivity listActivity = createMock(ListActivity.class);
        final ArrayList<CharSequence> arrayList = new ArrayList<CharSequence>(0);

        locationBookmarks.onResume(null, errorDisplayer);
        listActivity.setListAdapter(simpleAdapter);

        replay(listActivity);
        replay(locationBookmarks);
        replay(errorDisplayer);
        replay(simpleAdapter);
        new CacheListDelegate(listActivity, arrayList, locationBookmarks,
                errorDisplayer) {
            protected SimpleAdapter createSimpleAdapter(Context context,
                    List<CharSequence> locations, int view_layout, String[] from, int[] to) {
                assertEquals(context, listActivity);
                assertEquals(locations, arrayList);
                assertEquals(view_layout, R.layout.cache_row);
                assertEquals(CacheListDelegate.ADAPTER_FROM, from);
                assertEquals(CacheListDelegate.ADAPTER_TO, to);
                return simpleAdapter;
            }
        }.onResume();
        verify(listActivity);
        verify(errorDisplayer);
        verify(simpleAdapter);
        verify(locationBookmarks);
    }
}
