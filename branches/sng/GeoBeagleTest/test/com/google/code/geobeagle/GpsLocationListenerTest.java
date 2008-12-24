
package com.google.code.geobeagle;

import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import com.google.code.geobeagle.ui.LocationViewer;

import android.location.Location;
import android.location.LocationProvider;

import junit.framework.TestCase;

public class GpsLocationListenerTest extends TestCase {

    public void testOnLocationChanged() {
        Location location = createMock(Location.class);
        LocationViewer locationViewer = createMock(LocationViewer.class);
        locationViewer.setLocation(location);

        replay(location);
        new GpsLocationListener(locationViewer).onLocationChanged(location);
        verify(location);
    }

    public void testOnStatusChange() {
        Location location = createMock(Location.class);
        LocationViewer locationViewer = createMock(LocationViewer.class);

        replay(location);
        new GpsLocationListener(locationViewer).onStatusChanged(null,
                LocationProvider.OUT_OF_SERVICE, null);
        verify(location);
    }

}
