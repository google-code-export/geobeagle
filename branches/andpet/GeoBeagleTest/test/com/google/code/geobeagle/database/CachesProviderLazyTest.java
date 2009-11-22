package com.google.code.geobeagle.database;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.*;

import com.google.code.geobeagle.Clock;
import com.google.code.geobeagle.GeocacheList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class CachesProviderLazyTest {
    ICachesProviderCenter mProvider;
    Clock mClock;
    GeocacheList mList1;
    GeocacheList mList2;
    final double MINDIST = 2.0;
    final long MINTIME = 1000;

    @Before
    public void setUp() {
        mProvider = PowerMock.createMock(ICachesProviderCenter.class);
        mClock = PowerMock.createMock(Clock.class);
        mList1 = PowerMock.createMock(GeocacheList.class);
        mList2 = PowerMock.createMock(GeocacheList.class);
        mList2.add(null);
        expect(mClock.getCurrentTime()).andReturn(0L);
    }
    
    @Test
    public void testHasChangedAtStartup() {
        PowerMock.replayAll();
        
        CachesProviderLazy lazy = new CachesProviderLazy(mProvider, MINDIST, MINTIME, mClock);

        assertTrue(lazy.hasChanged());
        lazy.resetChanged();
        assertFalse(lazy.hasChanged());
        
        //PowerMock.verifyAll();
    }

    @Test
    /** Loading the list directly after initialization */
    public void testDoFirstLoading() {
        expect(mProvider.getCaches()).andReturn(mList1);
        PowerMock.replayAll();
        
        CachesProviderLazy lazy = new CachesProviderLazy(mProvider, MINDIST, MINTIME, mClock);

        assertEquals(mList1, lazy.getCaches());
        PowerMock.verifyAll();
    }

    @Test
    /** Getting the list the second time doesn't reload the list */
    public void testLoadingTwice() {
        expect(mProvider.getCaches()).andReturn(mList1);
        PowerMock.replayAll();
        
        CachesProviderLazy lazy = new CachesProviderLazy(mProvider, MINDIST, MINTIME, mClock);

        assertEquals(mList1, lazy.getCaches());
        assertEquals(mList1, lazy.getCaches());
        PowerMock.verifyAll();
    }
    
    @Test
    /** Move the center and expect a new list to be loaded */
    public void testChanged() {
        expect(mClock.getCurrentTime()).andReturn(2000L);
        expect(mProvider.getCaches()).andReturn(mList1);
        mProvider.setCenter(1, 1);
        expect(mProvider.getCaches()).andReturn(mList2);
        PowerMock.replayAll();

        CachesProviderLazy lazy = new CachesProviderLazy(mProvider, MINDIST, MINTIME, mClock);
        assertEquals(mList1, lazy.getCaches());
        lazy.setCenter(1, 1);
        assertTrue(lazy.hasChanged());
        assertEquals(mList2, lazy.getCaches());
        PowerMock.verifyAll();
    }

    @Test
    /** Not enough time passed for the change to become visible */
    public void testChangedTooSoon() {
        expect(mClock.getCurrentTime()).andReturn(500L);
        mProvider.setCenter(1, 1);
        PowerMock.replayAll();

        CachesProviderLazy lazy = new CachesProviderLazy(mProvider, MINDIST, MINTIME, mClock);
        lazy.resetChanged();
        lazy.setCenter(1, 1);
        assertFalse(lazy.hasChanged());
        PowerMock.verifyAll();
    }

    @Test
    /** A large enough change will be carried out once enough time passed */
    public void testDelayedChange() {
        expect(mClock.getCurrentTime()).andReturn(500L);
        expect(mProvider.getCaches()).andReturn(mList1);
        mProvider.setCenter(1, 1);
        expect(mProvider.getCaches()).andReturn(mList2);
        expect(mClock.getCurrentTime()).andReturn(1500L);
        PowerMock.replayAll();

        CachesProviderLazy lazy = new CachesProviderLazy(mProvider, MINDIST, MINTIME, mClock);
        lazy.resetChanged();
        lazy.setCenter(1, 1);
        assertFalse(lazy.hasChanged());
        assertEquals(0, lazy.getCount());
        assertEquals(mList1, lazy.getCaches());
        //Enough time has passed...
        assertTrue(lazy.hasChanged());
        assertEquals(1, lazy.getCount());
        assertEquals(mList2, lazy.getCaches());
        PowerMock.verifyAll();
    }

    @Test
    /** The change is not big enough to signal with hasChanged() */
    public void testChangeTooSmall() {
        expect(mClock.getCurrentTime()).andReturn(2000L);
        mProvider.setCenter(0.0001, 0.0001);
        PowerMock.replayAll();

        CachesProviderLazy lazy = new CachesProviderLazy(mProvider, MINDIST, MINTIME, mClock);
        lazy.resetChanged();
        lazy.setCenter(1, 1);
        assertFalse(lazy.hasChanged());
        PowerMock.verifyAll();
    } 

    @Test
    /** A small change won't be carried out even after enough time passed */
    public void testDelayedNoChange() {
        expect(mClock.getCurrentTime()).andReturn(500L);
        expect(mProvider.getCaches()).andReturn(mList1);
        mProvider.setCenter(0.0001, 0.0001);
        expect(mClock.getCurrentTime()).andReturn(1500L);
        PowerMock.replayAll();

        CachesProviderLazy lazy = new CachesProviderLazy(mProvider, MINDIST, MINTIME, mClock);
        lazy.resetChanged();
        lazy.setCenter(1, 1);
        assertFalse(lazy.hasChanged());
        assertEquals(mList1, lazy.getCaches());
        assertEquals(0, lazy.getCount());
        //Enough time has passed...
        assertFalse(lazy.hasChanged());
        assertEquals(mList1, lazy.getCaches());
        assertEquals(0, lazy.getCount());
        PowerMock.verifyAll();
    }
}
