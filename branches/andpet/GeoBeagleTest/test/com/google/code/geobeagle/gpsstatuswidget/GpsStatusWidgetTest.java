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

import com.google.code.geobeagle.Clock;
import com.google.code.geobeagle.GeoFix;
import com.google.code.geobeagle.GeoFixProvider;
import com.google.code.geobeagle.GeoFixProviderLive;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.formatting.DistanceFormatter;
import com.google.code.geobeagle.gpsstatuswidget.GpsStatusWidgetDelegate.TimeProvider;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.content.Context;
import android.graphics.Color;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

@PrepareForTest( {
        Bundle.class, Color.class, Handler.class, TextView.class, Context.class
})


@RunWith(PowerMockRunner.class)
public class GpsStatusWidgetTest {
    @Test
    public void testFadeMeter() {
        View parent = PowerMock.createMock(View.class);
        Clock clock = PowerMock.createMock(Clock.class);
        MeterBars meterBars = PowerMock.createMock(MeterBars.class);

        EasyMock.expect(clock.getCurrentTime()).andReturn(1000L);
        meterBars.setLag(0);
        parent.postInvalidateDelayed(100);

        PowerMock.replayAll();
        new MeterFader(parent, meterBars, clock).paint();
        // Log.d("GeoBeagle", "painting " + lastUpdateLag);
        PowerMock.verifyAll();
    }

    @Test
    public void testFadeMeterLastDelay() {
        View parent = PowerMock.createMock(View.class);
        Clock clock = PowerMock.createMock(Clock.class);
        MeterBars meterBars = PowerMock.createMock(MeterBars.class);

        EasyMock.expect(clock.getCurrentTime()).andReturn(1000L);
        meterBars.setLag(0);
        parent.postInvalidateDelayed(100);

        EasyMock.expect(clock.getCurrentTime()).andReturn(2000L);
        meterBars.setLag(1000);

        PowerMock.replayAll();
        final MeterFader meterFader = new MeterFader(parent, meterBars, clock);
        meterFader.paint();
        meterFader.paint();
        // Log.d("GeoBeagle", "painting " + lastUpdateLag);
        PowerMock.verifyAll();
    }

    @Test
    public void testFadeMeterReset() {
        View parent = PowerMock.createMock(View.class);
        Clock clock = PowerMock.createMock(Clock.class);
        MeterBars meterBars = PowerMock.createMock(MeterBars.class);

        EasyMock.expect(clock.getCurrentTime()).andReturn(1000L);
        meterBars.setLag(0);
        parent.postInvalidateDelayed(100);

        EasyMock.expect(clock.getCurrentTime()).andReturn(1100L);
        meterBars.setLag(100);
        parent.postInvalidateDelayed(100);

        parent.postInvalidate();

        EasyMock.expect(clock.getCurrentTime()).andReturn(1200L);
        meterBars.setLag(0);
        parent.postInvalidateDelayed(100);

        PowerMock.replayAll();
        final MeterFader meterFader = new MeterFader(parent, meterBars, clock);
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
        Clock clock = PowerMock.createMock(Clock.class);
        MeterBars meterBars = PowerMock.createMock(MeterBars.class);

        EasyMock.expect(clock.getCurrentTime()).andReturn(1000L);
        meterBars.setLag(0);
        parent.postInvalidateDelayed(100);

        EasyMock.expect(clock.getCurrentTime()).andReturn(1100L);
        meterBars.setLag(100);
        parent.postInvalidateDelayed(100);

        PowerMock.replayAll();
        final MeterFader meterFader = new MeterFader(parent, meterBars, clock);
        meterFader.paint();
        meterFader.paint();
        // Log.d("GeoBeagle", "painting " + lastUpdateLag);
        PowerMock.verifyAll();
    }

    @Test
    public void testForceRefresh() {
        MeterFader meterFader = PowerMock.createMock(MeterFader.class);

        meterFader.paint();

        PowerMock.replayAll();
        new GpsStatusWidgetDelegate(null, null, null, meterFader, null, null,
                null, null, null, null).paint();
        PowerMock.verifyAll();
    }
    
    @Test
    public void testGpsStatusWidget_OnLocationChangedNullLocation() {
        Meter meter = PowerMock.createMock(Meter.class);
        GeoFixProvider geoFixProvider = PowerMock.createMock(GeoFixProvider.class);
        GeoFix geoFix = PowerMock.createMock(GeoFix.class);
        TextView provider = PowerMock.createMock(TextView.class);
        MeterFader meterFader = PowerMock.createMock(MeterFader.class);
        DistanceFormatter distanceFormatter = PowerMock.createMock(DistanceFormatter.class);
        TextView lagTextView = PowerMock.createMock(TextView.class);
        TimeProvider timeProvider = PowerMock.createMock(TimeProvider.class);

        EasyMock.expect(geoFixProvider.getLocation()).andReturn(geoFix);
        EasyMock.expect(geoFix.getProvider()).andReturn("gps");
        EasyMock.expect(geoFix.getAccuracy()).andReturn(1.2f);
        meter.setAccuracy(1.2f, distanceFormatter);
        meterFader.reset();
        EasyMock.expect(timeProvider.getTime()).andReturn(1000L);
        
        EasyMock.expect(geoFix.getLagString(1000L)).andReturn("lag 1000");
        lagTextView.setText("lag 1000");
        
        provider.setText("gps");
        
        PowerMock.replayAll();
        new GpsStatusWidgetDelegate(geoFixProvider, distanceFormatter, meter,
                meterFader, provider, null, null, lagTextView, null, timeProvider).refresh();
        PowerMock.verifyAll();
    }

    // Removed @Test, see todo below.
    public void testGpsWidget_SetEnabledDisabled() {
        TextView status = PowerMock.createMock(TextView.class);
        PowerMock.suppressConstructor(LinearLayout.class);

        status.setText("gps ENABLED");
        status.setText("gps DISABLED");

        PowerMock.replayAll();
        GpsStatusWidgetDelegate gpsStatusWidget = new GpsStatusWidgetDelegate(
                null, null, null, null, null, null, status, null, null, null);
        //TODO: GPS widget no longer gets provider status changes... it should get the info somehow
//        gpsStatusWidget.onProviderEnabled("gps");
//        gpsStatusWidget.onProviderDisabled("gps");
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
        TextView provider = PowerMock.createMock(TextView.class);
        GeoFix location = PowerMock.createMock(GeoFix.class);
        GeoFixProvider geoFixProvider = PowerMock.createMock(GeoFixProviderLive.class);
        DistanceFormatter distanceFormatter = PowerMock.createMock(DistanceFormatter.class);
        TimeProvider timeProvider = PowerMock.createMock(TimeProvider.class);
        TextView lagTextView = PowerMock.createMock(TextView.class);

        EasyMock.expect(geoFixProvider.getLocation()).andReturn(location);
        EasyMock.expect(location.getProvider()).andReturn("gps");
        EasyMock.expect(location.getAccuracy()).andReturn(1.2f);
        EasyMock.expect(timeProvider.getTime()).andReturn(1010L);
        provider.setText("gps");
        EasyMock.expect(location.getLagString(1010L)).andReturn("lag 1010");
        meter.setAccuracy(1.2f, distanceFormatter);
        meterFader.reset();
        lagTextView.setText("lag 1010");

        PowerMock.replayAll();
        final GpsStatusWidgetDelegate gpsStatusWidgetDelegate = new GpsStatusWidgetDelegate(
                geoFixProvider, null, meter, meterFader, provider, null, null,
                lagTextView, null, timeProvider);
        gpsStatusWidgetDelegate.setDistanceFormatter(distanceFormatter);
        gpsStatusWidgetDelegate.forceRefresh();
        PowerMock.verifyAll();
    }

    public void testOnLocationChangedProviderDisabled() {
        Meter meter = PowerMock.createMock(Meter.class);
        GeoFix location = PowerMock.createMock(GeoFix.class);
        GeoFixProvider geoFixProvider = PowerMock.createMock(GeoFixProviderLive.class);

        EasyMock.expect(geoFixProvider.isProviderEnabled()).andReturn(false);
        EasyMock.expect(geoFixProvider.getLocation()).andReturn(location);
        meter.setDisabled();

        PowerMock.replayAll();
        new GpsStatusWidgetDelegate(geoFixProvider, null, meter, null, null,
                null, null, null, null, null).refresh();
        PowerMock.verifyAll();
    }

    @Test
    public void testPaint() {
        MeterFader meterFader = PowerMock.createMock(MeterFader.class);

        meterFader.paint();

        PowerMock.replayAll();
        new GpsStatusWidgetDelegate(null, null, null, meterFader, null, null,
                null, null, null, null).paint();
        PowerMock.verifyAll();
    }

    @Test
    public void testSetStatus() {
        Context context = PowerMock.createMock(Context.class);
        TextView status = PowerMock.createMock(TextView.class);
        DistanceFormatter distanceFormatter = PowerMock.createMock(DistanceFormatter.class);

        PowerMock.suppressConstructor(LinearLayout.class);

        EasyMock.expect(context.getString(R.string.out_of_service)).andReturn("OUT OF SERVICE");
        EasyMock.expect(context.getString(R.string.available)).andReturn("AVAILABLE");
        EasyMock.expect(context.getString(R.string.temporarily_unavailable)).andReturn(
                "TEMPORARILY UNAVAILABLE");
        status.setText("gps status: OUT OF SERVICE");
        status.setText("network status: AVAILABLE");
        status.setText("gps status: TEMPORARILY UNAVAILABLE");

        PowerMock.replayAll();
        GpsStatusWidgetDelegate gpsStatusWidget = new GpsStatusWidgetDelegate(
                null, distanceFormatter, null, null, null, context, status,
                null, null, null);
        gpsStatusWidget.onStatusChanged("gps", LocationProvider.OUT_OF_SERVICE, null);
        gpsStatusWidget.onStatusChanged("network", LocationProvider.AVAILABLE, null);
        gpsStatusWidget.onStatusChanged("gps", LocationProvider.TEMPORARILY_UNAVAILABLE, null);
        PowerMock.verifyAll();
    }

    @Test
    public void testUpdateLagText() {
        GeoFix geoFix = PowerMock.createMock(GeoFix.class);
        TextView lagTextView = PowerMock.createMock(TextView.class);
        
        EasyMock.expect(geoFix.getLagString(1000)).andReturn("lag 1000");
        lagTextView.setText("lag 1000");
        
        PowerMock.replayAll();
        new GpsStatusWidgetDelegate(null, null, null, null, lagTextView, null,
                lagTextView, lagTextView, geoFix, null).updateLagText(1000);
        PowerMock.verifyAll();
    }
    
    @Test
    public void testUpdateGpsWidgetRunnable() {
        GeoFixProvider geoFixProvider = PowerMock
                .createMock(GeoFixProviderLive.class);
        Meter meter = PowerMock.createMock(Meter.class);
        Handler handler = PowerMock.createMock(Handler.class);
        GpsStatusWidgetDelegate gpsStatusWidgetDelegate = PowerMock
                .createMock(GpsStatusWidgetDelegate.class);
        TimeProvider timeProvider = PowerMock.createMock(TimeProvider.class);
        
        EasyMock.expect(timeProvider.getTime()).andReturn(1000L);
        gpsStatusWidgetDelegate.updateLagText(1000L);
        EasyMock.expect(geoFixProvider.getAzimuth()).andReturn(42f);
        meter.setAzimuth(42f);
        EasyMock
                .expect(
                        handler.postDelayed(EasyMock.isA(UpdateGpsWidgetRunnable.class), EasyMock
                                .eq(500L))).andReturn(true);

        PowerMock.replayAll();
        new UpdateGpsWidgetRunnable(handler, geoFixProvider, meter,
                gpsStatusWidgetDelegate, timeProvider).run();
        PowerMock.verifyAll();
    }
}
