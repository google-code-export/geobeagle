
package com.google.code.geobeagle.database;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.code.geobeagle.CacheFilter;
import com.google.code.geobeagle.GeocacheList;
import com.google.code.geobeagle.GeocacheListPrecomputed;

import org.easymock.EasyMock;
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
    }

    @Test
    public void testFilterUpdateSetsChanged() {
        expect(mCacheFilter1.getSqlWhereClause()).andReturn("1");
        expect(mCacheFilter1.getRequiredTag()).andReturn(11);
        expect(mCacheFilter1.getForbiddenTag()).andReturn(0);
        expect(mCacheFilter2.getSqlWhereClause()).andReturn("2");
        expect(mCacheFilter2.getRequiredTag()).andReturn(22);
        expect(mCacheFilter2.getForbiddenTag()).andReturn(0);

        PowerMock.replayAll();
        mCachesProviderDb = new CachesProviderDb(mDbFrontend);
        mCachesProviderDb.setFilter(mCacheFilter1);
        mCachesProviderDb.resetChanged();
        mCachesProviderDb.setFilter(mCacheFilter2);
        assertTrue(mCachesProviderDb.hasChanged());
        PowerMock.verifyAll();
    }

    @Test
    public void testUnchangedFilter() {
        expect(mCacheFilter1.getSqlWhereClause()).andReturn("CacheType = 1").anyTimes();
        expect(mCacheFilter1.getRequiredTag()).andReturn(0).anyTimes();
        expect(mCacheFilter1.getForbiddenTag()).andReturn(0);
        expect(mCacheFilter2.getSqlWhereClause()).andReturn("CacheType = 1").anyTimes();
        expect(mCacheFilter2.getRequiredTag()).andReturn(0).anyTimes();
        expect(mCacheFilter2.getForbiddenTag()).andReturn(0);
        expect(mDbFrontend.countRaw((String)EasyMock.anyObject())).andReturn(5).anyTimes();

        PowerMock.replayAll();
        mCachesProviderDb = new CachesProviderDb(mDbFrontend);
        mCachesProviderDb.setFilter(mCacheFilter1);
        mCachesProviderDb.getCount(); // will load the filter
        mCachesProviderDb.resetChanged();
        mCachesProviderDb.setFilter(mCacheFilter2);
        assertFalse(mCachesProviderDb.hasChanged());
        PowerMock.verifyAll();
    }

    @Test
    public void testExtraConditionGetNewCaches() {
        GeocacheList list1 = new GeocacheListPrecomputed();
        expect(mDbFrontend.loadCachesRaw("SELECT Id FROM CACHES")).andReturn(list1);
        expect(mDbFrontend.loadCachesRaw("SELECT Id FROM CACHES WHERE CacheType = 1")).andReturn(list1);
        expect(mDbFrontend.countRaw((String)EasyMock.anyObject())).andReturn(0).anyTimes();
        expect(mCacheFilter1.getRequiredTag()).andReturn(0).anyTimes();
        expect(mCacheFilter1.getForbiddenTag()).andReturn(0);
        expect(mCacheFilter1.getSqlWhereClause()).andReturn("");
        expect(mCacheFilter2.getRequiredTag()).andReturn(0).anyTimes();
        expect(mCacheFilter2.getForbiddenTag()).andReturn(0);
        expect(mCacheFilter2.getSqlWhereClause()).andReturn("CacheType = 1");

        PowerMock.replayAll();
        mCachesProviderDb = new CachesProviderDb(mDbFrontend);
        mCachesProviderDb.setFilter(mCacheFilter1);
        mCachesProviderDb.getCaches();
        mCachesProviderDb.resetChanged();
        mCachesProviderDb.setFilter(mCacheFilter2);
        mCachesProviderDb.getCaches();
        PowerMock.verifyAll();
    }
}
