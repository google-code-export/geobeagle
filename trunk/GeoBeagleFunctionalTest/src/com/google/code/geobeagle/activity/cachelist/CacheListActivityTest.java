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

package com.google.code.geobeagle.activity.cachelist;

import com.google.code.geobeagle.R;

import org.easymock.EasyMock;

import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;
import android.view.MenuItem;

public class CacheListActivityTest extends
        ActivityInstrumentationTestCase2<CacheListActivity> {

    private CacheListActivity cacheListActivity;
    private Instrumentation instrumentation;
    private MenuItem item;

    public CacheListActivityTest() {
        super("com.google.code.geobeagle", CacheListActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        instrumentation = getInstrumentation();
        item = EasyMock.createMock(MenuItem.class);
        cacheListActivity = getActivity();
    }

    public void testAddMyLocation() throws InterruptedException {
        EasyMock.expect(item.getItemId()).andReturn(
                R.string.menu_add_my_location);

        EasyMock.replay(item);
        cacheListActivity.onOptionsItemSelected(item);
        EasyMock.verify(item);

        instrumentation.waitForIdleSync();
        Thread.sleep(2000);

        instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
    }

    public void testSearchOnline() throws InterruptedException {
        EasyMock.expect(item.getItemId())
                .andReturn(R.string.menu_search_online);

        EasyMock.replay(item);
        cacheListActivity.onOptionsItemSelected(item);
        EasyMock.verify(item);

        instrumentation.waitForIdleSync();
        Thread.sleep(2000);

        instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
    }

    public void testShowMap() throws InterruptedException {
        EasyMock.expect(item.getItemId()).andReturn(R.string.menu_map);

        EasyMock.replay(item);
        cacheListActivity.onOptionsItemSelected(item);
        EasyMock.verify(item);

        instrumentation.waitForIdleSync();
        Thread.sleep(2000);

        instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
    }

    public void testShowAllCaches() throws InterruptedException {
        instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);

        instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_DOWN);
        instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_LEFT);
        instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_ENTER);
        Thread.sleep(2000);
    }

    public void testSync() {
        EasyMock.expect(item.getItemId()).andReturn(R.string.menu_sync);

        EasyMock.replay(item);
        cacheListActivity.onOptionsItemSelected(item);
        EasyMock.verify(item);
        instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
        instrumentation.waitForIdleSync();
    }
}
