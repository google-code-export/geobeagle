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

package com.google.code.geobeagle.activity.searchonline;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.ActivityRestorer;
import com.google.code.geobeagle.activity.MenuActionStub;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;

public class SearchOnlineActivityTest extends
        ActivityInstrumentationTestCase2<SearchOnlineActivity> {

    public SearchOnlineActivityTest() {
        super("com.google.code.geobeagle", SearchOnlineActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testCreate() {
        final SearchOnlineActivity searchOnlineActivity = getActivity();
        assertNotNull(searchOnlineActivity.getDistanceFormatterManager());
        assertNotNull(searchOnlineActivity.getGpsWidgetAndUpdater());
        assertNotNull(searchOnlineActivity.getCombinedLocationListener());
        assertNotNull(searchOnlineActivity.getHelpContentsView());
        assertEquals(R.id.help_contents, searchOnlineActivity
                .getHelpContentsView().getId());
        ActivityRestorer activityRestorer = searchOnlineActivity
                .getActivityRestorer();
        assertNotNull(activityRestorer);
        assertEquals(searchOnlineActivity.getBaseContext()
                .getSharedPreferences("GeoBeagle", Context.MODE_PRIVATE),
                activityRestorer.getSharedPreferences());
        assertNotNull(searchOnlineActivity.getJsInterface());

        final MenuActionStub menuActionStub = new MenuActionStub();
        getInstrumentation()
                .addMonitor(searchOnlineActivity.getClass().getCanonicalName(),
                        null, false);
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                searchOnlineActivity.onOptionsItemSelected(menuActionStub);
            }
        });
        getInstrumentation().waitForIdleSync();
    }
}
