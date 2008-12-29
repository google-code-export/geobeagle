
package com.google.code.geobeagle;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import com.google.code.geobeagle.ui.ErrorDisplayer;
import com.google.code.geobeagle.ui.MyLocationProvider;

import android.location.Location;

import junit.framework.TestCase;

public class MyLocationProviderTest extends TestCase {
    public void testNullLocation() {
        GpsControl gpsControl = createMock(GpsControl.class);
        ErrorDisplayer errorDisplayer = createMock(ErrorDisplayer.class);
        expect(gpsControl.getLocation()).andReturn(null);

        errorDisplayer.displayError(R.string.error_cant_get_location);

        replay(gpsControl);
        replay(errorDisplayer);
        MyLocationProvider myLocationProvider = new MyLocationProvider(gpsControl, errorDisplayer);
        assertEquals(null, myLocationProvider.getLocation());
        verify(gpsControl);
        verify(errorDisplayer);
    }

    public void test() {
        GpsControl gpsControl = createMock(GpsControl.class);
        ErrorDisplayer errorDisplayer = createMock(ErrorDisplayer.class);
        Location location = createMock(Location.class);

        expect(gpsControl.getLocation()).andReturn(location);

        replay(gpsControl);
        MyLocationProvider myLocationProvider = new MyLocationProvider(gpsControl, errorDisplayer);
        assertEquals(location, myLocationProvider.getLocation());
        verify(gpsControl);
    }
}
