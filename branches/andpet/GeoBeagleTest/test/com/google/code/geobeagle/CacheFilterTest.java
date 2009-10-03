package com.google.code.geobeagle;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.view.View;

@RunWith(PowerMockRunner.class)
@PrepareForTest(View.class)
public class CacheFilterTest {

    @Test
    public void testFindAllByDefault() {
        CacheFilter cacheFilter = new CacheFilter();
        assertEquals(null, cacheFilter.getSqlWhereClause());
    }

    @Test
    public void testAllSelected() {
        CacheFilter.SettingsProvider provider = new CacheFilter.SettingsProvider() {
            @Override
            public boolean getBoolean(int id) { return true; }
            @Override
            public String getString(int id) { return ""; }
            @Override
            public void setBoolean(int id, boolean value) { }
            @Override
            public void setString(int id, String value) { }
        };

        CacheFilter cacheFilter = new CacheFilter();
        cacheFilter.setFromProvider(provider);
        assertEquals(null, cacheFilter.getSqlWhereClause());
    }

    @Test
    public void testNoneSelected() {
        CacheFilter.SettingsProvider provider = new CacheFilter.SettingsProvider() {
            @Override
            public boolean getBoolean(int id) { return false; }
            @Override
            public String getString(int id) { return ""; }
            @Override
            public void setBoolean(int id, boolean value) { }
            @Override
            public void setString(int id, String value) { }
        };

        CacheFilter cacheFilter = new CacheFilter();
        cacheFilter.setFromProvider(provider);
        assertEquals(null, cacheFilter.getSqlWhereClause());
    }
    
    @Test
    public void testCaseInsensitive() {
        CacheFilter.SettingsProvider provider = new CacheFilter.SettingsProvider() {
            @Override
            public boolean getBoolean(int id) { return true; }
            @Override
            public String getString(int id) { return "text"; }
            @Override
            public void setBoolean(int id, boolean value) { }
            @Override
            public void setString(int id, String value) { }
        };

        CacheFilter cacheFilter = new CacheFilter();
        cacheFilter.setFromProvider(provider);
        assertTrue(cacheFilter.getSqlWhereClause().contains("lower"));
    }

    @Test
    public void testCaseSensitive() {
        CacheFilter.SettingsProvider provider = new CacheFilter.SettingsProvider() {
            @Override
            public boolean getBoolean(int id) { return true; }
            @Override
            public String getString(int id) { return "Text"; }
            @Override
            public void setBoolean(int id, boolean value) { }
            @Override
            public void setString(int id, String value) { }
        };

        CacheFilter cacheFilter = new CacheFilter();
        cacheFilter.setFromProvider(provider);
        assertFalse(cacheFilter.getSqlWhereClause().contains("lower"));
    }
}
