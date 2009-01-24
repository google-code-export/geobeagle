
package com.google.code.geobeagle;

import static org.easymock.classextension.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import com.google.code.geobeagle.ui.LocationViewer;

import android.location.Location;
import android.location.LocationProvider;

import junit.framework.TestCase;

public class GpsLocationListenerTest extends TestCase {

    private LocationViewer mLocationViewer;
    private LocationControl mGpsControl;

    public void setUp() {
        mLocationViewer = createMock(LocationViewer.class);
        mGpsControl = createMock(LocationControl.class);
    }

    public void testOnLocationChanged() {
        Location location = createMock(Location.class);
        expect(mGpsControl.getLocation()).andReturn(location);
        mLocationViewer.setLocation(location);

        replay(location);
        replay(mGpsControl);
        new GeoBeagleLocationListener(mGpsControl, mLocationViewer).onLocationChanged(location);
        verify(location);
        verify(mGpsControl);
    }

    public void testOnStatusChange() {
        mLocationViewer.setStatus("gps", LocationProvider.OUT_OF_SERVICE);

        replay(mLocationViewer);
        new GeoBeagleLocationListener(null, mLocationViewer).onStatusChanged("gps",
                LocationProvider.OUT_OF_SERVICE, null);
        verify(mLocationViewer);
    }

    public void testOnEnabled() {
        mLocationViewer.setEnabled();

        replay(mLocationViewer);
        new GeoBeagleLocationListener(null, mLocationViewer).onProviderEnabled(null);
        verify(mLocationViewer);
    }

    public void testOnDisabled() {
        mLocationViewer.setDisabled();

        replay(mLocationViewer);
        new GeoBeagleLocationListener(null, mLocationViewer).onProviderDisabled(null);
        verify(mLocationViewer);

    }
}
