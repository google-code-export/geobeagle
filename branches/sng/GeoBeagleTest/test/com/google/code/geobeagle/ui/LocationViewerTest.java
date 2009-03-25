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
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.ResourceProvider;
import com.google.code.geobeagle.ui.GpsStatusWidget.MeterFormatter;
import com.google.code.geobeagle.ui.GpsStatusWidget.MeterView;
import com.google.code.geobeagle.ui.GpsStatusWidget.Time;

import android.location.Location;
import android.location.LocationProvider;

import junit.framework.TestCase;

public class LocationViewerTest extends TestCase {

    public void testAccuracyToBars() {
        GpsStatusWidget.MeterFormatter meterFormatter = new GpsStatusWidget.MeterFormatter();
        assertEquals(0, meterFormatter.accuracyToBarCount(-1));
        assertEquals(0, meterFormatter.accuracyToBarCount(0));
        assertEquals(0, meterFormatter.accuracyToBarCount(1));
        assertEquals(1, meterFormatter.accuracyToBarCount(2));
        assertEquals(2, meterFormatter.accuracyToBarCount(4));
        assertEquals(3, meterFormatter.accuracyToBarCount(8));
        assertEquals(GpsStatusWidget.METER_LEFT.length(), meterFormatter
                .accuracyToBarCount(Long.MAX_VALUE));
    }

    public void testAccuracyToBarText() {
        GpsStatusWidget.MeterFormatter meterFormatter = new GpsStatusWidget.MeterFormatter();
        assertEquals("·×·", meterFormatter.barsToMeterText(meterFormatter.accuracyToBarCount(2)));
    }

    public void testGetAlpha() {
        GpsStatusWidget.MeterFormatter meterFormatter = new GpsStatusWidget.MeterFormatter();
        assertEquals(256, meterFormatter.lagToAlpha(-1));
        assertEquals(255, meterFormatter.lagToAlpha(0));
        assertEquals(254, meterFormatter.lagToAlpha(8));
        assertEquals(253, meterFormatter.lagToAlpha(16));
        assertEquals(128, meterFormatter.lagToAlpha(Integer.MAX_VALUE));
    }

    public void testGetMeterText() {
        GpsStatusWidget.MeterFormatter meterFormatter = new GpsStatusWidget.MeterFormatter();
        assertEquals("×", meterFormatter.barsToMeterText(0));
        assertEquals("·×·", meterFormatter.barsToMeterText(1));
        assertEquals("‹····×····›", meterFormatter.barsToMeterText(5));
    }

    public void testMeterView() {
        MockableTextView textView = createMock(MockableTextView.class);
        MeterFormatter meterFormatter = createMock(MeterFormatter.class);

        expect(meterFormatter.accuracyToBarCount(342)).andReturn(7);
        expect(meterFormatter.barsToMeterText(7)).andReturn("<-->");
        expect(meterFormatter.lagToAlpha(17)).andReturn(94);
        textView.setText("<-->");
        textView.setTextColor(94, 147, 190, 38);

        replay(textView);
        replay(meterFormatter);
        final MeterView meterView = new MeterView(textView, meterFormatter);
        meterView.set(17, 342);
        verify(textView);
        verify(meterFormatter);
    }

    public void testSetLocationEightSecondDelay() {
        MeterView meterView = createMock(MeterView.class);
        MockableTextView lag = createMock(MockableTextView.class);
        MockableTextView accuracy = createMock(MockableTextView.class);
        MockableTextView provider = createMock(MockableTextView.class);
        Time time = createMock(Time.class);
        Location location = createMock(Location.class);

        expect(time.getCurrentTime()).andReturn(10000L);
        expect(time.getCurrentTime()).andReturn(18000L);
        expect(location.getAccuracy()).andReturn(12f);
        expect(location.getProvider()).andReturn("gps");
        provider.setText("gps");
        lag.setText("8s");
        accuracy.setText("12.0m");
        meterView.set(8000, 12f);

        replay(meterView);
        replay(lag);
        replay(accuracy);
        replay(provider);
        replay(location);
        replay(time);
        GpsStatusWidget gpsStatusWidget = new GpsStatusWidget(null, meterView, provider, lag,
                accuracy, null, time, location);
        gpsStatusWidget.setLocation(location);
        gpsStatusWidget.refreshLocation();
        verify(location);
        verify(meterView);
        verify(provider);
        verify(lag);
        verify(accuracy);
        verify(time);
    }

    public void testSetStatus() {
        ResourceProvider resourceProvider = createMock(ResourceProvider.class);
        MockableTextView status = createMock(MockableTextView.class);

        expect(resourceProvider.getString(R.string.out_of_service)).andReturn("OUT OF SERVICE");
        expect(resourceProvider.getString(R.string.available)).andReturn("AVAILABLE");
        expect(resourceProvider.getString(R.string.temporarily_unavailable)).andReturn(
                "TEMPORARILY UNAVAILABLE");
        status.setText("gps status: OUT OF SERVICE");
        status.setText("network status: AVAILABLE");
        status.setText("gps status: TEMPORARILY UNAVAILABLE");

        replay(resourceProvider);
        replay(status);
        GpsStatusWidget gpsStatusWidget = new GpsStatusWidget(resourceProvider, null, null, null,
                null, status, null, null);
        gpsStatusWidget.setStatus("gps", LocationProvider.OUT_OF_SERVICE);
        gpsStatusWidget.setStatus("network", LocationProvider.AVAILABLE);
        gpsStatusWidget.setStatus("gps", LocationProvider.TEMPORARILY_UNAVAILABLE);
        verify(resourceProvider);
        verify(status);
    }
}
