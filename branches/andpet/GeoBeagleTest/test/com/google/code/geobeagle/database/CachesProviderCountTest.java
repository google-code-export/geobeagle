package com.google.code.geobeagle.database;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.google.code.geobeagle.Geocache;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class CachesProviderCountTest {

    private Geocache mockGeocache(double latitude, double longitude) {
        Geocache geocache = PowerMock.createMock(Geocache.class);
        expect(geocache.getLatitude()).andReturn(latitude).anyTimes();
        expect(geocache.getLongitude()).andReturn(longitude).anyTimes();
        return geocache;
    }
    
    @Test
    public void testEmptyDb() {
        CachesProviderStub area = new CachesProviderStub();
        
        PowerMock.replayAll();

        CachesProviderCount provider = new CachesProviderCount(area, 10, 100);
        assertEquals(provider.getCount(), 0);
        assertTrue(area.getSetRadiusCalls() <= CachesProviderCount.MAX_ITERATIONS);
    }

    @Test
    public void testTooFewHits() {
        CachesProviderStub area = new CachesProviderStub();
        area.addCache(mockGeocache(0, 0));
        
        PowerMock.replayAll();

        CachesProviderCount provider = new CachesProviderCount(area, 10, 100);
        assertEquals(provider.getCount(), 1);
        assertTrue(area.getSetRadiusCalls() <= CachesProviderCount.MAX_ITERATIONS);
    }

    @Test
    public void testTooManyHits() {
        CachesProviderStub area = new CachesProviderStub();
        for (int i = 0; i < 10; i++)
            area.addCache(mockGeocache(0, 0));
        
        PowerMock.replayAll();

        CachesProviderCount provider = new CachesProviderCount(area, 1, 5);
        assertEquals(provider.getCount(), 10);
        //Expect maximum number of searches:
        assertTrue(area.getSetRadiusCalls() == CachesProviderCount.MAX_ITERATIONS);
    }

    @Test
    public void testNormalOperation() {
        CachesProviderStub area = new CachesProviderStub();
        for (int i = 0; i < 10; i++)
            area.addCache(mockGeocache(0, i / 100.0));
        
        PowerMock.replayAll();

        CachesProviderCount provider = new CachesProviderCount(area, 5, 5);
        assertEquals(5, provider.getCount());
        assertTrue(area.getSetRadiusCalls() <= CachesProviderCount.MAX_ITERATIONS);
    }
    
    @Test
    public void testHasChanged() {
        CachesProviderStub area = new CachesProviderStub();
        CachesProviderCount provider = new CachesProviderCount(area, 10, 100);
        assertTrue(provider.hasChanged());
        provider.setChanged(false);
        assertTrue(!provider.hasChanged());
        provider.setChanged(true);
        assertTrue(provider.hasChanged());
        provider.setChanged(false);
        provider.setCenter(1, 0);
        assertTrue(provider.hasChanged());
        
        provider.setChanged(false);
        area.setChanged(true);
        assertTrue(provider.hasChanged());
    }
}
