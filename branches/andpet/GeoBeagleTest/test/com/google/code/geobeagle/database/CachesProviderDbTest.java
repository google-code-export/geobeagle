package com.google.code.geobeagle.database;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.junit.Assert.*;

import com.google.code.geobeagle.CacheFilter;
import com.google.code.geobeagle.GeocacheList;
import com.google.code.geobeagle.GeocacheListPrecomputed;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.util.Log;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Log.class)
public class CachesProviderDbTest {
    private DbFrontend mDbFrontend;
    private CacheFilter mCacheFilter1;
    private CacheFilter mCacheFilter2;
    CachesProviderDb mCachesProviderDb;

    @Before
    public void setUp() {
        PowerMock.mockStatic(Log.class);
        expect(Log.d(isA(String.class), isA(String.class))).andReturn(0).anyTimes();
        mDbFrontend = PowerMock.createMock(DbFrontend.class);
        mCacheFilter1 = PowerMock.createMock(CacheFilter.class);
        mCacheFilter2 = PowerMock.createMock(CacheFilter.class);
        mCachesProviderDb = new CachesProviderDb(mDbFrontend, mCacheFilter1);
    }
    
    @Test
    public void testFilterUpdateSetsChanged() {
        expect(mCacheFilter1.getSqlWhereClause()).andReturn("1");
        expect(mCacheFilter2.getSqlWhereClause()).andReturn("2");
        PowerMock.replayAll();
        
        mCachesProviderDb.resetChanged();
        mCachesProviderDb.setFilter(mCacheFilter2);
        assertTrue(mCachesProviderDb.hasChanged());
    }

    @Test
    public void testUnchangedFilter() {
        expect(mCacheFilter1.getSqlWhereClause()).andReturn("CacheType = 1").anyTimes();
        expect(mDbFrontend.count("CacheType = 1")).andReturn(5);
        PowerMock.replayAll();
        
        mCachesProviderDb.getCount();  //will load the filter
        mCachesProviderDb.resetChanged();
        mCachesProviderDb.setFilter(mCacheFilter2);
        assertFalse(mCachesProviderDb.hasChanged());
    }
    
    @Test
    public void testExtraConditionGetNewCaches() {
        GeocacheList list1 = new GeocacheListPrecomputed();
        expect(mDbFrontend.loadCaches(null)).andReturn(list1);
        expect(mDbFrontend.loadCaches("CacheType = 1")).andReturn(list1);
        expect(mCacheFilter1.getSqlWhereClause()).andReturn(null);
        expect(mCacheFilter1.getSqlWhereClause()).andReturn("CacheType = 1");

        PowerMock.replayAll();
        
        mCachesProviderDb.getCaches();
        mCachesProviderDb.resetChanged();
        mCachesProviderDb.setFilter(mCacheFilter2);
        mCachesProviderDb.getCaches();

        PowerMock.verifyAll();        
    }
}
