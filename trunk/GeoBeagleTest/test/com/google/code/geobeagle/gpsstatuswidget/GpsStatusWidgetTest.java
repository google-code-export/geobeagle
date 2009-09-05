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
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.ResourceProvider;
import com.google.code.geobeagle.Time;
import com.google.code.geobeagle.formatting.DistanceFormatter;
import com.google.code.geobeagle.location.CombinedLocationManager;

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
        Time time = PowerMock.createMock(Time.class);
        MeterBars meterBars = PowerMock.createMock(MeterBars.class);

        expect(time.getCurrentTime()).andReturn(1000L);
        meterBars.setLag(0);
        parent.postInvalidateDelayed(100);

        PowerMock.replayAll();
        new MeterFader(parent, meterBars, time).paint();
        // Log.d("GeoBeagle", "painting " + lastUpdateLag);
        PowerMock.verifyAll();
    }

    @Test
    public void testFadeMeterLastDelay() {
        View parent = PowerMock.createMock(View.class);
        Time time = PowerMock.createMock(Time.class);
        MeterBars meterBars = PowerMock.createMock(MeterBars.class);

        expect(time.getCurrentTime()).andReturn(1000L);
        meterBars.setLag(0);
        parent.postInvalidateDelayed(100);

        expect(time.getCurrentTime()).andReturn(2000L);
        meterBars.setLag(1000);

        PowerMock.replayAll();
        final MeterFader meterFader = new MeterFader(parent, meterBars, time);
        meterFader.paint();
        meterFader.paint();
        // Log.d("GeoBeagle", "painting " + lastUpdateLag);
        PowerMock.verifyAll();
    }

    @Test
    public void testFadeMeterReset() {
        View parent = PowerMock.createMock(View.class);
        Time time = PowerMock.createMock(Time.class);
        MeterBars meterBars = PowerMock.createMock(MeterBars.class);

        expect(time.getCurrentTime()).andReturn(1000L);
        meterBars.setLag(0);
        parent.postInvalidateDelayed(100);

        expect(time.getCurrentTime()).andReturn(1100L);
        meterBars.setLag(100);
        parent.postInvalidateDelayed(100);

        parent.postInvalidate();

        expect(time.getCurrentTime()).andReturn(1200L);
        meterBars.setLag(0);
        parent.postInvalidateDelayed(100);

        PowerMock.replayAll();
        final MeterFader meterFader = new MeterFader(parent, meterBars, time);
        meterFader.paint();
        meterFader.paint();
        meterFader.reset();
        meterFader.paint();
        // Log.d("GeoBeagle", "painting " + lastUpdateLag);
        PowerMock.verifyAll();
    }

    @Test
    public void testFadeMeterTwice() {
        View parent = PowerMock.createMock(View.class);
        Time time = PowerMock.createMock(Time.class);
        MeterBars meterBars = PowerMock.createMock(MeterBars.class);

        expect(time.getCurrentTime()).andReturn(1000L);
        meterBars.setLag(0);
        parent.postInvalidateDelayed(100);

        expect(time.getCurrentTime()).andReturn(1100L);
        meterBars.setLag(100);
        parent.postInvalidateDelayed(100);

        PowerMock.replayAll();
        final MeterFader meterFader = new MeterFader(parent, meterBars, time);
        meterFader.paint();
        meterFader.paint();
        // Log.d("GeoBeagle", "painting " + lastUpdateLag);
        PowerMock.verifyAll();
    }

    @Test
    public void testGpsStatusWidget_OnLocationChangedNullLocation() {
        PowerMock.suppressConstructor(LinearLayout.class);

        new GpsStatusWidgetDelegate(null, null, null, null, null, null, null, null)
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
                null, null, null, status, null);
        gpsStatusWidget.onProviderEnabled("gps");
        gpsStatusWidget.onProviderDisabled("gps");
        PowerMock.verifyAll();
    }

    @Test
    public void testMeterWrapper_SetAccuracyAzimuth() {
        MeterBars meterBars = PowerMock.createMock(MeterBars.class);
        TextView accuracyView = PowerMock.createMock(TextView.class);
        DistanceFormatter distanceFormatter = PowerMock.createMock(DistanceFormatter.class);

        EasyMock.expect(distanceFormatter.formatDistance(1.2f)).andReturn("1m");
        accuracyView.setText("1m");
        meterBars.set(1.2f, 0);

        EasyMock.expect(distanceFormatter.formatDistance(1.2f)).andReturn("1m");
        meterBars.set(1.2f, 280);

        EasyMock.expect(distanceFormatter.formatDistance(2.2f)).andReturn("2m");
        accuracyView.setText("2m");
        meterBars.set(2.2f, 280);
        EasyMock.expect(distanceFormatter.formatDistance(2.2f)).andReturn("2m");

        PowerMock.replayAll();
        final Meter meter = new Meter(meterBars, accuracyView);
        meter.setAccuracy(1.2f, distanceFormatter);
        meter.setAzimuth(280);
        meter.setAccuracy(2.2f, distanceFormatter);
        PowerMock.verifyAll();
    }

    @Test
    public void testMeterWrapper_SetDisabled() {
        MeterBars meterBars = PowerMock.createMock(MeterBars.class);
        TextView accuracyView = PowerMock.createMock(TextView.class);

        accuracyView.setText("");
        meterBars.set(Float.MAX_VALUE, 0);

        PowerMock.replayAll();
        final Meter meter = new Meter(meterBars, accuracyView);
        meter.setDisabled();
        PowerMock.verifyAll();
    }

    @Test
    public void testOnLocationChanged() {
        MeterFader meterFader = PowerMock.createMock(MeterFader.class);
        Meter meter = PowerMock.createMock(Meter.class);
        TextLagUpdater textLagUpdater = PowerMock.createMock(TextLagUpdater.class);
        TextView provider = PowerMock.createMock(TextView.class);
        Location location = PowerMock.createMock(Location.class);
        CombinedLocationManager combinedLocationManager = PowerMock
                .createMock(CombinedLocationManager.class);
        DistanceFormatter distanceFormatter = PowerMock.createMock(DistanceFormatter.class);

        expect(combinedLocationManager.isProviderEnabled()).andReturn(true);
        expect(location.getProvider()).andReturn("gps");
        expect(location.getAccuracy()).andReturn(1.2f);
        expect(location.getTime()).andReturn(1000L);
        provider.setText("gps");
        meter.setAccuracy(1.2f, distanceFormatter);
        meterFader.reset();
        textLagUpdater.reset(1000);

        PowerMock.replayAll();
        final GpsStatusWidgetDelegate gpsStatusWidgetDelegate = new GpsStatusWidgetDelegate(
                combinedLocationManager, null, meter, meterFader, provider, null, null,
                textLagUpdater);
        gpsStatusWidgetDelegate.setDistanceFormatter(distanceFormatter);
        gpsStatusWidgetDelegate.onLocationChanged(location);
        PowerMock.verifyAll();
    }

    @Test
    public void testOnLocationChangedProviderDisabled() {
        Meter meter = PowerMock.createMock(Meter.class);
        TextLagUpdater textLagUpdater = PowerMock.createMock(TextLagUpdater.class);
        Location location = PowerMock.createMock(Location.class);
        CombinedLocationManager combinedLocationManager = PowerMock
                .createMock(CombinedLocationManager.class);

        expect(combinedLocationManager.isProviderEnabled()).andReturn(false);
        textLagUpdater.setDisabled();
        meter.setDisabled();

        PowerMock.replayAll();
        new GpsStatusWidgetDelegate(combinedLocationManager, null, meter, null, null, null, null,
                textLagUpdater).onLocationChanged(location);
        PowerMock.verifyAll();
    }

    @Test
    public void testPaint() {
        MeterFader meterFader = PowerMock.createMock(MeterFader.class);

        meterFader.paint();

        PowerMock.replayAll();
        new GpsStatusWidgetDelegate(null, null, null, meterFader, null, null, null, null).paint();
        PowerMock.verifyAll();
    }

    @Test
    public void testSetStatus() {
        ResourceProvider resourceProvider = PowerMock.createMock(ResourceProvider.class);
        TextView status = PowerMock.createMock(TextView.class);
        DistanceFormatter distanceFormatter = PowerMock.createMock(DistanceFormatter.class);

        PowerMock.suppressConstructor(LinearLayout.class);

        expect(resourceProvider.getString(R.string.out_of_service)).andReturn("OUT OF SERVICE");
        expect(resourceProvider.getString(R.string.available)).andReturn("AVAILABLE");
        expect(resourceProvider.getString(R.string.temporarily_unavailable)).andReturn(
                "TEMPORARILY UNAVAILABLE");
        status.setText("gps status: OUT OF SERVICE");
        status.setText("network status: AVAILABLE");
        status.setText("gps status: TEMPORARILY UNAVAILABLE");

        PowerMock.replayAll();
        GpsStatusWidgetDelegate gpsStatusWidget = new GpsStatusWidgetDelegate(null,
                distanceFormatter, null, null, null, resourceProvider, status, null);
        gpsStatusWidget.onStatusChanged("gps", LocationProvider.OUT_OF_SERVICE, null);
        gpsStatusWidget.onStatusChanged("network", LocationProvider.AVAILABLE, null);
        gpsStatusWidget.onStatusChanged("gps", LocationProvider.TEMPORARILY_UNAVAILABLE, null);
        PowerMock.verifyAll();
    }

    @Test
    public void testUpdateGpsWidgetRunnable() {
        TextLagUpdater textLagUpdater = PowerMock.createMock(TextLagUpdater.class);
        LocationControlBuffered locationControlBuffered = PowerMock
                .createMock(LocationControlBuffered.class);
        Meter meter = PowerMock.createMock(Meter.class);
        Handler handler = PowerMock.createMock(Handler.class);

        textLagUpdater.updateTextLag();
        expect(locationControlBuffered.getAzimuth()).andReturn(42f);
        meter.setAzimuth(42f);
        EasyMock
                .expect(
                        handler.postDelayed(EasyMock.isA(UpdateGpsWidgetRunnable.class), EasyMock
                                .eq(500L))).andReturn(true);

        PowerMock.replayAll();
        new UpdateGpsWidgetRunnable(handler, locationControlBuffered, meter, textLagUpdater).run();
        PowerMock.verifyAll();
    }
}
