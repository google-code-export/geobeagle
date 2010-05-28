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
import static org.junit.Assert.assertEquals;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.gpsstatuswidget.MeterFormatter;
import com.google.code.geobeagle.gpsstatuswidget.MeterBars;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        Color.class, Context.class, TextView.class
})
public class MeterViewTest {

    @Test
    public void testMeterFormatter_AccuracyToBars() {
        Context context = PowerMock.createMock(Context.class);

        expect(context.getString(R.string.meter_left)).andReturn("<<...<...");
        expect(context.getString(R.string.meter_right)).andReturn("...>...>>");
        expect(context.getString(R.string.degrees_symbol)).andReturn("°");

        PowerMock.replayAll();
        MeterFormatter meterFormatter = new MeterFormatter(context);
        assertEquals(0, meterFormatter.accuracyToBarCount(-1));
        assertEquals(0, meterFormatter.accuracyToBarCount(0));
        assertEquals(0, meterFormatter.accuracyToBarCount(1));
        assertEquals(1, meterFormatter.accuracyToBarCount(2));
        assertEquals(2, meterFormatter.accuracyToBarCount(4));
        assertEquals(3, meterFormatter.accuracyToBarCount(8));
        assertEquals("<<...<...".length(), meterFormatter.accuracyToBarCount(Long.MAX_VALUE));
        PowerMock.verifyAll();
    }

    @Test
    public void testMeterFormatter_GetAlpha() {
        Context context = PowerMock.createMock(Context.class);
        expect(context.getString(R.string.meter_left)).andReturn("<<...<...");
        expect(context.getString(R.string.meter_right)).andReturn("...>...>>");
        expect(context.getString(R.string.degrees_symbol)).andReturn("°");

        PowerMock.replayAll();
        MeterFormatter meterFormatter = new MeterFormatter(context);
        assertEquals(256, meterFormatter.lagToAlpha(-1));
        assertEquals(255, meterFormatter.lagToAlpha(0));
        assertEquals(254, meterFormatter.lagToAlpha(8));
        assertEquals(253, meterFormatter.lagToAlpha(16));
        assertEquals(128, meterFormatter.lagToAlpha(Integer.MAX_VALUE));
        PowerMock.verifyAll();
    }

    @Test
    public void testMeterFormatter_GetMeterText() {
        Context context = PowerMock.createMock(Context.class);

        expect(context.getString(R.string.meter_left)).andReturn("....1....2....");
        expect(context.getString(R.string.meter_right)).andReturn("....8....9....");
        expect(context.getString(R.string.degrees_symbol)).andReturn("°");

        PowerMock.replayAll();
        MeterFormatter meterFormatter = new MeterFormatter(context);
        assertEquals("[0°]", meterFormatter.barsToMeterText(0, "0"));
        assertEquals("[.15°.]", meterFormatter.barsToMeterText(1, "15"));
        assertEquals("[2....90°....8]", meterFormatter.barsToMeterText(5, "90"));
        PowerMock.verifyAll();
    }

    @Test
    public void testMeterView_set() {
        TextView textView = PowerMock.createMock(TextView.class);
        MeterFormatter meterFormatter = PowerMock.createMock(MeterFormatter.class);
        PowerMock.mockStatic(Color.class);

        expect(meterFormatter.accuracyToBarCount(342)).andReturn(7);
        expect(meterFormatter.barsToMeterText(7, "90")).andReturn("<-90°->");
        textView.setText("<-90°->");

        PowerMock.replayAll();
        new MeterBars(textView, meterFormatter).set(342, 90);
        PowerMock.verifyAll();
    }

    @Test
    public void testMeterView_setLag() {
        TextView textView = PowerMock.createMock(TextView.class);
        MeterFormatter meterFormatter = PowerMock.createMock(MeterFormatter.class);
        PowerMock.mockStatic(Color.class);

        expect(meterFormatter.lagToAlpha(17)).andReturn(94);
        expect(Color.argb(94, 147, 190, 38)).andReturn(333);
        textView.setTextColor(333);

        PowerMock.replayAll();
        new MeterBars(textView, meterFormatter).setLag(17);
        PowerMock.verifyAll();
    }

}
