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
import com.google.code.geobeagle.database.CachesProviderToggler;
import com.google.code.geobeagle.database.DbFrontend;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.app.ListActivity;
import android.widget.TextView;

@PrepareForTest( {
        ListActivity.class, TextView.class

})
@RunWith(PowerMockRunner.class)
public class TitleUpdaterTest {

    @Test
    public void testTitleUpdater() {
        ListActivity listActivity = PowerMock.createMock(ListActivity.class);
        CachesProviderToggler cachesProviderToggler = PowerMock
                .createMock(CachesProviderToggler.class);
        DbFrontend dbFrontend = PowerMock.createMock(DbFrontend.class);

        EasyMock.expect(dbFrontend.count(null)).andReturn(12);
        EasyMock.expect(cachesProviderToggler.getCount()).andReturn(5);

        EasyMock.expect(cachesProviderToggler.isShowingNearest()).andReturn(true);
        // EasyMock.expect(filterNearestCaches.getTitleText()).andReturn(R.string.cache_list_title);
        EasyMock.expect(listActivity.getString(R.string.cache_list_title, 5, 12)).andReturn(
                "new title");
        listActivity.setTitle("new title");

        PowerMock.replayAll();
        new TitleUpdater(listActivity, cachesProviderToggler, dbFrontend).refresh();
        PowerMock.verifyAll();
    }

    @Test
    public void testTitleUpdaterEmpty() {
        ListActivity listActivity = PowerMock.createMock(ListActivity.class);
        TextView textView = PowerMock.createMock(TextView.class);
        CachesProviderToggler cachesProviderToggler = PowerMock
                .createMock(CachesProviderToggler.class);
        DbFrontend dbFrontend = PowerMock.createMock(DbFrontend.class);

        EasyMock.expect(dbFrontend.count(null)).andReturn(12);
        EasyMock.expect(cachesProviderToggler.getCount()).andReturn(0);

        EasyMock.expect(cachesProviderToggler.isShowingNearest()).andReturn(true);
        EasyMock.expect(listActivity.getString(R.string.cache_list_title, 0, 12)).andReturn(
                "new title");
        listActivity.setTitle("new title");
        EasyMock.expect(listActivity.findViewById(android.R.id.empty)).andReturn(textView);
        textView.setText(R.string.no_nearby_caches);

        PowerMock.replayAll();
        new TitleUpdater(listActivity, cachesProviderToggler, dbFrontend).refresh();
        PowerMock.verifyAll();
    }
}
