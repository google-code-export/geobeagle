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

package com.google.code.geobeagle.mainactivity.ui;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.ResourceProvider;
import com.google.code.geobeagle.location.CombinedLocationManager;
import com.google.code.geobeagle.location.LocationControlBuffered;
import com.google.code.geobeagle.mainactivity.ui.GpsStatusWidget.GpsStatusWidgetDelegate;
import com.google.code.geobeagle.mainactivity.ui.GpsStatusWidget.MeterFader;
import com.google.code.geobeagle.mainactivity.ui.GpsStatusWidget.MeterWrapper;
import com.google.code.geobeagle.mainactivity.ui.GpsStatusWidget.TextLagUpdater;
import com.google.code.geobeagle.mainactivity.ui.GpsStatusWidget.UpdateGpsWidgetRunnable;

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
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        Bundle.class, Color.class, Handler.class, LinearLayout.class, TextView.class,
        GpsStatusWidget.class
})
public class GpsStatusWidgetTest {
    @Test
    public void testFadeMeter() {
        View parent = PowerMock.createMock(View.class);
        Misc.Time time = PowerMock.createMock(Misc.Time.class);
        MeterView meterView = PowerMock.createMock(MeterView.class);

        expect(time.getCurrentTime()).andReturn(1000L);
        meterView.setLag(0);
        parent.postInvalidateDelayed(100);

        PowerMock.replayAll();
        new MeterFader(parent, meterView, time).paint();
        // Log.v("GeoBeagle", "painting " + lastUpdateLag);
        PowerMock.verifyAll();
    }

    @Test
    public void testFadeMeterLastDelay() {
        View parent = PowerMock.createMock(View.class);
        Misc.Time time = PowerMock.createMock(Misc.Time.class);
        MeterView meterView = PowerMock.createMock(MeterView.class);

        expect(time.getCurrentTime()).andReturn(1000L);
        meterView.setLag(0);
        parent.postInvalidateDelayed(100);

        expect(time.getCurrentTime()).andReturn(2000L);
        meterView.setLag(1000);

        PowerMock.replayAll();
        final MeterFader meterFader = new MeterFader(parent, meterView, time);
        meterFader.paint();
        meterFader.paint();
        // Log.v("GeoBeagle", "painting " + lastUpdateLag);
        PowerMock.verifyAll();
    }

    @Test
    public void testFadeMeterReset() {
        View parent = PowerMock.createMock(View.class);
        Misc.Time time = PowerMock.createMock(Misc.Time.class);
        MeterView meterView = PowerMock.createMock(MeterView.class);

        expect(time.getCurrentTime()).andReturn(1000L);
        meterView.setLag(0);
        parent.postInvalidateDelayed(100);

        expect(time.getCurrentTime()).andReturn(1100L);
        meterView.setLag(100);
        parent.postInvalidateDelayed(100);

        parent.postInvalidate();

        expect(time.getCurrentTime()).andReturn(1200L);
        meterView.setLag(0);
        parent.postInvalidateDelayed(100);

        PowerMock.replayAll();
        final MeterFader meterFader = new MeterFader(parent, meterView, time);
        meterFader.paint();
        meterFader.paint();
        meterFader.reset();
        meterFader.paint();
        // Log.v("GeoBeagle", "painting " + lastUpdateLag);
        PowerMock.verifyAll();
    }

    @Test
    public void testFadeMeterTwice() {
        View parent = PowerMock.createMock(View.class);
        Misc.Time time = PowerMock.createMock(Misc.Time.class);
        MeterView meterView = PowerMock.createMock(MeterView.class);

        expect(time.getCurrentTime()).andReturn(1000L);
        meterView.setLag(0);
        parent.postInvalidateDelayed(100);

        expect(time.getCurrentTime()).andReturn(1100L);
        meterView.setLag(100);
        parent.postInvalidateDelayed(100);

        PowerMock.replayAll();
        final MeterFader meterFader = new MeterFader(parent, meterView, time);
        meterFader.paint();
        meterFader.paint();
        // Log.v("GeoBeagle", "painting " + lastUpdateLag);
        PowerMock.verifyAll();
    }

    @Test
    public void testGpsStatusWidget_OnLocationChangedNullLocation() {
        PowerMock.suppressConstructor(LinearLayout.class);

        new GpsStatusWidgetDelegate(null, null, null, null, null, null, null)
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
                null, null, status, null);
        gpsStatusWidget.onProviderEnabled("gps");
        gpsStatusWidget.onProviderDisabled("gps");
        PowerMock.verifyAll();
    }

    @Test
    public void testMeterWrapper_SetAccuracyAzimuth() {
        MeterView meterView = PowerMock.createMock(MeterView.class);
        TextView accuracyView = PowerMock.createMock(TextView.class);

        accuracyView.setText("1m");
        meterView.set(1.2f, 0);

        meterView.set(1.2f, 280);

        accuracyView.setText("2m");
        meterView.set(2.2f, 280);

        PowerMock.replayAll();
        final MeterWrapper meterWrapper = new MeterWrapper(meterView, accuracyView);
        meterWrapper.setAccuracy(1.2f);
        meterWrapper.setAzimuth(280);
        meterWrapper.setAccuracy(2.2f);
        PowerMock.verifyAll();
    }

    @Test
    public void testMeterWrapper_SetDisabled() {
        MeterView meterView = PowerMock.createMock(MeterView.class);
        TextView accuracyView = PowerMock.createMock(TextView.class);

        accuracyView.setText("");
        meterView.set(Float.MAX_VALUE, 0);

        PowerMock.replayAll();
        final MeterWrapper meterWrapper = new MeterWrapper(meterView, accuracyView);
        meterWrapper.setDisabled();
        PowerMock.verifyAll();
    }

    @Test
    public void testOnLocationChanged() {
        MeterFader meterFader = PowerMock.createMock(MeterFader.class);
        MeterWrapper meterWrapper = PowerMock.createMock(MeterWrapper.class);
        TextLagUpdater textLagUpdater = PowerMock.createMock(TextLagUpdater.class);
        TextView provider = PowerMock.createMock(TextView.class);
        Location location = PowerMock.createMock(Location.class);
        CombinedLocationManager combinedLocationManager = PowerMock
                .createMock(CombinedLocationManager.class);

        expect(combinedLocationManager.isProviderEnabled()).andReturn(true);
        expect(location.getProvider()).andReturn("gps");
        expect(location.getAccuracy()).andReturn(1.2f);
        expect(location.getTime()).andReturn(1000L);
        provider.setText("gps");
        meterWrapper.setAccuracy(1.2f);
        meterFader.reset();
        textLagUpdater.reset(1000);

        PowerMock.replayAll();
        new GpsStatusWidgetDelegate(combinedLocationManager, meterFader, meterWrapper, provider,
                null, null, textLagUpdater).onLocationChanged(location);
        PowerMock.verifyAll();
    }

    @Test
    public void testOnLocationChangedProviderDisabled() {
        MeterWrapper meterWrapper = PowerMock.createMock(MeterWrapper.class);
        TextLagUpdater textLagUpdater = PowerMock.createMock(TextLagUpdater.class);
        Location location = PowerMock.createMock(Location.class);
        CombinedLocationManager combinedLocationManager = PowerMock
                .createMock(CombinedLocationManager.class);

        expect(combinedLocationManager.isProviderEnabled()).andReturn(false);
        textLagUpdater.setDisabled();
        meterWrapper.setDisabled();

        PowerMock.replayAll();
        new GpsStatusWidgetDelegate(combinedLocationManager, null, meterWrapper, null, null, null,
                textLagUpdater).onLocationChanged(location);
        PowerMock.verifyAll();
    }

    @Test
    public void testSetStatus() {
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
                null, resourceProvider, status, null);
        gpsStatusWidget.onStatusChanged("gps", LocationProvider.OUT_OF_SERVICE, null);
        gpsStatusWidget.onStatusChanged("network", LocationProvider.AVAILABLE, null);
        gpsStatusWidget.onStatusChanged("gps", LocationProvider.TEMPORARILY_UNAVAILABLE, null);
        PowerMock.verifyAll();
    }

    @Test
    public void testTextLagUpdater_setDisabled() {
        TextView textLag = PowerMock.createMock(TextView.class);

        textLag.setText("");
        PowerMock.replayAll();
        new TextLagUpdater(null, textLag, null).setDisabled();
        PowerMock.verifyAll();
    }

    @Test
    public void testTextLagUpdater_update() {
        TextView textLag = PowerMock.createMock(TextView.class);

        textLag.setText("");

        PowerMock.replayAll();
        new TextLagUpdater(null, textLag, null).setDisabled();
        PowerMock.verifyAll();
    }

    @Test
    public void testTimeFormatter() {
        assertEquals("10s", TextLagUpdater.formatTime(10));
        assertEquals("8m 0s", TextLagUpdater.formatTime(480));
        assertEquals("10m 5s", TextLagUpdater.formatTime(605));
        assertEquals("1h 1m", TextLagUpdater.formatTime(3660));
    }

    @Test
    public void testUpdateGpsWidgetRunnable() {
        TextLagUpdater textLagUpdater = PowerMock.createMock(TextLagUpdater.class);
        LocationControlBuffered locationControlBuffered = PowerMock
                .createMock(LocationControlBuffered.class);
        MeterWrapper meterWrapper = PowerMock.createMock(MeterWrapper.class);
        Handler handler = PowerMock.createMock(Handler.class);

        textLagUpdater.updateTextLag();
        expect(locationControlBuffered.getAzimuth()).andReturn(42f);
        meterWrapper.setAzimuth(42f);
        EasyMock
                .expect(
                        handler.postDelayed(EasyMock.isA(UpdateGpsWidgetRunnable.class), EasyMock
                                .eq(500L))).andReturn(true);

        PowerMock.replayAll();
        new UpdateGpsWidgetRunnable(handler, locationControlBuffered, meterWrapper, textLagUpdater)
                .run();
        PowerMock.verifyAll();
    }
}
