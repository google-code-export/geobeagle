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

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.Time;

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
import android.widget.LinearLayout;
import android.widget.TextView;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        Bundle.class, Color.class, Handler.class, LinearLayout.class, TextView.class,
        GpsStatusWidget.class, Context.class
})
public class GpsStatusWidgetTest {
    private InflatedGpsStatusWidget inflatedGpsStatusWidget;
    private TextView barsAndAzimuth;
    private Time time;

    @Before
    public void setUp() {
        inflatedGpsStatusWidget = PowerMock.createMock(InflatedGpsStatusWidget.class);
        barsAndAzimuth = PowerMock.createMock(TextView.class);
        time = PowerMock.createMock(Time.class);

        PowerMock.mockStatic(Color.class);
        expect(inflatedGpsStatusWidget.findViewById(R.id.location_viewer))
                .andReturn(barsAndAzimuth);
    }

    @Test
    public void testFadeMeter() {
        expect(Color.argb(255, 147, 190, 38)).andReturn(47);
        barsAndAzimuth.setTextColor(47);
        expect(time.getCurrentTime()).andReturn(1000L);
        inflatedGpsStatusWidget.postInvalidateDelayed(100);

        PowerMock.replayAll();
        new MeterFader(inflatedGpsStatusWidget, time).paint();
        PowerMock.verifyAll();
    }

    @Test
    public void testFadeMeterLastDelay() {
        expect(Color.argb(255, 147, 190, 38)).andReturn(47);
        expect(Color.argb(130, 147, 190, 38)).andReturn(52);
        barsAndAzimuth.setTextColor(47);
        barsAndAzimuth.setTextColor(52);
        expect(time.getCurrentTime()).andReturn(1000L);
        inflatedGpsStatusWidget.postInvalidateDelayed(100);

        expect(time.getCurrentTime()).andReturn(2000L);

        PowerMock.replayAll();
        MeterFader meterFader = new MeterFader(inflatedGpsStatusWidget, time);
        meterFader.paint();
        meterFader.paint();
        PowerMock.verifyAll();
    }

    @Test
    public void testFadeMeterReset() {
        expect(Color.argb(255, 147, 190, 38)).andReturn(47);
        expect(Color.argb(243, 147, 190, 38)).andReturn(52);
        expect(Color.argb(255, 147, 190, 38)).andReturn(65);
        barsAndAzimuth.setTextColor(47);
        barsAndAzimuth.setTextColor(52);
        barsAndAzimuth.setTextColor(65);
        expect(time.getCurrentTime()).andReturn(1000L);
        expect(time.getCurrentTime()).andReturn(1100L);
        expect(time.getCurrentTime()).andReturn(1200L);
        inflatedGpsStatusWidget.postInvalidateDelayed(100);
        inflatedGpsStatusWidget.postInvalidateDelayed(100);
        inflatedGpsStatusWidget.postInvalidateDelayed(100);
        inflatedGpsStatusWidget.postInvalidate();

        PowerMock.replayAll();
        MeterFader meterFader = new MeterFader(inflatedGpsStatusWidget, time);
        meterFader.paint();
        meterFader.paint();
        meterFader.reset();
        meterFader.paint();
        PowerMock.verifyAll();
    }

}
