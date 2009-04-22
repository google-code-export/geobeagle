
package com.google.code.geobeagle.ui.cachelist;

import static org.easymock.EasyMock.expect;

import com.google.code.geobeagle.data.GeocacheVector;
import com.google.code.geobeagle.data.GeocacheVectors;
import com.google.code.geobeagle.io.CacheWriter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class ContextActionDeleteTest {

    @Test
    public void testActionDelete() {
        CacheWriter cacheWriter = PowerMock.createMock(CacheWriter.class);
        GeocacheListAdapter geocacheListAdapter = PowerMock.createMock(GeocacheListAdapter.class);
        GeocacheVectors geocacheVectors = PowerMock.createMock(GeocacheVectors.class);
        GeocacheVector geocacheVector = PowerMock.createMock(GeocacheVector.class);

        expect(geocacheVectors.get(17)).andReturn(geocacheVector);
        expect(geocacheVector.getId()).andReturn("GC123");
        cacheWriter.deleteCache("GC123");
        geocacheVectors.remove(17);
        geocacheListAdapter.notifyDataSetChanged();

        PowerMock.replayAll();
        new ContextActionDelete(geocacheListAdapter, cacheWriter, geocacheVectors).act(17);
        PowerMock.verifyAll();
    }
}
