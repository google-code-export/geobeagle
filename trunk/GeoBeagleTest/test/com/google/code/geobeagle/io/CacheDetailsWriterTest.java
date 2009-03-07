
package com.google.code.geobeagle.io;

import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import java.io.IOException;

import junit.framework.TestCase;

public class CacheDetailsWriterTest extends TestCase {
    public void testWriteEndTag() throws IOException {
        HtmlWriter htmlWriter = createMock(HtmlWriter.class);

        htmlWriter.writeFooter();
        htmlWriter.close();

        replay(htmlWriter);
        new CacheDetailsWriter(htmlWriter).writeEndTag();
        verify(htmlWriter);
    }

    public void testWriteLine() throws IOException {
        HtmlWriter htmlWriter = createMock(HtmlWriter.class);
        htmlWriter.write("some text");

        replay(htmlWriter);
        new CacheDetailsWriter(htmlWriter).writeLine("some text");
        verify(htmlWriter);
    }

    public void testWriteLogDate() throws IOException {
        HtmlWriter htmlWriter = createMock(HtmlWriter.class);
        htmlWriter.writeSeparator();
        htmlWriter.write("04/30/1963");

        replay(htmlWriter);
        new CacheDetailsWriter(htmlWriter).writeLogDate("04/30/1963");
        verify(htmlWriter);
    }

    public void testWriteWptName() throws IOException {
        HtmlWriter htmlWriter = createMock(HtmlWriter.class);
        htmlWriter.writeHeader();
        htmlWriter.write("GC1234");
        htmlWriter.write("37.0, 122.0");

        replay(htmlWriter);
        new CacheDetailsWriter(htmlWriter).writeWptName("GC1234", 37, 122);
        verify(htmlWriter);
    }

}
