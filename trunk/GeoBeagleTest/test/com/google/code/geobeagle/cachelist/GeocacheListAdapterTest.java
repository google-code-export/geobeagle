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

import com.google.code.geobeagle.activity.cachelist.ActivityVisible;
import com.google.code.geobeagle.activity.cachelist.model.GeocacheVector;
import com.google.code.geobeagle.activity.cachelist.model.GeocacheVectors;
import com.google.code.geobeagle.activity.cachelist.presenter.GeocacheListAdapter;
import com.google.code.geobeagle.activity.cachelist.view.GeocacheSummaryRowInflater;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.graphics.Color;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        GeocacheListAdapter.class, TextView.class
})
public class GeocacheListAdapterTest {

    @Test
    public void testGetCount() {
        PowerMock.suppressConstructor(BaseAdapter.class);
        GeocacheVectors geocacheVectors = PowerMock.createMock(GeocacheVectors.class);

        EasyMock.expect(geocacheVectors.size()).andReturn(34);

        PowerMock.replayAll();
        assertEquals(34, new GeocacheListAdapter(geocacheVectors, null, null).getCount());
        PowerMock.verifyAll();
    }

    @Test
    public void testGetItem() {
        PowerMock.suppressConstructor(BaseAdapter.class);
        GeocacheVectors geocacheVectors = PowerMock.createMock(GeocacheVectors.class);

        PowerMock.replayAll();
        assertEquals(1763, new GeocacheListAdapter(geocacheVectors, null, null).getItem(1763));
        PowerMock.verifyAll();
    }

    @Test
    public void testGetItemId() {
        PowerMock.suppressConstructor(BaseAdapter.class);
        GeocacheVectors geocacheVectors = PowerMock.createMock(GeocacheVectors.class);

        PowerMock.replayAll();
        assertEquals(1763L, new GeocacheListAdapter(geocacheVectors, null, null).getItemId(1763));
        PowerMock.verifyAll();
    }

    @Test
    public void testGetView() {
        GeocacheSummaryRowInflater geocacheSummaryRowInflater = PowerMock
                .createMock(GeocacheSummaryRowInflater.class);
        PowerMock.suppressConstructor(BaseAdapter.class);
        View convertView = PowerMock.createMock(View.class);
        View newConvertView = PowerMock.createMock(View.class);
        GeocacheVector geocacheVector = PowerMock.createMock(GeocacheVector.class);
        GeocacheVectors geocacheVectors = PowerMock.createMock(GeocacheVectors.class);
        ActivityVisible activityVisible = PowerMock.createMock(ActivityVisible.class);

        EasyMock.expect(activityVisible.getVisible()).andReturn(true);
        EasyMock.expect(geocacheSummaryRowInflater.inflate(convertView)).andReturn(newConvertView);
        EasyMock.expect(geocacheVectors.get(17)).andReturn(geocacheVector).anyTimes();
        EasyMock.expect(geocacheVector.getId()).andReturn("GC123");
        newConvertView.setBackgroundColor(Color.TRANSPARENT);
        geocacheSummaryRowInflater.setData(newConvertView, geocacheVector);

        PowerMock.replayAll();
        assertEquals(newConvertView, new GeocacheListAdapter(geocacheVectors,
                geocacheSummaryRowInflater, activityVisible).getView(17, convertView, null));
        PowerMock.verifyAll();
    }
}
