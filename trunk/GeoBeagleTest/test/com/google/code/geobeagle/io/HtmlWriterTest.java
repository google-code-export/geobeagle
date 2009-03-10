/*
 ** Licensed under the Apache License, Version 2.0 (the "License");
 ** you may not use this file except in compliance with the License.
 ** You may obtain a copy of the License at
 **
 **     http://www.apache.org/licenses/LICENSE-2.0
 **
 ** Unless required by applicable law or agreed to in writing, software
 ** distributed under the License is distributed on an "AS IS" BASIS,
 ** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ** See the License for the specific language governing permissions and
 ** limitations under the License.
 */

package com.google.code.geobeagle.io;

import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import com.google.code.geobeagle.io.di.CachePersisterFacadeDI.WriterWrapper;

import java.io.IOException;

import junit.framework.TestCase;

public class HtmlWriterTest extends TestCase {
    public void testClose() throws IOException {
        final WriterWrapper writer = createMock(WriterWrapper.class);

        writer.close();

        replay(writer);
        HtmlWriter htmlWriter = new HtmlWriter(writer);
        htmlWriter.close();
        verify(writer);
    }

    public void testOpen() throws IOException {
        final WriterWrapper writer = createMock(WriterWrapper.class);

        writer.open("/path/to/file");

        replay(writer);
        HtmlWriter htmlWriter = new HtmlWriter(writer);
        htmlWriter.open("/path/to/file");
        verify(writer);
    }

    public void testWrite() throws IOException {
        final WriterWrapper writer = createMock(WriterWrapper.class);

        writer.write("some text<br/>\n");

        replay(writer);
        HtmlWriter htmlWriter = new HtmlWriter(writer);
        htmlWriter.write("some text");
        verify(writer);
    }

    public void testWriteFooter() throws IOException {
        final WriterWrapper writer = createMock(WriterWrapper.class);
        writer.write("  </body>\n");
        writer.write("</html>\n");

        replay(writer);
        HtmlWriter htmlWriter = new HtmlWriter(writer);
        htmlWriter.writeFooter();
        verify(writer);
    }

    public void testWriteHeader() throws IOException {
        final WriterWrapper writer = createMock(WriterWrapper.class);
        writer.write("<html>\n");
        writer.write("  <body>\n");

        replay(writer);
        HtmlWriter htmlWriter = new HtmlWriter(writer);
        htmlWriter.writeHeader();
        verify(writer);
    }

    public void testWriteSeparator() throws IOException {
        final WriterWrapper writer = createMock(WriterWrapper.class);
        writer.write("<hr/>\n");

        replay(writer);
        HtmlWriter htmlWriter = new HtmlWriter(writer);
        htmlWriter.writeSeparator();
        verify(writer);
    }

}
