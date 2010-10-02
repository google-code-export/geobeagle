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
import com.google.code.geobeagle.activity.cachelist.GeoBeagleTest;
import com.google.code.geobeagle.formatting.DistanceFormatter;
import com.google.code.geobeagle.location.CombinedLocationManager;
import com.google.inject.Provider;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.content.Context;
import android.location.Location;
import android.location.LocationProvider;
import android.util.Log;
import android.widget.TextView;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        Context.class, TextView.class, Log.class
})
public class GpsStatusWidgetDelegateTest extends GeoBeagleTest {

    private InflatedGpsStatusWidget inflatedGpsStatusWidget;
    private Meter meter;
    private MeterFader meterFader;
    private TextView status;
    private TextLagUpdater textLagUpdater;
    private CombinedLocationManager combinedLocationManager;
    private Location location;

    @Before
    public void setUp() {
        inflatedGpsStatusWidget = PowerMock.createMock(InflatedGpsStatusWidget.class);
        meter = PowerMock.createMock(Meter.class);
        meterFader = PowerMock.createMock(MeterFader.class);
        status = PowerMock.createMock(TextView.class);
        textLagUpdater = PowerMock.createMock(TextLagUpdater.class);
        combinedLocationManager = PowerMock.createMock(CombinedLocationManager.class);
        location = PowerMock.createMock(Location.class);
    }

    @Test
    public void testGpsStatusWidget_OnLocationChangedNullLocation() {
        EasyMock.expect(inflatedGpsStatusWidget.findViewById(R.id.provider)).andReturn(null);
        EasyMock.expect(inflatedGpsStatusWidget.findViewById(R.id.status)).andReturn(null);

        PowerMock.replayAll();
        new GpsStatusWidgetDelegate(null, null, null, null, null, null, inflatedGpsStatusWidget)
                .onLocationChanged(null);
        PowerMock.verifyAll();
    }

    @Test
    public void testGpsWidget_SetEnabledDisabled() {
        status.setText("gps ENABLED");
        status.setText("gps DISABLED");
        EasyMock.expect(inflatedGpsStatusWidget.findViewById(R.id.provider)).andReturn(null);
        EasyMock.expect(inflatedGpsStatusWidget.findViewById(R.id.status)).andReturn(status);

        PowerMock.replayAll();
        GpsStatusWidgetDelegate gpsStatusWidget = new GpsStatusWidgetDelegate(null, null, null,
                null, null, null, inflatedGpsStatusWidget);
        gpsStatusWidget.onProviderEnabled("gps");
        gpsStatusWidget.onProviderDisabled("gps");
        PowerMock.verifyAll();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testOnLocationChanged() {
        DistanceFormatter distanceFormatter = PowerMock.createMock(DistanceFormatter.class);
        Provider<DistanceFormatter> distanceFormatterProvider = PowerMock
                .createMock(Provider.class);

        expect(inflatedGpsStatusWidget.findViewById(R.id.provider)).andReturn(null);
        expect(inflatedGpsStatusWidget.findViewById(R.id.status)).andReturn(null);
        expect(distanceFormatterProvider.get()).andReturn(distanceFormatter);
        expect(combinedLocationManager.isProviderEnabled()).andReturn(true);
        expect(location.getAccuracy()).andReturn(1.2f);
        expect(location.getTime()).andReturn(1000L);
        meter.setAccuracy(1.2f, distanceFormatter);
        meterFader.reset();
        textLagUpdater.reset(1000);

        PowerMock.replayAll();
        GpsStatusWidgetDelegate gpsStatusWidgetDelegate = new GpsStatusWidgetDelegate(
                combinedLocationManager, distanceFormatterProvider, meter, meterFader, null,
                textLagUpdater, inflatedGpsStatusWidget);
        gpsStatusWidgetDelegate.onLocationChanged(location);
        PowerMock.verifyAll();
    }

    @Test
    public void testOnLocationChangedProviderDisabled() {
        expect(inflatedGpsStatusWidget.findViewById(R.id.provider)).andReturn(null);
        expect(inflatedGpsStatusWidget.findViewById(R.id.status)).andReturn(null);
        expect(combinedLocationManager.isProviderEnabled()).andReturn(false);
        textLagUpdater.setDisabled();
        meter.setDisabled();

        PowerMock.replayAll();
        new GpsStatusWidgetDelegate(combinedLocationManager, null, meter, null, null,
                textLagUpdater, inflatedGpsStatusWidget).onLocationChanged(location);
        PowerMock.verifyAll();
    }

    @Test
    public void testPaint() {
        expect(inflatedGpsStatusWidget.findViewById(R.id.provider)).andReturn(null);
        expect(inflatedGpsStatusWidget.findViewById(R.id.status)).andReturn(null);
        meterFader.paint();

        PowerMock.replayAll();
        new GpsStatusWidgetDelegate(null, null, null, meterFader, null, null,
                inflatedGpsStatusWidget).paint();
        PowerMock.verifyAll();
    }

    @Test
    public void testSetStatus() {
        Context context = PowerMock.createMock(Context.class);

        expect(inflatedGpsStatusWidget.findViewById(R.id.provider)).andReturn(null);
        expect(inflatedGpsStatusWidget.findViewById(R.id.status)).andReturn(status);
        expect(context.getString(R.string.out_of_service)).andReturn("OUT OF SERVICE");
        expect(context.getString(R.string.available)).andReturn("AVAILABLE");
        expect(context.getString(R.string.temporarily_unavailable)).andReturn(
                "TEMPORARILY UNAVAILABLE");
        status.setText("gps status: OUT OF SERVICE");
        status.setText("network status: AVAILABLE");
        status.setText("gps status: TEMPORARILY UNAVAILABLE");

        PowerMock.replayAll();
        GpsStatusWidgetDelegate gpsStatusWidget = new GpsStatusWidgetDelegate(null, null, null,
                null, context, null, inflatedGpsStatusWidget);
        gpsStatusWidget.onStatusChanged("gps", LocationProvider.OUT_OF_SERVICE, null);
        gpsStatusWidget.onStatusChanged("network", LocationProvider.AVAILABLE, null);
        gpsStatusWidget.onStatusChanged("gps", LocationProvider.TEMPORARILY_UNAVAILABLE, null);
        PowerMock.verifyAll();
    }

}
