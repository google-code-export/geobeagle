package com.google.code.geobeagle.database;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.junit.Assert.*;

import com.google.code.geobeagle.CacheFilter;
import com.google.code.geobeagle.Geocache;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.util.Log;

import java.util.ArrayList;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Log.class)
public class CachesProviderAreaTest {
    private DbFrontend mDbFrontend;
    private CacheFilter mCacheFilter;
    CachesProviderArea mCachesProviderArea;

    @Before
    public void setUp() {
        PowerMock.mockStatic(Log.class);
        expect(Log.d(isA(String.class), isA(String.class))).andReturn(0).anyTimes();
        mDbFrontend = PowerMock.createMock(DbFrontend.class);
        mCacheFilter = PowerMock.createMock(CacheFilter.class);
        mCachesProviderArea = new CachesProviderArea(mDbFrontend, mCacheFilter);
    }
    
    @Test
    public void testFilterUpdateSetsChanged() {
        expect(mCacheFilter.getSqlWhereClause()).andReturn("1");
        mCacheFilter.reload();
        expect(mCacheFilter.getSqlWhereClause()).andReturn("2");
        PowerMock.replayAll();
        
        mCachesProviderArea.resetChanged();
        mCachesProviderArea.reloadFilter();
        assertTrue(mCachesProviderArea.hasChanged());
    }

    @Test
    public void testUnchangedFilter() {
        expect(mCacheFilter.getSqlWhereClause()).andReturn("CacheType = 1").anyTimes();
        mCacheFilter.reload();
        expect(mDbFrontend.count("CacheType = 1")).andReturn(5);
        PowerMock.replayAll();
        
        mCachesProviderArea.getCount();  //will load the filter
        mCachesProviderArea.resetChanged();
        mCachesProviderArea.reloadFilter();
        assertFalse(mCachesProviderArea.hasChanged());
    }
    
    @Test
    public void testExtraConditionGetNewCaches() {
        ArrayList<Geocache> list1 = new ArrayList<Geocache>();
        expect(mDbFrontend.loadCaches(null)).andReturn(list1);
        expect(mDbFrontend.loadCaches("CacheType = 1")).andReturn(list1);
        expect(mCacheFilter.getSqlWhereClause()).andReturn(null);
        mCacheFilter.reload();
        expect(mCacheFilter.getSqlWhereClause()).andReturn("CacheType = 1");

        PowerMock.replayAll();
        
        mCachesProviderArea.getCaches();
        mCachesProviderArea.resetChanged();
        mCachesProviderArea.reloadFilter();
        mCachesProviderArea.getCaches();

        PowerMock.verifyAll();        
    }
}
