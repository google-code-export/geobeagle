package com.google.code.geobeagle;

import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import static org.junit.Assert.*;

import org.junit.Test;

import android.view.View;

@RunWith(PowerMockRunner.class)
@PrepareForTest(View.class)
public class CacheFilterTest {

    @Test
    public void dummyTest() {
        assertTrue(true);        
    }

    //TODO: Add back tests

//    @Test
//    public void testAllSelected() {
//        CacheFilter.FilterGui provider = new CacheFilter.FilterGui() {
//            @Override
//            public boolean getBoolean(int id) { return true; }
//            @Override
//            public String getString(int id) { return ""; }
//            @Override
//            public void setBoolean(int id, boolean value) { }
//            @Override
//            public void setString(int id, String value) { }
//        };
//
//        CacheFilter cacheFilter = new CacheFilter(null, null);
//        cacheFilter.loadFromGui(provider);
//        assertEquals(null, cacheFilter.getSqlWhereClause());
//    }
//
//    @Test
//    public void testNoneSelected() {
//        CacheFilter.FilterGui provider = new CacheFilter.FilterGui() {
//            @Override
//            public boolean getBoolean(int id) { return false; }
//            @Override
//            public String getString(int id) { return ""; }
//            @Override
//            public void setBoolean(int id, boolean value) { }
//            @Override
//            public void setString(int id, String value) { }
//        };
//
//        CacheFilter cacheFilter = new CacheFilter(null, null);
//        cacheFilter.loadFromGui(provider);
//        assertEquals(null, cacheFilter.getSqlWhereClause());
//    }
//    
//    @Test
//    public void testCaseInsensitive() {
//        CacheFilter.FilterGui provider = new CacheFilter.FilterGui() {
//            @Override
//            public boolean getBoolean(int id) { return true; }
//            @Override
//            public String getString(int id) { return "text"; }
//            @Override
//            public void setBoolean(int id, boolean value) { }
//            @Override
//            public void setString(int id, String value) { }
//        };
//
//        CacheFilter cacheFilter = new CacheFilter(null, null);
//        cacheFilter.loadFromGui(provider);
//        assertTrue(cacheFilter.getSqlWhereClause().contains("lower"));
//    }
//
//    @Test
//    public void testCaseSensitive() {
//        CacheFilter.FilterGui provider = new CacheFilter.FilterGui() {
//            @Override
//            public boolean getBoolean(int id) { return true; }
//            @Override
//            public String getString(int id) { return "Text"; }
//            @Override
//            public void setBoolean(int id, boolean value) { }
//            @Override
//            public void setString(int id, String value) { }
//        };
//
//        CacheFilter cacheFilter = new CacheFilter(null, null);
//        cacheFilter.loadFromGui(provider);
//        assertFalse(cacheFilter.getSqlWhereClause().contains("lower"));
//    }
}
