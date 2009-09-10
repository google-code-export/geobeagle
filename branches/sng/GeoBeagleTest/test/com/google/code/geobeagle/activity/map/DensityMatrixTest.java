package com.google.code.geobeagle.activity.map;


import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import com.google.code.geobeagle.Geocache;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        DensityMatrix.class
})
public class DensityMatrixTest {
	@Test
    public void testTwoPatches() {
        Geocache cache1 = PowerMock.createMock(Geocache.class);
        Geocache cache2 = PowerMock.createMock(Geocache.class);

        EasyMock.expect(cache1.getLatitude()).andReturn(0.05);
        EasyMock.expect(cache1.getLongitude()).andReturn(0.05);
        EasyMock.expect(cache2.getLatitude()).andReturn(0.15);
        EasyMock.expect(cache2.getLongitude()).andReturn(0.05);

        PowerMock.replayAll();
        
        ArrayList<Geocache> caches = new ArrayList<Geocache>();
        caches.add(cache1);
        caches.add(cache2);
        DensityMatrix matrix = new DensityMatrix(0.1, 0.1);
        matrix.addCaches(caches);
        assertEquals(2, matrix.getDensityPatches().size());
        
        PowerMock.verifyAll();
    }
	
}
