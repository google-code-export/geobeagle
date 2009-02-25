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

import com.google.code.geobeagle.LocationControl;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.data.CacheListData;
import com.google.code.geobeagle.io.LocationBookmarksSql;
import com.google.code.geobeagle.ui.CacheListDelegate.SimpleAdapterFactory;

import android.app.ListActivity;
import android.content.Intent;
import android.location.Location;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.Map;

import junit.framework.TestCase;

public class CacheListDelegateTest extends TestCase {
    ListActivity listActivity = createMock(ListActivity.class);
    SimpleAdapter simpleAdapter = createMock(SimpleAdapter.class);

    public void testOnCreate() {
        listActivity.setContentView(R.layout.cache_list);

        replay(listActivity);
        new CacheListDelegate(listActivity, null, null, null, null, null, null, null).onCreate();
        verify(listActivity);
    }

    public void testOnListItemClick() {
        Intent intent = createMock(Intent.class);
        CacheListData cacheListData = createMock(CacheListData.class);
        SimpleAdapterFactory simpleAdapterFactory = createMock(SimpleAdapterFactory.class);

        expect(cacheListData.getLocation(12)).andReturn("a cache");
        expect(intent.setAction(CacheListDelegate.SELECT_CACHE)).andReturn(intent);
        expect(intent.putExtra("location", (CharSequence)"a cache")).andReturn(intent);
        listActivity.startActivity(intent);

        replay(simpleAdapterFactory);
        replay(intent);
        replay(listActivity);
        replay(cacheListData);
        CacheListDelegate cacheListDelegate = new CacheListDelegate(listActivity, null, null,
                simpleAdapterFactory, cacheListData, intent, null, null);
        cacheListDelegate.onListItemClick(null, null, 12, 0);
        verify(simpleAdapterFactory);
        verify(intent);
        verify(listActivity);
        verify(cacheListData);
    }

    public void testOnResume() {
        SimpleAdapterFactory simpleAdapterFactory = createMock(SimpleAdapterFactory.class);
        LocationBookmarksSql locationBookmarks = createMock(LocationBookmarksSql.class);
        CacheListData cacheListData = createMock(CacheListData.class);
        LocationControl locationControl = createMock(LocationControl.class);
        Location here = createMock(Location.class);
        ArrayList<CharSequence> locations = new ArrayList<CharSequence>(0);
        ArrayList<Map<String, Object>> adapterData = new ArrayList<Map<String, Object>>(0);

        locationBookmarks.onResume(null);
        expect(locationBookmarks.getLocations()).andReturn(locations);
        expect(locationControl.getLocation()).andReturn(here);
        cacheListData.add(locations, here);
        expect(cacheListData.getAdapterData()).andReturn(adapterData);
        expect(
                simpleAdapterFactory.createSimpleAdapter(listActivity, adapterData,
                        R.layout.cache_row, CacheListDelegate.ADAPTER_FROM,
                        CacheListDelegate.ADAPTER_TO)).andReturn(simpleAdapter);
        listActivity.setListAdapter(simpleAdapter);

        replay(simpleAdapterFactory);
        replay(locationBookmarks);
        replay(cacheListData);
        replay(locationControl);
        new CacheListDelegate(listActivity, locationBookmarks, locationControl,
                simpleAdapterFactory, cacheListData, null, null, null).onResume();
        verify(simpleAdapterFactory);
        verify(locationBookmarks);
        verify(cacheListData);
        verify(locationControl);
    }
}
