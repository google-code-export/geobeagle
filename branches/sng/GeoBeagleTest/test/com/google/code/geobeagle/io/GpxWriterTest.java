
package com.google.code.geobeagle.io;

import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import java.io.IOException;

import junit.framework.TestCase;

public class GpxWriterTest extends TestCase {
    public void testWriteEndTag() throws IOException {
        CacheDetailsWriter cacheDetailsWriter = createMock(CacheDetailsWriter.class);

        cacheDetailsWriter.writeFooter();
        cacheDetailsWriter.close();

        replay(cacheDetailsWriter);
        new GpxWriter(cacheDetailsWriter).writeEndTag();
        verify(cacheDetailsWriter);
    }

    public void testWriteLine() throws IOException {
        CacheDetailsWriter cacheDetailsWriter = createMock(CacheDetailsWriter.class);
        cacheDetailsWriter.write("some text");

        replay(cacheDetailsWriter);
        new GpxWriter(cacheDetailsWriter).writeLine("some text");
        verify(cacheDetailsWriter);
    }

    public void testWriteLogDate() throws IOException {
        CacheDetailsWriter cacheDetailsWriter = createMock(CacheDetailsWriter.class);
        cacheDetailsWriter.writeSeparator();
        cacheDetailsWriter.write("04/30/1963");

        replay(cacheDetailsWriter);
        new GpxWriter(cacheDetailsWriter).writeLogDate("04/30/1963");
        verify(cacheDetailsWriter);
    }

    public void testWriteWptName() throws IOException {
        CacheDetailsWriter cacheDetailsWriter = createMock(CacheDetailsWriter.class);
        cacheDetailsWriter.writeHeader();
        cacheDetailsWriter.write("GC1234");
        cacheDetailsWriter.write("37.0, 122.0");

        replay(cacheDetailsWriter);
        new GpxWriter(cacheDetailsWriter).writeWptName("GC1234", 37, 122);
        verify(cacheDetailsWriter);
    }

}
