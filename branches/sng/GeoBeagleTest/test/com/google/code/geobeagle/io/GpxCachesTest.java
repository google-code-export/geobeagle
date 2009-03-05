
package com.google.code.geobeagle.io;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.io.GpxLoader.Cache;
import com.google.code.geobeagle.ui.ErrorDisplayer;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import junit.framework.TestCase;

public class GpxCachesTest extends TestCase {

    public void testCacheIteratorHasNextNoCache() throws XmlPullParserException, IOException {
        GpxToCache gpxToCache = createMock(GpxToCache.class);
        GpxCaches gpxCaches = new GpxCaches(gpxToCache, null);
        GpxCaches.CacheIterator cacheIterator = gpxCaches.new CacheIterator();

        expect(gpxToCache.load()).andReturn(null);

        replay(gpxToCache);
        assertFalse(cacheIterator.hasNext());
        verify(gpxToCache);
    }

    public void testCacheIteratorHasNextHasCache() throws XmlPullParserException, IOException {
        GpxToCache gpxToCache = createMock(GpxToCache.class);
        Cache cache = createMock(Cache.class);

        GpxCaches gpxCaches = new GpxCaches(gpxToCache, null);
        GpxCaches.CacheIterator cacheIterator = gpxCaches.new CacheIterator();

        expect(gpxToCache.load()).andReturn(cache);

        replay(gpxToCache);
        assertTrue(cacheIterator.hasNext());
        verify(gpxToCache);
    }

    public void testCacheIteratorHasNextPullParserException() throws XmlPullParserException,
            IOException {
        hasNextException(XmlPullParserException.class, R.string.error_parsing_file);
    }

    public void testCacheIteratorHasNextIOException() throws XmlPullParserException, IOException {
        hasNextException(IOException.class, R.string.error_reading_file);
    }

    private <T> void hasNextException(Class<T> exceptionClass, int errorMsg)
            throws XmlPullParserException, IOException {
        GpxToCache gpxToCache = createMock(GpxToCache.class);
        ErrorDisplayer errorDisplayer = createMock(ErrorDisplayer.class);
        Throwable t = (Throwable)createMock(exceptionClass);

        GpxCaches gpxCaches = new GpxCaches(gpxToCache, errorDisplayer);
        GpxCaches.CacheIterator cacheIterator = gpxCaches.new CacheIterator();

        expect(gpxToCache.load()).andThrow(t);
        expect(gpxToCache.getSource()).andReturn("foo.gpx");
        errorDisplayer.displayError(errorMsg, "foo.gpx");
        expect(t.fillInStackTrace()).andReturn(t);

        replay(gpxToCache);
        replay(errorDisplayer);
        replay(t);
        assertFalse(cacheIterator.hasNext());
        verify(gpxToCache);
        verify(errorDisplayer);
        verify(t);
    }
}
