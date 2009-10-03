package com.google.code.geobeagle.database;


import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.google.code.geobeagle.Geocache;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.util.Log;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Log.class)
public class CachesProviderCountTest {

    private Geocache mockGeocache(double latitude, double longitude) {
        Geocache geocache = PowerMock.createMock(Geocache.class);
        expect(geocache.getLatitude()).andReturn(latitude).anyTimes();
        expect(geocache.getLongitude()).andReturn(longitude).anyTimes();
        return geocache;
    }

    private CachesProviderStub mArea;
    @Before
    public void setUp() {
        PowerMock.mockStatic(Log.class);
        expect(Log.d(isA(String.class), isA(String.class))).andReturn(0).anyTimes();
        
        mArea = new CachesProviderStub();
        //EasyMock.replay(Log.class);
    }
    
    @Test
    public void testEmptyDb() {
        PowerMock.replayAll();

        CachesProviderCount provider = new CachesProviderCount(mArea, 10, 100);
        assertEquals(provider.getCount(), 0);
        assertTrue(mArea.getSetBoundsCalls() <= CachesProviderCount.MAX_ITERATIONS);
    }

    @Test
    public void testTooFewHits() {
        mArea.addCache(mockGeocache(0, 0));
        
        PowerMock.replayAll();

        CachesProviderCount provider = new CachesProviderCount(mArea, 10, 100);
        assertEquals(provider.getCount(), 1);
        assertTrue(mArea.getSetBoundsCalls() <= CachesProviderCount.MAX_ITERATIONS);
    }

    @Test
    public void testTooManyHits() {
        for (int i = 0; i < 10; i++)
            mArea.addCache(mockGeocache(0, 0));
        
        PowerMock.replayAll();

        CachesProviderCount provider = new CachesProviderCount(mArea, 1, 5);
        assertEquals(provider.getCount(), 10);
        //Expect maximum number of searches:
        assertTrue(mArea.getSetBoundsCalls() == CachesProviderCount.MAX_ITERATIONS);
    }

    @Test
    public void testNormalOperation() {
        for (int i = 0; i < 10; i++)
            mArea.addCache(mockGeocache(0, i / 100.0));
        
        PowerMock.replayAll();

        CachesProviderCount provider = new CachesProviderCount(mArea, 5, 5);
        assertEquals(5, provider.getCount());
        assertTrue(mArea.getSetBoundsCalls() <= CachesProviderCount.MAX_ITERATIONS);
    }
    
    @Test
    public void testHasChanged() {
        CachesProviderCount provider = new CachesProviderCount(mArea, 10, 100);
        assertTrue(provider.hasChanged());
        provider.setChanged(false);
        assertTrue(!provider.hasChanged());
        provider.setChanged(true);
        assertTrue(provider.hasChanged());
        provider.setChanged(false);
        provider.setCenter(1, 0);
        assertTrue(provider.hasChanged());
        
        provider.setChanged(false);
        mArea.setChanged(true);
        assertTrue(provider.hasChanged());
    }
}
