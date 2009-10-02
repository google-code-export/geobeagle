package com.google.code.geobeagle.database;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.*;

import com.google.code.geobeagle.Geocache;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;

@RunWith(PowerMockRunner.class)
public class CachesProviderAreaTest {

    @Before
    public void setUp() {
    }
    
    @Test
    public void testExtraConditionSetsChanged() {
        DbFrontend dbFrontend = PowerMock.createMock(DbFrontend.class);
        PowerMock.replayAll();
        
        CachesProviderArea cachesProviderArea = new CachesProviderArea(dbFrontend);
        cachesProviderArea.setChanged(false);
        cachesProviderArea.setExtraCondition("CacheType = 1");
        assertTrue(cachesProviderArea.hasChanged());
    }

    @Test
    public void testExtraConditionGetNewCaches() {
        DbFrontend dbFrontend = PowerMock.createMock(DbFrontend.class);
        
        ArrayList<Geocache> list1 = new ArrayList<Geocache>();
        expect(dbFrontend.loadCaches(null)).andReturn(list1);
        expect(dbFrontend.loadCaches("CacheType = 1")).andReturn(list1);

        PowerMock.replayAll();
        
        CachesProviderArea cachesProviderArea = new CachesProviderArea(dbFrontend);
        cachesProviderArea.getCaches();
        cachesProviderArea.setChanged(false);
        cachesProviderArea.setExtraCondition("CacheType = 1");
        cachesProviderArea.getCaches();

        PowerMock.verifyAll();        
    }
}
