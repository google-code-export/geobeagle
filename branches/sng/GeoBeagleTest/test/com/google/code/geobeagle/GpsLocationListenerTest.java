
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
    private GpsControl mGpsControl;

    public void setUp() {
        mLocationViewer = createMock(LocationViewer.class);
        mGpsControl = createMock(GpsControl.class);
    }

    public void testOnLocationChanged() {
        Location location = createMock(Location.class);
        expect(mGpsControl.getLocation()).andReturn(location);
        mLocationViewer.setLocation(location);

        replay(location);
        replay(mGpsControl);
        new GpsLocationListener(mGpsControl, mLocationViewer).onLocationChanged(location);
        verify(location);
        verify(mGpsControl);
    }

    public void testOnStatusChange() {
        mLocationViewer.setStatus(LocationProvider.OUT_OF_SERVICE);

        replay(mLocationViewer);
        new GpsLocationListener(null, mLocationViewer).onStatusChanged(null,
                LocationProvider.OUT_OF_SERVICE, null);
        verify(mLocationViewer);
    }

    public void testOnEnabled() {
        mLocationViewer.setEnabled();

        replay(mLocationViewer);
        new GpsLocationListener(null, mLocationViewer).onProviderEnabled(null);
        verify(mLocationViewer);
    }

    public void testOnDisabled() {
        mLocationViewer.setDisabled();

        replay(mLocationViewer);
        new GpsLocationListener(null, mLocationViewer).onProviderDisabled(null);
        verify(mLocationViewer);

    }
}
