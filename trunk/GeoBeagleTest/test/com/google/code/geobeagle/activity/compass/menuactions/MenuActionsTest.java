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

package com.google.code.geobeagle.activity.compass.menuactions;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.actions.MenuActionCacheList;
import com.google.code.geobeagle.actions.MenuActionEditGeocache;
import com.google.code.geobeagle.actions.MenuActionSearchOnline;
import com.google.code.geobeagle.actions.MenuActionSettings;
import com.google.code.geobeagle.activity.EditCacheActivity;
import com.google.code.geobeagle.activity.cachelist.CacheListActivity;
import com.google.code.geobeagle.activity.compass.CompassActivity;
import com.google.code.geobeagle.activity.preferences.EditPreferences;
import com.google.code.geobeagle.activity.searchonline.SearchOnlineActivity;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.app.Activity;
import android.content.Intent;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        MenuActionCacheList.class, MenuActionEditGeocache.class, MenuActionSettings.class,
        MenuActionSearchOnline.class, Activity.class
})
public class MenuActionsTest {

    @Test
    public void testMenuActionCacheList() throws Exception {
        Activity activity = PowerMock.createMock(Activity.class);
        Intent intent = PowerMock.createMock(Intent.class);

        PowerMock.expectNew(Intent.class, activity, CacheListActivity.class).andReturn(intent);
        activity.startActivity(intent);

        PowerMock.replayAll();
        new MenuActionCacheList(activity).act();
        PowerMock.verifyAll();
    }

    @Test
    public void testMenuActionSettings() throws Exception {
        Activity activity = PowerMock.createMock(Activity.class);
        Intent intent = PowerMock.createMock(Intent.class);

        PowerMock.expectNew(Intent.class, activity, EditPreferences.class).andReturn(intent);
        activity.startActivity(intent);

        PowerMock.replayAll();
        new MenuActionSettings(activity).act();
        PowerMock.verifyAll();
    }

    @Test
    public void testMenuActionSearchOnline() throws Exception {
        Activity activity = PowerMock.createMock(Activity.class);
        Intent intent = PowerMock.createMock(Intent.class);

        PowerMock.expectNew(Intent.class, activity, SearchOnlineActivity.class).andReturn(intent);
        activity.startActivity(intent);

        PowerMock.replayAll();
        new MenuActionSearchOnline(activity).act();
        PowerMock.verifyAll();
    }

    @Test
    public void testMenuActionEditGeocache() throws Exception {
        CompassActivity geobeagle = PowerMock.createMock(CompassActivity.class);
        Intent intent = PowerMock.createMock(Intent.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);

        PowerMock.expectNew(Intent.class, geobeagle, EditCacheActivity.class).andReturn(intent);
        EasyMock.expect(geobeagle.getGeocache()).andReturn(geocache);
        EasyMock.expect(intent.putExtra("geocache", geocache)).andReturn(intent);
        geobeagle.startActivityForResult(intent, 0);

        PowerMock.replayAll();
        new MenuActionEditGeocache(geobeagle).act();
        PowerMock.verifyAll();
    }
}
