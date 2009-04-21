
package com.google.code.geobeagle.ui.cachelist.row;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.code.geobeagle.ui.cachelist.row.GpsWidgetRowInflater.GpsWidgetRowViews;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

import android.view.View;

@RunWith(PowerMockRunner.class)
public class GpsWidgetRowInflaterTest {

    @Test
    public void testGetType() {
        PowerMock.replayAll();
        assertEquals(RowType.GpsWidgetRow, new GpsWidgetRowViews().getType());
        PowerMock.verifyAll();
    }

    @Test
    public void testInflateMatch() {
        View gpsWidget = PowerMock.createMock(View.class);

        PowerMock.replayAll();
        assertEquals(gpsWidget, new GpsWidgetRowInflater(gpsWidget).inflate(gpsWidget));
        PowerMock.verifyAll();
    }

    @Test
    public void testInflateNoMatch() {
        View gpsWidget = PowerMock.createMock(View.class);
        View convertView = PowerMock.createMock(View.class);

        PowerMock.replayAll();
        assertEquals(gpsWidget, new GpsWidgetRowInflater(gpsWidget).inflate(convertView));
        PowerMock.verifyAll();
    }

    @Test
    public void testInflateNull() {
        View gpsWidget = PowerMock.createMock(View.class);

        PowerMock.replayAll();
        assertEquals(gpsWidget, new GpsWidgetRowInflater(gpsWidget).inflate(null));
        PowerMock.verifyAll();
    }

    @Test
    public void testMatch() {
        View gpsWidget = PowerMock.createMock(View.class);

        PowerMock.replayAll();
        assertTrue(new GpsWidgetRowInflater(gpsWidget).match(0));
        PowerMock.verifyAll();
    }

    @Test
    public void testMatchNot() {
        View gpsWidget = PowerMock.createMock(View.class);

        PowerMock.replayAll();
        assertFalse(new GpsWidgetRowInflater(gpsWidget).match(1));
        PowerMock.verifyAll();
    }

    @Test
    public void testSetData() {
        View gpsWidget = PowerMock.createMock(View.class);
        View view = PowerMock.createMock(View.class);

        PowerMock.replayAll();
        new GpsWidgetRowInflater(gpsWidget).setData(view, 12);
        PowerMock.verifyAll();
    }
}
