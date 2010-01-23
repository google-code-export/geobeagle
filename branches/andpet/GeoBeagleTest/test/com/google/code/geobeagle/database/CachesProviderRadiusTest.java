
package com.google.code.geobeagle.database;

import static org.junit.Assert.*;

import com.google.code.geobeagle.GeocacheList;

import org.easymock.EasyMock;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

public class CachesProviderRadiusTest {

    @Test
    public void testGetCaches() {
        ICachesProviderArea cachesProviderArea = PowerMock
                .createMock(ICachesProviderArea.class);
        GeocacheList geocacheList = PowerMock.createMock(GeocacheList.class);

        EasyMock.expect(cachesProviderArea.getCaches()).andReturn(geocacheList);

        PowerMock.replayAll();
        assertEquals(geocacheList, new CachesProviderRadius(cachesProviderArea)
                .getCaches());
        PowerMock.verifyAll();
    }
}
