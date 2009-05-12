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

import com.google.code.geobeagle.CombinedLocationManager;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.ResourceProvider;
import com.google.code.geobeagle.ui.GpsStatusWidget.MeterFormatter;
import com.google.code.geobeagle.ui.GpsStatusWidget.MeterView;
import com.google.code.geobeagle.ui.GpsStatusWidget.UpdateGpsWidgetRunnable;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.graphics.Color;
import android.location.Location;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        Bundle.class, Color.class, Handler.class, TextView.class
})
public class GpsStatusWidgetTest {
    @Test
    public void testAccuracyToBars() {
        MeterFormatter meterFormatter = new MeterFormatter();
        assertEquals(0, meterFormatter.accuracyToBarCount(-1));
        assertEquals(0, meterFormatter.accuracyToBarCount(0));
        assertEquals(0, meterFormatter.accuracyToBarCount(1));
        assertEquals(1, meterFormatter.accuracyToBarCount(2));
        assertEquals(2, meterFormatter.accuracyToBarCount(4));
        assertEquals(3, meterFormatter.accuracyToBarCount(8));
        assertEquals(GpsStatusWidget.METER_LEFT.length(), meterFormatter
                .accuracyToBarCount(Long.MAX_VALUE));
    }

    @Test
    public void testAccuracyToBarText() {
        MeterFormatter meterFormatter = new MeterFormatter();
        assertEquals("[·×·]", meterFormatter.barsToMeterText(meterFormatter.accuracyToBarCount(2)));
    }

    @Test
    public void testGetAlpha() {
        MeterFormatter meterFormatter = new MeterFormatter();
        assertEquals(256, meterFormatter.lagToAlpha(-1));
        assertEquals(255, meterFormatter.lagToAlpha(0));
        assertEquals(254, meterFormatter.lagToAlpha(8));
        assertEquals(253, meterFormatter.lagToAlpha(16));
        assertEquals(128, meterFormatter.lagToAlpha(Integer.MAX_VALUE));
    }

    @Test
    public void testGetMeterText() {
        MeterFormatter meterFormatter = new MeterFormatter();
        assertEquals("[×]", meterFormatter.barsToMeterText(0));
        assertEquals("[·×·]", meterFormatter.barsToMeterText(1));
        assertEquals("[‹····×····›]", meterFormatter.barsToMeterText(5));
    }

    @Test
    public void testMeterView() {
        TextView textView = PowerMock.createMock(TextView.class);
        MeterFormatter meterFormatter = PowerMock.createMock(MeterFormatter.class);
        PowerMock.mockStatic(Color.class);

        expect(meterFormatter.accuracyToBarCount(342)).andReturn(7);
        expect(meterFormatter.barsToMeterText(7)).andReturn("<-->");
        expect(meterFormatter.lagToAlpha(17)).andReturn(94);
        textView.setText("<-->");
        expect(Color.argb(94, 147, 190, 38)).andReturn(333);
        textView.setTextColor(333);

        PowerMock.replayAll();
        new MeterView(textView, meterFormatter).set(17, 342);
        PowerMock.verifyAll();
    }

    @Test
    public void testSetEnabledDisabled() {
        TextView status = PowerMock.createMock(TextView.class);

        status.setText("gps ENABLED");
        status.setText("gps DISABLED");

        PowerMock.replayAll();
        GpsStatusWidget gpsStatusWidget = new GpsStatusWidget(null, null, null, null, null, status,
                null, null, null);
        gpsStatusWidget.onProviderEnabled("gps");
        gpsStatusWidget.onProviderDisabled("gps");
        PowerMock.verifyAll();
    }

    @Test
    public void testOnLocationChangedNullLocation() {
        new GpsStatusWidget(null, null, null, null, null, null, null, null, null)
                .onLocationChanged(null);
    }

    @Test
    public void testRefreshLocationLocationDisabled() {
        MeterView meterView = PowerMock.createMock(MeterView.class);
        TextView lag = PowerMock.createMock(TextView.class);
        TextView accuracy = PowerMock.createMock(TextView.class);
        CombinedLocationManager combinedLocationManager = PowerMock
                .createMock(CombinedLocationManager.class);

        expect(combinedLocationManager.isProviderEnabled()).andReturn(false);
        lag.setText("");
        accuracy.setText("");
        meterView.set(Long.MAX_VALUE, Float.MAX_VALUE);

        PowerMock.replayAll();
        new GpsStatusWidget(null, meterView, null, lag, accuracy, null, null, null,
                combinedLocationManager).refreshLocation();
        PowerMock.verifyAll();
    }

    @Test
    public void testSetLocationElevenSecondDelay() {
        MeterView meterView = PowerMock.createMock(MeterView.class);
        TextView lag = PowerMock.createMock(TextView.class);
        TextView accuracy = PowerMock.createMock(TextView.class);
        TextView provider = PowerMock.createMock(TextView.class);
        Misc.Time time = PowerMock.createMock(Misc.Time.class);
        Location location = PowerMock.createMock(Location.class);
        CombinedLocationManager locationManager = PowerMock
                .createMock(CombinedLocationManager.class);

        expect(locationManager.isProviderEnabled()).andReturn(true);
        expect(time.getCurrentTime()).andReturn(10000L);
        expect(location.getTime()).andReturn(9000L);
        expect(time.getCurrentTime()).andReturn(20000L);
        expect(location.getAccuracy()).andReturn(12f);
        expect(location.getProvider()).andReturn("gps");
        provider.setText("gps");
        lag.setText("11s");
        accuracy.setText("12m");
        meterView.set(10000, 12f);

        PowerMock.replayAll();
        GpsStatusWidget gpsStatusWidget = new GpsStatusWidget(null, meterView, provider, lag,
                accuracy, null, time, location, locationManager);
        gpsStatusWidget.onLocationChanged(location);
        gpsStatusWidget.refreshLocation();
        PowerMock.verifyAll();
    }

    @Test
    public void testSetLocationGpsReportsFutureTimeWeirdnessDelay() {
        MeterView meterView = PowerMock.createMock(MeterView.class);
        TextView lag = PowerMock.createMock(TextView.class);
        TextView accuracy = PowerMock.createMock(TextView.class);
        TextView provider = PowerMock.createMock(TextView.class);
        Misc.Time time = PowerMock.createMock(Misc.Time.class);
        Location location = PowerMock.createMock(Location.class);
        CombinedLocationManager combinedLocationManager = PowerMock
                .createMock(CombinedLocationManager.class);

        expect(combinedLocationManager.isProviderEnabled()).andReturn(true);
        expect(time.getCurrentTime()).andReturn(10000L);
        expect(location.getTime()).andReturn(12000L);
        expect(time.getCurrentTime()).andReturn(18000L);
        expect(location.getAccuracy()).andReturn(12f);
        expect(location.getProvider()).andReturn("gps");
        provider.setText("gps");
        lag.setText("8s");
        accuracy.setText("12m");
        meterView.set(8000, 12f);

        PowerMock.replayAll();
        GpsStatusWidget gpsStatusWidget = new GpsStatusWidget(null, meterView, provider, lag,
                accuracy, null, time, location, combinedLocationManager);
        gpsStatusWidget.onLocationChanged(location);
        gpsStatusWidget.refreshLocation();
        PowerMock.verifyAll();
    }

    @Test
    public void testSetStatus() {
        ResourceProvider resourceProvider = PowerMock.createMock(ResourceProvider.class);
        TextView status = PowerMock.createMock(TextView.class);
        Bundle bundle = PowerMock.createMock(Bundle.class);

        expect(resourceProvider.getString(R.string.out_of_service)).andReturn("OUT OF SERVICE");
        expect(resourceProvider.getString(R.string.available)).andReturn("AVAILABLE");
        expect(resourceProvider.getString(R.string.temporarily_unavailable)).andReturn(
                "TEMPORARILY UNAVAILABLE");
        status.setText("gps status: OUT OF SERVICE");
        status.setText("network status: AVAILABLE");
        status.setText("gps status: TEMPORARILY UNAVAILABLE");

        PowerMock.replayAll();
        GpsStatusWidget gpsStatusWidget = new GpsStatusWidget(resourceProvider, null, null, null,
                null, status, null, null, null);
        gpsStatusWidget.onStatusChanged("gps", LocationProvider.OUT_OF_SERVICE, bundle);
        gpsStatusWidget.onStatusChanged("network", LocationProvider.AVAILABLE, bundle);
        gpsStatusWidget.onStatusChanged("gps", LocationProvider.TEMPORARILY_UNAVAILABLE, bundle);
        PowerMock.verifyAll();
    }

    @Test
    public void testUpdateGpsWidgetRunnable() {
        GpsStatusWidget gpsStatusWidget = PowerMock.createMock(GpsStatusWidget.class);
        Handler handler = PowerMock.createMock(Handler.class);

        gpsStatusWidget.refreshLocation();
        EasyMock
                .expect(
                        handler.postDelayed(EasyMock.isA(UpdateGpsWidgetRunnable.class), EasyMock
                                .eq(100L))).andReturn(true);

        PowerMock.replayAll();
        new UpdateGpsWidgetRunnable(gpsStatusWidget, handler).run();
        PowerMock.verifyAll();
    }
}
