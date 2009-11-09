package com.google.code.geobeagle.actions;

import com.google.code.geobeagle.CacheType;
import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.GeocacheFactory;
import com.google.code.geobeagle.LocationAndDirection;
import com.google.code.geobeagle.GeocacheFactory.Source;
import com.google.code.geobeagle.activity.cachelist.actions.MenuActionMyLocation;

import org.easymock.classextension.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

import android.location.Location;

import java.util.Locale;
import java.util.TimeZone;

@RunWith(PowerMockRunner.class)
public class MenuActionMyLocationTest {

    @Test
    public void testCreate() {
        LocationAndDirection locationAndDirection = PowerMock
                .createMock(LocationAndDirection.class);
        Location location = PowerMock.createMock(Location.class);
        GeocacheFactory geocacheFactory = PowerMock.createMock(GeocacheFactory.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);
        Locale.setDefault(Locale.ENGLISH);
        TimeZone.setDefault(TimeZone.getTimeZone("America/Los_Angeles"));

        EasyMock.expect(locationAndDirection.getLocation()).andReturn(location);
        EasyMock.expect(location.getTime()).andReturn(1000000L);
        EasyMock.expect(location.getLatitude()).andReturn(37.0);
        EasyMock.expect(location.getLongitude()).andReturn(-122.0);
        EasyMock.expect(
                geocacheFactory.create("ML161640", "[16:16] My Location", 37.0, -122.0,
                        Source.MY_LOCATION, null, CacheType.MY_LOCATION, 0, 0, 0)).andReturn(
                geocache);

        PowerMock.replayAll();
        new MenuActionMyLocation(null, null, geocacheFactory, locationAndDirection, null, null, null).act();
        PowerMock.verifyAll();
    }


}
