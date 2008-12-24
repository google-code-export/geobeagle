
package com.google.code.geobeagle;

import android.app.AlertDialog;
import android.location.Location;

import static org.easymock.classextension.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import junit.framework.TestCase;

public class MyLocationProviderTest extends TestCase {
    public void testNullLocation() {
        GpsControl gpsControl = createMock(GpsControl.class);
        AlertDialog alertDialog = createMock(AlertDialog.class);

        expect(gpsControl.getLocation()).andReturn(null);
        alertDialog
                .setMessage("Location cannot be determined.  Please ensure that your GPS is enabled and try again.");
        alertDialog.show();

        replay(gpsControl);
        replay(alertDialog);
        MyLocationProvider myLocationProvider = new MyLocationProvider(gpsControl, alertDialog);
        assertEquals(null, myLocationProvider.getLocation());
        verify(gpsControl);
        verify(alertDialog);
    }
    
    public void test() {
        GpsControl gpsControl = createMock(GpsControl.class);
        AlertDialog alertDialog = createMock(AlertDialog.class);
        Location location = createMock(Location.class);
        
        expect(gpsControl.getLocation()).andReturn(location);

        replay(gpsControl);
        MyLocationProvider myLocationProvider = new MyLocationProvider(gpsControl, alertDialog);
        assertEquals(location, myLocationProvider.getLocation());
        verify(gpsControl);
    }
}
