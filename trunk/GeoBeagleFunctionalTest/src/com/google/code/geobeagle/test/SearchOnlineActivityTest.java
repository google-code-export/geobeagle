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

package com.google.code.geobeagle.test;

import com.google.code.geobeagle.activity.searchonline.SearchOnlineActivity;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

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
        Log.d("Test", "completed ok!!!!!!!!!");
    }
    
    public void testFoo() {
        
    }
}
