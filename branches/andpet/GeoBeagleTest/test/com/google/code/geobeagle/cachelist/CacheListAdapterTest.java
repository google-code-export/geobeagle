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

package com.google.code.geobeagle.cachelist;

import static org.junit.Assert.assertEquals;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.GeocacheList;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListAdapter;
import com.google.code.geobeagle.activity.cachelist.presenter.GeocacheSummaryRowInflater;
import com.google.code.geobeagle.database.DistanceAndBearing;
import com.google.code.geobeagle.database.DistanceAndBearing.IDistanceAndBearingProvider;

import org.easymock.classextension.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        CacheListAdapter.class, TextView.class
})
public class CacheListAdapterTest {

    @Test
    public void testGetCount() {
        PowerMock.suppressConstructor(BaseAdapter.class);
        GeocacheList geocacheList = PowerMock.createMock(GeocacheList.class);
        
        EasyMock.expect(geocacheList.size()).andReturn(34);

        PowerMock.replayAll();
        assertEquals(34, new CacheListAdapter(null, null, null, null, geocacheList).getCount());
        PowerMock.verifyAll();
    }

    @Test
    public void testGetItem() {
        PowerMock.suppressConstructor(BaseAdapter.class);

        PowerMock.replayAll();
        assertEquals(1763, new CacheListAdapter(null, null, null, null, null).getItem(1763));
        PowerMock.verifyAll();
    }

    @Test
    public void testGetItemId() {
        PowerMock.suppressConstructor(BaseAdapter.class);

        PowerMock.replayAll();
        assertEquals(1763L, new CacheListAdapter(null, null, null, null, null).getItemId(1763));
        PowerMock.verifyAll();
    }

    @Test
    public void testGetView() {
        GeocacheSummaryRowInflater geocacheSummaryRowInflater = PowerMock
                .createMock(GeocacheSummaryRowInflater.class);
        PowerMock.suppressConstructor(BaseAdapter.class);
        View convertView = PowerMock.createMock(View.class);
        View newConvertView = PowerMock.createMock(View.class);
        DistanceAndBearing distanceAndBearing = PowerMock.createMock(DistanceAndBearing.class);
        GeocacheList geocacheList = PowerMock.createMock(GeocacheList.class);
        Geocache geocache= PowerMock.createMock(Geocache.class);
        IDistanceAndBearingProvider distanceAndBearingProvider = PowerMock.createMock(IDistanceAndBearingProvider.class);
        
        EasyMock.expect(geocacheSummaryRowInflater.inflate(convertView)).andReturn(newConvertView);
        EasyMock.expect(geocacheList.get(17)).andReturn(geocache);
        EasyMock.expect(distanceAndBearingProvider.getDistanceAndBearing(geocache)).andReturn(distanceAndBearing);
        geocacheSummaryRowInflater.setData(newConvertView, distanceAndBearing, 0);

        PowerMock.replayAll();
        assertEquals(newConvertView, new CacheListAdapter(null, distanceAndBearingProvider, geocacheSummaryRowInflater, null, geocacheList)
                .getView(17, convertView, null));
        PowerMock.verifyAll();
    }
}
