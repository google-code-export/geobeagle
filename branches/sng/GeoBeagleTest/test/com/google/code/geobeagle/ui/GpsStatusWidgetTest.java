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

import com.google.code.geobeagle.CombinedLocationManager;
import com.google.code.geobeagle.LocationControlBuffered;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.ResourceProvider;
import com.google.code.geobeagle.ui.GpsStatusWidget.UpdateGpsWidgetRunnable;
import com.google.code.geobeagle.ui.GpsStatusWidget.GpsStatusWidgetDelegate;

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
import android.widget.LinearLayout;
import android.widget.TextView;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        Bundle.class, Color.class, Handler.class, LinearLayout.class, TextView.class,
        GpsStatusWidget.class
})
public class GpsStatusWidgetTest {
    @Test
    public void testGpsStatusWidget_OnLocationChangedNullLocation() {
        PowerMock.suppressConstructor(LinearLayout.class);

        new GpsStatusWidgetDelegate(null, null, null, null, null, null, null, null, null, null)
                .onLocationChanged(null);
    }

    @Test
    public void testGpsWidget_SetEnabledDisabled() {
        TextView status = PowerMock.createMock(TextView.class);
        PowerMock.suppressConstructor(LinearLayout.class);

        status.setText("gps ENABLED");
        status.setText("gps DISABLED");

        PowerMock.replayAll();
        GpsStatusWidgetDelegate gpsStatusWidget = new GpsStatusWidgetDelegate(null, null, null,
                null, null, null, null, null, status, null);
        gpsStatusWidget.onProviderEnabled("gps");
        gpsStatusWidget.onProviderDisabled("gps");
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
        LocationControlBuffered locationControlBuffered = PowerMock
                .createMock(LocationControlBuffered.class);
        PowerMock.suppressConstructor(LinearLayout.class);
        PowerMock.suppressMethod(LinearLayout.class, "postInvalidate");
        GpsStatusWidget gpsStatusWidget = PowerMock.createMock(GpsStatusWidget.class);

        expect(locationManager.isProviderEnabled()).andReturn(true);
        expect(time.getCurrentTime()).andReturn(10000L);
        expect(location.getTime()).andReturn(9000L);
        expect(time.getCurrentTime()).andReturn(20000L);
        expect(location.getAccuracy()).andReturn(12f);
        expect(location.getProvider()).andReturn("gps");
        expect(locationControlBuffered.getAzimuth()).andReturn(45f);
        provider.setText("gps");
        lag.setText("10s");
        accuracy.setText("12m");
        meterView.set(12f, 45);
        expect(time.getCurrentTime()).andReturn(20000L);
        meterView.setLag(10000);
        gpsStatusWidget.postInvalidate();

        PowerMock.replayAll();
        GpsStatusWidgetDelegate gpsStatusWidgetDelegate = new GpsStatusWidgetDelegate(
                gpsStatusWidget, accuracy, locationControlBuffered, locationManager, lag,
                meterView, provider, null, null, time);
        gpsStatusWidgetDelegate.onLocationChanged(location);
        gpsStatusWidgetDelegate.fadeMeter();
        PowerMock.verifyAll();
    }

    @Test
    public void testFadeMeter() {
        Misc.Time time = PowerMock.createMock(Misc.Time.class);
        MeterView meterView = PowerMock.createMock(MeterView.class);

        expect(time.getCurrentTime()).andReturn(1000L);
        meterView.setLag(1000);

        PowerMock.replayAll();
        new GpsStatusWidgetDelegate(null, null, null, null, null, meterView, null, null, null, time)
                .fadeMeter();
        PowerMock.verifyAll();
    }

    @Test
    public void testFadeMeterPostInvalidate() {
        Misc.Time time = PowerMock.createMock(Misc.Time.class);
        MeterView meterView = PowerMock.createMock(MeterView.class);
        GpsStatusWidget gpsStatusWidget = PowerMock.createMock(GpsStatusWidget.class);

        expect(time.getCurrentTime()).andReturn(900L);
        meterView.setLag(900);
        gpsStatusWidget.postInvalidateDelayed(100);

        PowerMock.replayAll();
        new GpsStatusWidgetDelegate(gpsStatusWidget, null, null, null, null, meterView, null, null,
                null, time).fadeMeter();
        PowerMock.verifyAll();
    }

    @Test
    public void testOnLocationChangedProviderDisabled() {
        Location location = PowerMock.createMock(Location.class);
        CombinedLocationManager combinedLocationManager = PowerMock
                .createMock(CombinedLocationManager.class);
        TextView lag = PowerMock.createMock(TextView.class);
        TextView accuracy = PowerMock.createMock(TextView.class);
        MeterView meter = PowerMock.createMock(MeterView.class);

        expect(combinedLocationManager.isProviderEnabled()).andReturn(false);
        lag.setText("");
        accuracy.setText("");
        meter.set(Float.MAX_VALUE, 0);

        PowerMock.replayAll();
        new GpsStatusWidgetDelegate(null, accuracy, null, combinedLocationManager, lag, meter,
                null, null, null, null).onLocationChanged(location);
        PowerMock.verifyAll();

    }

    @Test
    public void testSetLocationGpsReportsFutureTimeWeirdnessDelay() {
        TextView accuracy = PowerMock.createMock(TextView.class);
        CombinedLocationManager combinedLocationManager = PowerMock
                .createMock(CombinedLocationManager.class);
        TextView lag = PowerMock.createMock(TextView.class);
        Location location = PowerMock.createMock(Location.class);
        LocationControlBuffered locationControlBuffered = PowerMock
                .createMock(LocationControlBuffered.class);
        MeterView meterView = PowerMock.createMock(MeterView.class);
        TextView provider = PowerMock.createMock(TextView.class);
        Misc.Time time = PowerMock.createMock(Misc.Time.class);
        GpsStatusWidget gpsStatusWidget = PowerMock.createMock(GpsStatusWidget.class);

        PowerMock.suppressConstructor(LinearLayout.class);
        PowerMock.suppressMethod(LinearLayout.class, "postInvalidate");

        expect(combinedLocationManager.isProviderEnabled()).andReturn(true);
        expect(time.getCurrentTime()).andReturn(10000L);
        expect(location.getTime()).andReturn(12000L);
        expect(time.getCurrentTime()).andReturn(18000L);
        expect(location.getAccuracy()).andReturn(12f);
        expect(location.getProvider()).andReturn("gps");
        expect(locationControlBuffered.getAzimuth()).andReturn(45f);
        meterView.setLag(10000);
        expect(time.getCurrentTime()).andReturn(20000L);
        gpsStatusWidget.postInvalidate();

        provider.setText("gps");
        lag.setText("8s");
        accuracy.setText("12m");
        meterView.set(12f, 45);

        PowerMock.replayAll();
        GpsStatusWidgetDelegate gpsStatusWidgetDelegate = new GpsStatusWidgetDelegate(
                gpsStatusWidget, accuracy, locationControlBuffered, combinedLocationManager, lag,
                meterView, provider, null, null, time);
        gpsStatusWidgetDelegate.onLocationChanged(location);
        gpsStatusWidgetDelegate.fadeMeter();
        PowerMock.verifyAll();
    }

    @Test
    public void testSetStatus() {
        Bundle bundle = PowerMock.createMock(Bundle.class);
        ResourceProvider resourceProvider = PowerMock.createMock(ResourceProvider.class);
        TextView status = PowerMock.createMock(TextView.class);

        PowerMock.suppressConstructor(LinearLayout.class);

        expect(resourceProvider.getString(R.string.out_of_service)).andReturn("OUT OF SERVICE");
        expect(resourceProvider.getString(R.string.available)).andReturn("AVAILABLE");
        expect(resourceProvider.getString(R.string.temporarily_unavailable)).andReturn(
                "TEMPORARILY UNAVAILABLE");
        status.setText("gps status: OUT OF SERVICE");
        status.setText("network status: AVAILABLE");
        status.setText("gps status: TEMPORARILY UNAVAILABLE");

        PowerMock.replayAll();
        GpsStatusWidgetDelegate gpsStatusWidget = new GpsStatusWidgetDelegate(null, null, null,
                null, null, null, null, resourceProvider, status, null);
        gpsStatusWidget.onStatusChanged("gps", LocationProvider.OUT_OF_SERVICE, bundle);
        gpsStatusWidget.onStatusChanged("network", LocationProvider.AVAILABLE, bundle);
        gpsStatusWidget.onStatusChanged("gps", LocationProvider.TEMPORARILY_UNAVAILABLE, bundle);
        PowerMock.verifyAll();
    }

    @Test
    public void testUpdateGpsWidgetRunnable() {
        GpsStatusWidgetDelegate gpsStatusWidget = PowerMock
                .createMock(GpsStatusWidgetDelegate.class);
        Handler handler = PowerMock.createMock(Handler.class);

        gpsStatusWidget.updateLag();
        gpsStatusWidget.updateMeter();
        EasyMock
                .expect(
                        handler.postDelayed(EasyMock.isA(UpdateGpsWidgetRunnable.class), EasyMock
                                .eq(500L))).andReturn(true);

        PowerMock.replayAll();
        new UpdateGpsWidgetRunnable(gpsStatusWidget, handler).run();
        PowerMock.verifyAll();
    }
}
