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

package com.google.code.geobeagle.ui;

import static org.easymock.EasyMock.expect;

import com.google.code.geobeagle.RadarView;
import com.google.code.geobeagle.data.Geocache;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.widget.TextView;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
    TextView.class
})
public class GeocacheViewerTest {

    @Test
    public void testSet() {
        TextView id = PowerMock.createMock(TextView.class);
        TextView name = PowerMock.createMock(TextView.class);
        TextView coords = PowerMock.createMock(TextView.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);
        RadarView radar = PowerMock.createMock(RadarView.class);

        expect(geocache.getLatitude()).andReturn(37.0);
        expect(geocache.getLongitude()).andReturn(-122.0);
        radar.setTarget(37000000, -122000000);
        expect(geocache.getId()).andReturn("GC123");
        expect(geocache.getName()).andReturn("a cache");

        id.setText("GC123");
        name.setText("a cache");
        coords.setText("37 00.000, -122 00.000");

        PowerMock.replayAll();
        new GeocacheViewer(radar, id, name, coords).set(geocache);
        PowerMock.verifyAll();
    }
}
