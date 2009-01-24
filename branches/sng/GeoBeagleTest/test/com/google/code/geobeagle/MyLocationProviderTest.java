
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
        LocationControl locationControl = createMock(LocationControl.class);
        ErrorDisplayer errorDisplayer = createMock(ErrorDisplayer.class);
        expect(locationControl.getLocation()).andReturn(null);

        errorDisplayer.displayError(R.string.error_cant_get_location);

        replay(locationControl);
        replay(errorDisplayer);
        MyLocationProvider myLocationProvider = new MyLocationProvider(locationControl, errorDisplayer);
        assertEquals(null, myLocationProvider.getLocation());
        verify(locationControl);
        verify(errorDisplayer);
    }

    public void test() {
        LocationControl locationControl = createMock(LocationControl.class);
        ErrorDisplayer errorDisplayer = createMock(ErrorDisplayer.class);
        Location location = createMock(Location.class);

        expect(locationControl.getLocation()).andReturn(location);

        replay(locationControl);
        MyLocationProvider myLocationProvider = new MyLocationProvider(locationControl, errorDisplayer);
        assertEquals(location, myLocationProvider.getLocation());
        verify(locationControl);
    }
}
