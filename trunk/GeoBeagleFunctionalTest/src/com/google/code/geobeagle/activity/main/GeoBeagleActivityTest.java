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

package com.google.code.geobeagle.activity.main;

import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import android.view.KeyEvent;

public class GeoBeagleActivityTest extends
        ActivityInstrumentationTestCase2<GeoBeagle> {

    private GeoBeagle geoBeagle;
    private Instrumentation instrumentation;

    public GeoBeagleActivityTest() {
        super("com.google.code.geobeagle", GeoBeagle.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        geoBeagle = getActivity();
        instrumentation = getInstrumentation();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
         getActivity().finish();
    }

    @Smoke
    public void testCreate() {
        assertNotNull(geoBeagle.locationControlBuffered);
        assertNotNull(geoBeagle.activitySaver);
        getInstrumentation().waitForIdleSync();
    }

    @Smoke
    public void testMenuSettings() throws InterruptedException {
        instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);

        instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_DOWN);
        instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_DOWN);
        instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_RIGHT);

        instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_ENTER);
        Thread.sleep(2000);
        instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
    }

    public void testMenuCacheList() throws InterruptedException {

        instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);

        instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_UP);

        instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_ENTER);
        Thread.sleep(2000);
        instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
    }
}
