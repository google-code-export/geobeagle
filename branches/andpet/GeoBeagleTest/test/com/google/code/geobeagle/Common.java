package com.google.code.geobeagle;

import static org.easymock.EasyMock.expect;

import org.powermock.api.easymock.PowerMock;

public class Common {

    public static Geocache mockGeocache(double latitude, double longitude) {
        Geocache geocache = PowerMock.createMock(Geocache.class);
        expect(geocache.getLatitude()).andReturn(latitude).anyTimes();
        expect(geocache.getLongitude()).andReturn(longitude).anyTimes();
        return geocache;
    }    
    
    
}
