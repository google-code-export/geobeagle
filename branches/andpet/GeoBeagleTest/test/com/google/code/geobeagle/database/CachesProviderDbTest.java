
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

import java.util.HashSet;
import java.util.Set;

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
        Set<Integer> requiredTags = new HashSet<Integer>();
        requiredTags.add(1);
        Set<Integer> forbiddenTags = new HashSet<Integer>();
        forbiddenTags.add(2);
        Set<Integer> updatedRequiredTags= new HashSet<Integer>();
        updatedRequiredTags.add(22);
        Set<Integer> updatedForbiddenTags = new HashSet<Integer>();
        updatedForbiddenTags.add(0);

        expect(mCacheFilter1.getSqlWhereClause()).andReturn("1");
        expect(mCacheFilter1.getRequiredTags()).andReturn(requiredTags);
        expect(mCacheFilter1.getForbiddenTags()).andReturn(forbiddenTags);
        expect(mCacheFilter2.getSqlWhereClause()).andReturn("2");
        expect(mCacheFilter2.getRequiredTags()).andReturn(updatedRequiredTags);
        expect(mCacheFilter2.getForbiddenTags()).andReturn(updatedForbiddenTags);

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
        Set<Integer> tags = new HashSet<Integer>();
        tags.add(0);
        
        expect(mCacheFilter1.getSqlWhereClause()).andReturn("CacheType = 1").anyTimes();
        expect(mCacheFilter1.getRequiredTags()).andReturn(tags).anyTimes();
        expect(mCacheFilter1.getForbiddenTags()).andReturn(tags);
        expect(mCacheFilter2.getSqlWhereClause()).andReturn("CacheType = 1").anyTimes();
        expect(mCacheFilter2.getRequiredTags()).andReturn(tags).anyTimes();
        expect(mCacheFilter2.getForbiddenTags()).andReturn(tags);
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

    // Broken test.
    public void testExtraConditionGetNewCaches() {
        Set<Integer> tags = new HashSet<Integer>();
        tags.add(0);
        
        GeocacheList list1 = new GeocacheListPrecomputed();
        expect(mDbFrontend.loadCachesRaw("SELECT Id FROM CACHES")).andReturn(list1);
        expect(mDbFrontend.loadCachesRaw("SELECT Id FROM CACHES WHERE CacheType = 1")).andReturn(list1);
        expect(mDbFrontend.countRaw((String)EasyMock.anyObject())).andReturn(0).anyTimes();
        expect(mCacheFilter1.getRequiredTags()).andReturn(tags).anyTimes();
        expect(mCacheFilter1.getForbiddenTags()).andReturn(tags);
        expect(mCacheFilter1.getSqlWhereClause()).andReturn("");
        expect(mCacheFilter2.getRequiredTags()).andReturn(tags).anyTimes();
        expect(mCacheFilter2.getForbiddenTags()).andReturn(tags);
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
