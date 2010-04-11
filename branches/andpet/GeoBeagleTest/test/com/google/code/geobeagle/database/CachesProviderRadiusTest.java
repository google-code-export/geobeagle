
package com.google.code.geobeagle.database;

import static org.junit.Assert.assertEquals;

import com.google.code.geobeagle.Geocache;

import org.easymock.EasyMock;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

import java.util.AbstractList;
import java.util.ArrayList;

public class CachesProviderRadiusTest {

    @Test
    public void testGetCaches() {
        ICachesProviderArea cachesProviderArea = PowerMock
                .createMock(ICachesProviderArea.class);
        AbstractList<Geocache> geocacheList = new ArrayList<Geocache>();

        EasyMock.expect(cachesProviderArea.getCaches()).andReturn(geocacheList);

        PowerMock.replayAll();
        assertEquals(geocacheList, new CachesProviderRadius(cachesProviderArea)
                .getCaches());
        PowerMock.verifyAll();
    }
}
