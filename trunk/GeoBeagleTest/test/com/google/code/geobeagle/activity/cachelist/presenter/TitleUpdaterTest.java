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

package com.google.code.geobeagle.activity.cachelist.presenter;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.Timing;
import com.google.code.geobeagle.activity.cachelist.SearchTarget;
import com.google.code.geobeagle.database.filter.FilterNearestCaches;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.app.ListActivity;
import android.widget.TextView;

@PrepareForTest({
        ListActivity.class, TextView.class
})
@RunWith(PowerMockRunner.class)
public class TitleUpdaterTest {

    @Test
    public void testTitleUpdater() {
        ListActivity listActivity = PowerMock.createMock(ListActivity.class);
        FilterNearestCaches filterNearestCaches = PowerMock.createMock(FilterNearestCaches.class);
        Timing timing = PowerMock.createMock(Timing.class);
        SearchTarget searchTarget = PowerMock.createMock(SearchTarget.class);

        timing.lap(EasyMock.isA(String.class));
        EasyMock.expectLastCall().anyTimes();

        EasyMock.expect(searchTarget.getTitle()).andReturn("Searching: foo; ");
        EasyMock.expect(filterNearestCaches.getTitleText()).andReturn(R.string.cache_list_title);
        EasyMock.expect(listActivity.getString(R.string.cache_list_title, 5, 12)).andReturn(
                "new title");
        listActivity.setTitle("Searching: foo; new title");

        PowerMock.replayAll();
        new TitleUpdater(listActivity, filterNearestCaches, timing, searchTarget).update(12, 5);
        PowerMock.verifyAll();
    }

    @Test
    public void testTitleUpdaterEmpty() {
        ListActivity listActivity = PowerMock.createMock(ListActivity.class);
        FilterNearestCaches filterNearestCaches = PowerMock.createMock(FilterNearestCaches.class);
        Timing timing = PowerMock.createMock(Timing.class);
        SearchTarget searchTarget = PowerMock.createMock(SearchTarget.class);

        timing.lap(EasyMock.isA(String.class));
        EasyMock.expectLastCall().anyTimes();
        EasyMock.expect(searchTarget.getTitle()).andReturn("Searching: foo; ");

        EasyMock.expect(filterNearestCaches.getTitleText()).andReturn(R.string.cache_list_title);
        listActivity.setTitle("Searching: foo; new title");
        EasyMock.expect(listActivity.getString(R.string.cache_list_title, 0, 12)).andReturn(
                "new title");

        PowerMock.replayAll();
        new TitleUpdater(listActivity, filterNearestCaches, timing, searchTarget).update(12, 0);
        PowerMock.verifyAll();
    }
}
