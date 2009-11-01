package com.google.code.geobeagle.database;

import static org.junit.Assert.assertEquals;
import static com.google.code.geobeagle.Common.mockGeocache;

import com.google.code.geobeagle.Geocache;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;

@RunWith(PowerMockRunner.class)
public class CachesProviderSortedTest {

    CachesProviderStub mProvider;
    ArrayList<Geocache> mSorted;

    @Before
    public void setUp() {
        mSorted = new ArrayList<Geocache>();
        Geocache cache1 = mockGeocache(1, 1);
        Geocache cache2 = mockGeocache(1, 2);
        mSorted.add(cache1);
        mSorted.add(cache2);
        
        mProvider = new CachesProviderStub();
        mProvider.addCache(cache2);
        mProvider.addCache(cache1);
    }
    
    @Test
    public void testSortedAtStart() {
        PowerMock.replayAll();

        CachesProviderSorted sorted = new CachesProviderSorted(mProvider);
        assertEquals(mSorted, sorted.getCaches());
    }

    @Test
    public void testSortedAfterSetCenter() {
        PowerMock.replayAll();

        CachesProviderSorted sorted = new CachesProviderSorted(mProvider);
        sorted.setCenter(0, 0);
        assertEquals(mSorted, sorted.getCaches());
    }
}
