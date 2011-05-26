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
package com.google.code.geobeagle.gpsstatuswidget;

import static org.easymock.EasyMock.expect;

import com.google.code.geobeagle.LocationControlBuffered;
import com.google.code.geobeagle.activity.cachelist.ActivityVisible;
import com.google.code.geobeagle.activity.cachelist.GeoBeagleTest;
import com.google.inject.Provider;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        Bundle.class, Color.class, Handler.class, LinearLayout.class, TextView.class,
        GpsStatusWidget.class, Context.class, Log.class
})
public class UpdateGpsWidgetRunnableTest extends GeoBeagleTest {
    private Provider<LocationControlBuffered> locationControlBufferedProvider;
    private LocationControlBuffered locationControlBuffered;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        locationControlBufferedProvider = PowerMock.createMock(Provider.class);
        locationControlBuffered = PowerMock.createMock(LocationControlBuffered.class);
    }

    @Test
    public void testUpdateGpsWidgetRunnable() {
        TextLagUpdater textLagUpdater = PowerMock.createMock(TextLagUpdater.class);
        Meter meter = PowerMock.createMock(Meter.class);
        Handler handler = PowerMock.createMock(Handler.class);
        ActivityVisible activityVisible = PowerMock.createMock(ActivityVisible.class);

        expect(locationControlBufferedProvider.get()).andReturn(locationControlBuffered);
        expect(activityVisible.getVisible()).andReturn(true);
        textLagUpdater.updateTextLag();
        expect(locationControlBuffered.getAzimuth()).andReturn(42f);
        meter.setAzimuth(42f);
        EasyMock.expect(
                handler.postDelayed(EasyMock.isA(UpdateGpsWidgetRunnable.class), EasyMock.eq(500L)))
                .andReturn(true);

        PowerMock.replayAll();
        new UpdateGpsWidgetRunnable(handler, locationControlBufferedProvider, meter,
                textLagUpdater, activityVisible).run();
        PowerMock.verifyAll();
    }
}
