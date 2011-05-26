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

package com.google.code.geobeagle.activity.compass.view;

import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.expectNew;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.activity.compass.CompassActivity;
import com.google.code.geobeagle.activity.compass.view.OnClickListenerCacheDetails;
import com.google.code.geobeagle.activity.details.DetailsActivity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.content.Intent;

@PrepareForTest({
    OnClickListenerCacheDetails.class
})
@RunWith(PowerMockRunner.class)
public class OnClickListenerCacheDetailsTest {

    @Test
    public void testOnClick() throws Exception {
        CompassActivity geobeagle = createMock(CompassActivity.class);
        Intent intent = createMock(Intent.class);
        Geocache geocache = createMock(Geocache.class);

        expectNew(Intent.class, geobeagle, DetailsActivity.class).andReturn(intent);
        expect(geobeagle.getGeocache()).andReturn(geocache);
        expect(geocache.getSourceName()).andReturn("bcaching.com");
        expect(geocache.getId()).andReturn("GC123");
        expect(geocache.getName()).andReturn("my cache");
        expect(intent.putExtra(DetailsActivity.INTENT_EXTRA_GEOCACHE_SOURCE, "bcaching.com"))
                .andReturn(intent);
        expect(intent.putExtra(DetailsActivity.INTENT_EXTRA_GEOCACHE_ID, "GC123"))
                .andReturn(intent);
        expect(intent.putExtra(DetailsActivity.INTENT_EXTRA_GEOCACHE_NAME, "my cache")).andReturn(
                intent);
        geobeagle.startActivity(intent);

        replayAll();
        new OnClickListenerCacheDetails(geobeagle).onClick(null);
        verifyAll();
    }
}
