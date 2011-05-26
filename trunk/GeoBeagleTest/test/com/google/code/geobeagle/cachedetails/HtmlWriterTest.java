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

package com.google.code.geobeagle.cachedetails;

import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class HtmlWriterTest {
    private StringWriterWrapper writer;

    @Before
    public void setUp() {
        writer = createMock(StringWriterWrapper.class);
    }

    @Test
    public void testClose() throws IOException {
        writer.close();

        replay(writer);
        HtmlWriter htmlWriter = new HtmlWriter(writer);
        htmlWriter.close();
        verify(writer);
    }

    @Test
    public void testOpen() throws IOException {
        writer.open();

        replay(writer);
        HtmlWriter htmlWriter = new HtmlWriter(writer);
        htmlWriter.open();
        verify(writer);
    }

    @Test
    public void testWrite() throws IOException {
        writer.write("some text<br/>\n");

        replay(writer);
        HtmlWriter htmlWriter = new HtmlWriter(writer);
        htmlWriter.writeln("some text");
        verify(writer);
    }

    @Test
    public void testWriteFooter() throws IOException {
        writer.write("  </body>\n");
        writer.write("</html>\n");

        replay(writer);
        HtmlWriter htmlWriter = new HtmlWriter(writer);
        htmlWriter.writeFooter();
        verify(writer);
    }

    @Test
    public void testWriteHeader() throws IOException {
        writer.write(HtmlWriter.HEADER);

        replay(writer);
        HtmlWriter htmlWriter = new HtmlWriter(writer);
        htmlWriter.writeHeader();
        verify(writer);
    }

    @Test
    public void testWriteSeparator() throws IOException {
        writer.write("<hr/>\n");

        replay(writer);
        HtmlWriter htmlWriter = new HtmlWriter(writer);
        htmlWriter.writeSeparator();
        verify(writer);
    }

}
