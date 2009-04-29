
package com.google.code.geobeagle.io;

import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import org.junit.Test;

import java.io.IOException;

public class CacheDetailsWriterTest {
    @Test
    public void testOpen() throws IOException {
        HtmlWriter htmlWriter = createMock(HtmlWriter.class);

        htmlWriter.open(CacheDetailsWriter.GEOBEAGLE_DIR + "/GC123.html");

        replay(htmlWriter);
        new CacheDetailsWriter(htmlWriter).open("GC123");
        verify(htmlWriter);
    }

    @Test
    public void testWriteEndTag() throws IOException {
        HtmlWriter htmlWriter = createMock(HtmlWriter.class);

        htmlWriter.writeFooter();
        htmlWriter.close();

        replay(htmlWriter);
        new CacheDetailsWriter(htmlWriter).close();
        verify(htmlWriter);
    }

    @Test
    public void testWriteHint() throws IOException {
        HtmlWriter htmlWriter = createMock(HtmlWriter.class);
        htmlWriter.write("<br />Hint: <font color=gray>a hint</font>");

        replay(htmlWriter);
        new CacheDetailsWriter(htmlWriter).writeHint("a hint");
        verify(htmlWriter);
    }

    @Test
    public void testWriteLine() throws IOException {
        HtmlWriter htmlWriter = createMock(HtmlWriter.class);
        htmlWriter.write("some text");

        replay(htmlWriter);
        new CacheDetailsWriter(htmlWriter).writeLine("some text");
        verify(htmlWriter);
    }

    @Test
    public void testWriteLogDate() throws IOException {
        HtmlWriter htmlWriter = createMock(HtmlWriter.class);
        htmlWriter.writeSeparator();
        htmlWriter.write("04/30/1963");

        replay(htmlWriter);
        new CacheDetailsWriter(htmlWriter).writeLogDate("04/30/1963");
        verify(htmlWriter);
    }

    @Test
    public void testWriteWptName() throws IOException {
        HtmlWriter htmlWriter = createMock(HtmlWriter.class);
        htmlWriter.writeHeader();
        htmlWriter.write("GC1234");
        htmlWriter.write("37.0, 122.0");

        replay(htmlWriter);
        CacheDetailsWriter cacheDetailsWriter = new CacheDetailsWriter(htmlWriter);
        cacheDetailsWriter.latitudeLongitude("37.0", "122.0");
        cacheDetailsWriter.writeWptName("GC1234");
        verify(htmlWriter);
    }
}
