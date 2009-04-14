
package com.google.code.geobeagle.ui.cachelist;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.data.Geocache;
import com.google.code.geobeagle.data.GeocacheFromMyLocationFactory;
import com.google.code.geobeagle.io.LocationSaver;
import com.google.code.geobeagle.ui.ErrorDisplayer;

import org.easymock.classextension.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class MenuActionMyLocationTest {

    @Test
    public void testAct() {
        LocationSaver locationSaver = PowerMock.createMock(LocationSaver.class);
        GeocacheFromMyLocationFactory geocacheFromMyLocationFactory = PowerMock
                .createMock(GeocacheFromMyLocationFactory.class);
        GeocacheListPresenter geocacheListPresenter = PowerMock
                .createMock(GeocacheListPresenter.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);

        EasyMock.expect(geocacheFromMyLocationFactory.create()).andReturn(geocache);
        locationSaver.saveLocation(geocache);
        geocacheListPresenter.onResume();

        PowerMock.replayAll();
        new MenuActionMyLocation(locationSaver, geocacheFromMyLocationFactory,
                geocacheListPresenter, null).act();
        PowerMock.verifyAll();
    }

    @Test
    public void testActNullLocation() {
        GeocacheFromMyLocationFactory geocacheFromMyLocationFactory = PowerMock
                .createMock(GeocacheFromMyLocationFactory.class);
        ErrorDisplayer errorDisplayer = PowerMock.createMock(ErrorDisplayer.class);

        EasyMock.expect(geocacheFromMyLocationFactory.create()).andReturn(null);
        errorDisplayer.displayError(R.string.current_location_null);

        PowerMock.replayAll();
        new MenuActionMyLocation(null, geocacheFromMyLocationFactory, null, errorDisplayer).act();
        PowerMock.verifyAll();
    }
}
