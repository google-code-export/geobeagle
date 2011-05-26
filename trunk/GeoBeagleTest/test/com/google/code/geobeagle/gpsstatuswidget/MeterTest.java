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
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.cachelist.GeoBeagleTest;
import com.google.code.geobeagle.formatting.DistanceFormatter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        Bundle.class, Color.class, Handler.class, LinearLayout.class, TextView.class,
        GpsStatusWidget.class, Context.class, Log.class
})
public class MeterTest extends GeoBeagleTest {
    private MeterBars meterBars;
    private InflatedGpsStatusWidget inflatedGpsStatusWidget;
    private TextView accuracyView;
    private DistanceFormatter distanceFormatter;

    @Before
    public void setUp() {
        meterBars = createMock(MeterBars.class);
        inflatedGpsStatusWidget = createMock(InflatedGpsStatusWidget.class);
        accuracyView = createMock(TextView.class);
        distanceFormatter = createMock(DistanceFormatter.class);
        expect(inflatedGpsStatusWidget.findViewById(R.id.accuracy))
                .andReturn(accuracyView);
    }

    @Test
    public void testMeterWrapper_SetAccuracyAzimuth() {
        expect(distanceFormatter.formatDistance(1.2f)).andReturn("1m");
        accuracyView.setText("1m");
        meterBars.set(1.2f, 0);

        expect(distanceFormatter.formatDistance(1.2f)).andReturn("1m");
        meterBars.set(1.2f, 280);

        expect(distanceFormatter.formatDistance(2.2f)).andReturn("2m");
        accuracyView.setText("2m");
        meterBars.set(2.2f, 280);
        expect(distanceFormatter.formatDistance(2.2f)).andReturn("2m");

        replayAll();
        Meter meter = new Meter(meterBars, inflatedGpsStatusWidget, new MeterState());
        meter.setAccuracy(1.2f, distanceFormatter);
        meter.setAzimuth(280);
        meter.setAccuracy(2.2f, distanceFormatter);
        verifyAll();
    }

    @Test
    public void testMeterWrapper_SetDisabled() {
        accuracyView.setText("");
        meterBars.set(Float.MAX_VALUE, 0);

        replayAll();
        new Meter(meterBars, inflatedGpsStatusWidget, new MeterState()).setDisabled();
        verifyAll();
    }
}
