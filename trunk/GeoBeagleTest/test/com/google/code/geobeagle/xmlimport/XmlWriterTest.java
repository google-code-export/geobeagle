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

package com.google.code.geobeagle.xmlimport;

import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.createStrictMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import com.google.code.geobeagle.activity.cachelist.GeoBeagleTest;
import com.google.code.geobeagle.cachedetails.DetailsDatabaseWriter;
import com.google.code.geobeagle.cachedetails.FilePathStrategy;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;

@RunWith(PowerMockRunner.class)
public class XmlWriterTest extends GeoBeagleTest {
    private TagWriter tagWriter;
    private StringWriter stringWriter;
    private XmlPullParser xmlPullParser;
    private FilePathStrategy filePathStrategy;
    private DetailsDatabaseWriter detailsDatabaseWriter;

    static class StringWriter {
        private final java.io.StringWriter stringWriter;
        private boolean isOpen;

        StringWriter() {
            stringWriter = new java.io.StringWriter();
            isOpen = false;
        }

        public void close() {
            stringWriter.write("\nEOF\n");
            isOpen = false;
        }

        public void write(String str) {
            stringWriter.write(str);
        }

        @Override
        public String toString() {
            return stringWriter.toString();
        }

        public boolean isOpen() {
            return isOpen;
        }

        public void mkdirs(String path) {
        }

        public void open(String path, String wpt) {
            stringWriter.write("FILE: " + path + "\n");
            isOpen = true;
        }
    }

    @Before
    public void setUp() {
        stringWriter = new StringWriter();
        filePathStrategy = createMock(FilePathStrategy.class);
        detailsDatabaseWriter = createStrictMock(DetailsDatabaseWriter.class);
        tagWriter = new TagWriter(detailsDatabaseWriter);
        xmlPullParser = createMock(XmlPullParser.class);
    }

    @Test
    public void testSimpleTag() throws IOException {
        expect(detailsDatabaseWriter.isOpen()).andReturn(false);
        detailsDatabaseWriter.open("GC123");
        expect(xmlPullParser.getAttributeCount()).andStubReturn(0);
        detailsDatabaseWriter.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        detailsDatabaseWriter.write("\n<gpx>");
        detailsDatabaseWriter.write("\n.<wpt>");
        detailsDatabaseWriter.write("\n..<name>");
        expect(detailsDatabaseWriter.isOpen()).andReturn(true);
        detailsDatabaseWriter.write("GC123");
        expect(detailsDatabaseWriter.isOpen()).andReturn(true);
        detailsDatabaseWriter.write("</name>");

        replayAll();
        XmlWriter xmlWriter = new XmlWriter(tagWriter);
        xmlWriter.start(xmlPullParser);
        xmlWriter.startTag("wpt", "/gpx/wpt");
        xmlWriter.startTag("name", "/gpx/wpt/name");
        xmlWriter.text("/gpx/wpt/name", "GC123");
        xmlWriter.endTag("name", "/gpx/wpt/name");
    }

    @Test
    public void testTagStack() throws IOException {
        expect(xmlPullParser.getAttributeCount()).andReturn(0).anyTimes();
        expect(detailsDatabaseWriter.isOpen()).andReturn(false);
        expect(detailsDatabaseWriter.isOpen()).andReturn(false);
        expect(detailsDatabaseWriter.isOpen()).andReturn(false);
        expect(detailsDatabaseWriter.isOpen()).andReturn(false);
        detailsDatabaseWriter.open("GC123");
        detailsDatabaseWriter.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        detailsDatabaseWriter.write("\n<gpx>");
        detailsDatabaseWriter.write("\n.<wpt>");
        detailsDatabaseWriter.write("\n..<time>");
        detailsDatabaseWriter.write("3oclock");
        detailsDatabaseWriter.write("</time>");
        detailsDatabaseWriter.write("\n..<name>");
        expect(detailsDatabaseWriter.isOpen()).andReturn(true);
        detailsDatabaseWriter.write("GC123");
        expect(detailsDatabaseWriter.isOpen()).andReturn(true);
        detailsDatabaseWriter.write("</name>");
        expect(detailsDatabaseWriter.isOpen()).andReturn(true);
        detailsDatabaseWriter.write("</wpt>");
        detailsDatabaseWriter.write("</gpx>");
        detailsDatabaseWriter.close();

        replayAll();
        XmlWriter xmlWriter = new XmlWriter(tagWriter);
        xmlWriter.start(xmlPullParser);
        xmlWriter.startTag("wpt", "/gpx/wpt");
        xmlWriter.startTag("time", "/gpx/wpt/time");
        xmlWriter.text("/gpx/wpt/time", "3oclock");
        xmlWriter.endTag("time", "/gpx/wpt/time");
        xmlWriter.startTag("name", "/gpx/wpt/name");
        xmlWriter.text("/gpx/wpt/name", "GC123");
        xmlWriter.endTag("name", "/gpx/wpt/name");
        xmlWriter.endTag("wpt", "/gpx/wpt");
        xmlWriter.endTag("gpx", "/gpx");
//        assertEquals("FILE: /sdcard/filename.txt/6/GC123.gpx\n"
//                + "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<gpx>\n"
//                + " <wpt>\n  <time>3oclock</time>\n  <name>GC123</name></wpt></gpx>\nEOF\n",
//                stringWriter.toString());
        verifyAll();
    }

    @Test
    public void testAttributes() throws IOException {
        expect(xmlPullParser.getAttributeCount()).andReturn(2);
        expect(xmlPullParser.getAttributeName(0)).andReturn("lat");
        expect(xmlPullParser.getAttributeValue(0)).andReturn("456");
        expect(xmlPullParser.getAttributeName(1)).andReturn("lon");
        expect(xmlPullParser.getAttributeValue(1)).andReturn("123");
        expect(xmlPullParser.getAttributeCount()).andReturn(0);
        expect(detailsDatabaseWriter.isOpen()).andReturn(false);
        detailsDatabaseWriter.open("GC123");
        detailsDatabaseWriter.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        detailsDatabaseWriter.write("\n<gpx>");
        detailsDatabaseWriter.write("\n.<wpt lon='123' lat='456'>");
        detailsDatabaseWriter.write("\n..<name>");
        expect(detailsDatabaseWriter.isOpen()).andReturn(true);
        detailsDatabaseWriter.write("GC123");
        expect(detailsDatabaseWriter.isOpen()).andReturn(true);
        detailsDatabaseWriter.write("</name>");

        replayAll();
        XmlWriter xmlWriter = new XmlWriter(tagWriter);
        xmlWriter.start(xmlPullParser);
        xmlWriter.startTag("wpt", "/gpx/wpt");
        xmlWriter.startTag("name", "/gpx/wpt/name");
        xmlWriter.text("/gpx/wpt/name", "GC123");
        xmlWriter.endTag("name", "/gpx/wpt/name");
        verifyAll();
    }

    @Test
    public void testEscaping() throws IOException {
        expect(detailsDatabaseWriter.isOpen()).andReturn(false);
        detailsDatabaseWriter.open("GC123");
        expect(xmlPullParser.getAttributeCount()).andStubReturn(0);
        detailsDatabaseWriter.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        detailsDatabaseWriter.write("\n<gpx>");
        detailsDatabaseWriter.write("\n.<wpt>");
        detailsDatabaseWriter.write("\n..<name>");
        expect(detailsDatabaseWriter.isOpen()).andReturn(true);
        detailsDatabaseWriter.write("GC123");
        expect(detailsDatabaseWriter.isOpen()).andReturn(true);
        detailsDatabaseWriter.write("</name>");
        expect(detailsDatabaseWriter.isOpen()).andReturn(true);
        detailsDatabaseWriter.write("\n..<desc>");
        expect(detailsDatabaseWriter.isOpen()).andReturn(true);
        detailsDatabaseWriter.write("&lt;&gt;&amp;");

        replayAll();
        XmlWriter xmlWriter = new XmlWriter(tagWriter);
        xmlWriter.start(xmlPullParser);
        xmlWriter.startTag("wpt", "/gpx/wpt");
        xmlWriter.startTag("name", "/gpx/wpt/name");
        xmlWriter.text("/gpx/wpt/name", "GC123");
        xmlWriter.endTag("name", "/gpx/wpt/name");
        xmlWriter.startTag("desc", "/gpx/wpt/desc");
        xmlWriter.text("/gpx/wpt/desc", "<>&");
        verifyAll();
    }

}
