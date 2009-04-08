
package com.google.code.geobeagle.ui.cachelist;

import com.google.code.geobeagle.data.Geocache;
import com.google.code.geobeagle.data.GeocacheFromMyLocationFactory;
import com.google.code.geobeagle.io.LocationSaver;

import org.easymock.classextension.EasyMock;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

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
                geocacheListPresenter).act();
        PowerMock.verifyAll();
    }
}
