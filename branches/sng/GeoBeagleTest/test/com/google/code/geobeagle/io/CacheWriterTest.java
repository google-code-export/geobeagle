
package com.google.code.geobeagle.io;

import static org.easymock.EasyMock.aryEq;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.notNull;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import com.google.code.geobeagle.io.di.DatabaseDI.SQLiteWrapper;

import junit.framework.TestCase;

public class CacheWriterTest extends TestCase {

    public void testCacheWriter() {
        SQLiteWrapper sqlite = createMock(SQLiteWrapper.class);

        sqlite.execSQL(eq(Database.SQL_REPLACE_CACHE), (Object[])notNull());

        replay(sqlite);
        CacheWriter cacheWriter = new CacheWriter(sqlite);
        cacheWriter.insertAndUpdateCache("gc123", "a cache", 122, 37, "source");
        verify(sqlite);
    }

    public void testCacheWriterClear() {
        SQLiteWrapper sqlite = createMock(SQLiteWrapper.class);
        Object params[] = new Object[] {
            "the source"
        };
        sqlite.execSQL(eq(Database.SQL_CLEAR_CACHES), (Object[])aryEq(params));

        replay(sqlite);
        CacheWriter cacheWriter = new CacheWriter(sqlite);
        cacheWriter.clearCaches("the source");
        verify(sqlite);
    }

    public void testCacheWriterDelete() {
        SQLiteWrapper sqlite = createMock(SQLiteWrapper.class);
        Object params[] = new Object[] {
            "GC123"
        };
        sqlite.execSQL(eq(Database.SQL_DELETE_CACHE), (Object[])aryEq(params));

        replay(sqlite);
        CacheWriter cacheWriter = new CacheWriter(sqlite);
        cacheWriter.deleteCache("GC123");
        verify(sqlite);
    }

}
