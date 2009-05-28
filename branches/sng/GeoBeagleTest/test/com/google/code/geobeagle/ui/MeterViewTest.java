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
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.graphics.Color;
import android.widget.TextView;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        Color.class, TextView.class
})
public class MeterViewTest {

    @Test
    public void testMeterFormatter_AccuracyToBars() {
        MeterView.MeterFormatter meterFormatter = new MeterView.MeterFormatter();
        assertEquals(0, meterFormatter.accuracyToBarCount(-1));
        assertEquals(0, meterFormatter.accuracyToBarCount(0));
        assertEquals(0, meterFormatter.accuracyToBarCount(1));
        assertEquals(1, meterFormatter.accuracyToBarCount(2));
        assertEquals(2, meterFormatter.accuracyToBarCount(4));
        assertEquals(3, meterFormatter.accuracyToBarCount(8));
        assertEquals(MeterView.METER_LEFT.length(), meterFormatter
                .accuracyToBarCount(Long.MAX_VALUE));
    }

    @Test
    public void testMeterFormatter_AccuracyToBarText() {
        MeterView.MeterFormatter meterFormatter = new MeterView.MeterFormatter();
        assertEquals("[·×·]", meterFormatter.barsToMeterText(meterFormatter.accuracyToBarCount(2),
                "×"));
    }

    @Test
    public void testMeterFormatter_GetAlpha() {
        MeterView.MeterFormatter meterFormatter = new MeterView.MeterFormatter();
        assertEquals(256, meterFormatter.lagToAlpha(-1));
        assertEquals(255, meterFormatter.lagToAlpha(0));
        assertEquals(254, meterFormatter.lagToAlpha(8));
        assertEquals(253, meterFormatter.lagToAlpha(16));
        assertEquals(128, meterFormatter.lagToAlpha(Integer.MAX_VALUE));
    }

    @Test
    public void testMeterFormatter_GetMeterText() {
        MeterView.MeterFormatter meterFormatter = new MeterView.MeterFormatter();
        assertEquals("[×]", meterFormatter.barsToMeterText(0, "×"));
        assertEquals("[·×·]", meterFormatter.barsToMeterText(1, "×"));
        assertEquals("[‹····×····›]", meterFormatter.barsToMeterText(5, "×"));
    }

    @Test
    public void testMeterView_set() {
        TextView textView = PowerMock.createMock(TextView.class);
        MeterView.MeterFormatter meterFormatter = PowerMock
                .createMock(MeterView.MeterFormatter.class);
        PowerMock.mockStatic(Color.class);

        expect(meterFormatter.accuracyToBarCount(342)).andReturn(7);
        expect(meterFormatter.barsToMeterText(7, "90°")).andReturn("<-->");
        textView.setText("<-->");

        PowerMock.replayAll();
        new MeterView(textView, meterFormatter).set(342, 90);
        PowerMock.verifyAll();
    }

    @Test
    public void testMeterView_setLag() {
        TextView textView = PowerMock.createMock(TextView.class);
        MeterView.MeterFormatter meterFormatter = PowerMock
                .createMock(MeterView.MeterFormatter.class);
        PowerMock.mockStatic(Color.class);

        expect(meterFormatter.lagToAlpha(17)).andReturn(94);
        expect(Color.argb(94, 147, 190, 38)).andReturn(333);
        textView.setTextColor(333);

        PowerMock.replayAll();
        new MeterView(textView, meterFormatter).setLag(17);
        PowerMock.verifyAll();
    }

}
