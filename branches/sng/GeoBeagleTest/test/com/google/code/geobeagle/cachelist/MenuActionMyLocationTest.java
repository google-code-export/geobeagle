
package com.google.code.geobeagle.cachelist;

import com.google.code.geobeagle.ErrorDisplayer;
import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.cachelist.actions.menu.MenuActionMyLocation;
import com.google.code.geobeagle.activity.cachelist.model.GeocacheFromMyLocationFactory;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh;
import com.google.code.geobeagle.database.LocationSaver;

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
        CacheListRefresh cacheListRefresh = PowerMock.createMock(CacheListRefresh.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);

        EasyMock.expect(geocacheFromMyLocationFactory.create()).andReturn(geocache);
        locationSaver.saveLocation(geocache);
        cacheListRefresh.forceRefresh();

        PowerMock.replayAll();
        new MenuActionMyLocation(locationSaver, geocacheFromMyLocationFactory, cacheListRefresh,
                null).act();
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
