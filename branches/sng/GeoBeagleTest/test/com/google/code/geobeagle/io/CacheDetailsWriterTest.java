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

import java.io.IOException;
import java.io.Writer;

import junit.framework.TestCase;

public class CacheDetailsWriterTest extends TestCase {
    public void testClose() throws IOException {
        final Writer writer = createMock(Writer.class);

        writer.close();

        replay(writer);
        new CacheDetailsWriter(writer).close();
        verify(writer);
    }

    public void testWrite() throws IOException {
        final Writer writer = createMock(Writer.class);
        writer.write("some text<br/>\n");

        replay(writer);
        new CacheDetailsWriter(writer).write("some text");
        verify(writer);
    }

    public void testWriteFooter() throws IOException {
        final Writer writer = createMock(Writer.class);
        writer.write("  </body>\n");
        writer.write("</html>\n");

        replay(writer);
        new CacheDetailsWriter(writer).writeFooter();
        verify(writer);
    }

    public void testWriteHeader() throws IOException {
        final Writer writer = createMock(Writer.class);
        writer.write("<html>\n");
        writer.write("  <body>\n");

        replay(writer);
        new CacheDetailsWriter(writer).writeHeader();
        verify(writer);
    }
}
