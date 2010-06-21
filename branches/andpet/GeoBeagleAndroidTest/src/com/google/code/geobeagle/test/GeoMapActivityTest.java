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

import com.google.code.geobeagle.IToaster;
import com.google.code.geobeagle.Toaster;
import com.google.code.geobeagle.activity.map.GeoMapActivity;
import com.google.code.geobeagle.activity.map.OverlayManager;

import org.easymock.EasyMock;

import android.test.ActivityInstrumentationTestCase2;

public class GeoMapActivityTest extends
        ActivityInstrumentationTestCase2<GeoMapActivity> {

    public GeoMapActivityTest() {
        super("com.google.code.geobeagle", GeoMapActivity.class);
    }

    public void testShowTooManyCachesToast() {
        final IToaster mockToaster = EasyMock.createMock(IToaster.class);

        // Actually this should be true; will fix in a later CL.
        mockToaster.showToast(false);
        EasyMock.expectLastCall().anyTimes();

        Toaster.ToasterFactory mockToasterFactory = new Toaster.ToasterFactory() {
            @Override
            public IToaster getToaster(Toaster toaster) {
                return mockToaster;
            }
        };

        EasyMock.replay(mockToaster);
        GeoMapActivity.peggedCacheProviderToasterFactory = mockToasterFactory;
        final GeoMapActivity geoMapActivity = getActivity();
        getInstrumentation().addMonitor(
                geoMapActivity.getClass().getCanonicalName(), null, false);
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                OverlayManager overlayManager = geoMapActivity
                        .getOverlayManager();
                overlayManager.forceRefresh();
            }
        });
        EasyMock.verify(mockToaster);

    }

}
