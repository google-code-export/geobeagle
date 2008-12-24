
package com.google.code.geobeagle;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import com.google.code.geobeagle.ui.ErrorDialog;
import com.google.code.geobeagle.ui.MyLocationProvider;

import android.location.Location;

import junit.framework.TestCase;

public class MyLocationProviderTest extends TestCase {
    public void testNullLocation() {
        GpsControl gpsControl = createMock(GpsControl.class);
        ErrorDialog errorDialog = createMock(ErrorDialog.class);
        expect(gpsControl.getLocation()).andReturn(null);

        errorDialog.show(R.string.error_cant_get_location);

        replay(gpsControl);
        replay(errorDialog);
        MyLocationProvider myLocationProvider = new MyLocationProvider(gpsControl, errorDialog);
        assertEquals(null, myLocationProvider.getLocation());
        verify(gpsControl);
        verify(errorDialog);
    }

    public void test() {
        GpsControl gpsControl = createMock(GpsControl.class);
        ErrorDialog errorDialog = createMock(ErrorDialog.class);
        Location location = createMock(Location.class);

        expect(gpsControl.getLocation()).andReturn(location);

        replay(gpsControl);
        MyLocationProvider myLocationProvider = new MyLocationProvider(gpsControl, errorDialog);
        assertEquals(location, myLocationProvider.getLocation());
        verify(gpsControl);
    }
}
