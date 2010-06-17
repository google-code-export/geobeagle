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
import static org.junit.Assert.assertEquals;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import com.google.code.geobeagle.activity.cachelist.GeoBeagleTest;
import com.google.code.geobeagle.cachedetails.FilePathStrategy;
import com.google.code.geobeagle.cachedetails.Writer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;

@RunWith(PowerMockRunner.class)
public class XmlWriterTest extends GeoBeagleTest {
    private XmlWriter xmlWriter;
    private TagWriter tagWriter;
    private StringWriter stringWriter;
    private XmlPullParserWrapper xmlPullParser;

    static class StringWriter implements Writer {
        private java.io.StringWriter stringWriter;
        private boolean isOpen;

        StringWriter() {
            stringWriter = new java.io.StringWriter();
            isOpen = false;
        }

        @Override
        public void close() throws IOException {
            stringWriter.write("\nEOF\n");
            isOpen = false;
        }

        @Override
        public void open(String path) throws IOException {
            stringWriter.write("FILE: " + path + "\n");
            isOpen = true;
        }

        @Override
        public void write(String str) throws IOException {
            stringWriter.write(str);
        }

        @Override
        public String toString() {
            return stringWriter.toString();
        }

        @Override
        public boolean isOpen() {
            return isOpen;
        }
    }

    @Before
    public void setUp() {
        stringWriter = new StringWriter();
        tagWriter = new TagWriter(stringWriter);
        xmlWriter = new XmlWriter(new FilePathStrategy("/sdcard/"), tagWriter);
        xmlPullParser = createMock(XmlPullParserWrapper.class);
    }

    @Test
    public void testSimpleTag() throws IOException {
        expect(xmlPullParser.getAttributeCount()).andStubReturn(0);

        replayAll();
        XmlWriter xmlWriter = new XmlWriter(new FilePathStrategy("/sdcard/"), tagWriter);
        xmlWriter.open("filename.txt");
        xmlWriter.startTag("wpt", "/gpx/wpt", xmlPullParser);
        xmlWriter.startTag("name", "/gpx/wpt/name", xmlPullParser);
        xmlWriter.text("/gpx/wpt/name", "GC123", xmlPullParser);
        xmlWriter.endTag("name", "/gpx/wpt/name");
        // System.out.println(stringWriter.toString());
        assertEquals("FILE: /sdcard/filename.txt/6/GC123.gpx\n"
                + "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
                + "<gpx>\n <wpt>\n  <name>GC123</name>", stringWriter.toString());
        verifyAll();
    }

    @Test
    public void testTagStack() throws IOException {
        expect(xmlPullParser.getAttributeCount()).andReturn(0).anyTimes();

        replayAll();
        XmlWriter xmlWriter = new XmlWriter(new FilePathStrategy("/sdcard/"), tagWriter);
        xmlWriter.open("filename.txt");

        xmlWriter.startTag("wpt", "/gpx/wpt", xmlPullParser);
        xmlWriter.startTag("time", "/gpx/wpt/time", xmlPullParser);
        xmlWriter.text("/gpx/wpt/time", "3oclock", xmlPullParser);
        xmlWriter.endTag("time", "/gpx/wpt/time");
        xmlWriter.startTag("name", "/gpx/wpt/name", xmlPullParser);
        xmlWriter.text("/gpx/wpt/name", "GC123", xmlPullParser);
        xmlWriter.endTag("name", "/gpx/wpt/name");
        xmlWriter.endTag("wpt", "/gpx/wpt");
        xmlWriter.endTag("gpx", "/gpx");

        // System.out.println(stringWriter.toString());
        assertEquals("FILE: /sdcard/filename.txt/6/GC123.gpx\n"
                + "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<gpx>\n"
                + " <wpt>\n  <name>GC123</name></wpt></gpx>\nEOF\n", stringWriter.toString());
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

        replayAll();
        XmlWriter xmlWriter = new XmlWriter(new FilePathStrategy("/sdcard/"), tagWriter);
        xmlWriter.open("filename.txt");
        xmlWriter.startTag("wpt", "/gpx/wpt", xmlPullParser);
        xmlWriter.startTag("name", "/gpx/wpt/name", xmlPullParser);
        xmlWriter.text("/gpx/wpt/name", "GC123", xmlPullParser);
        xmlWriter.endTag("name", "/gpx/wpt/name");
//        System.out.println(stringWriter.toString());
        assertEquals("FILE: /sdcard/filename.txt/6/GC123.gpx\n"
                + "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
                + "<gpx>\n <wpt lon='123' lat='456'>\n  <name>GC123</name>", stringWriter
                .toString());
        verifyAll();
    }

    @Test
    public void testEscaping() throws IOException {
        expect(xmlPullParser.getAttributeCount()).andReturn(0).anyTimes();

        replayAll();
        xmlWriter.open("filename.txt");
        xmlWriter.startTag("wpt", "/gpx/wpt", xmlPullParser);
        xmlWriter.startTag("name", "/gpx/wpt/name", xmlPullParser);
        xmlWriter.text("/gpx/wpt/name", "GC123", xmlPullParser);
        xmlWriter.endTag("name", "/gpx/wpt/name");
        xmlWriter.startTag("desc", "/gpx/wpt/desc", xmlPullParser);
        xmlWriter.text("/gpx/wpt/desc", "<>&", xmlPullParser);

        // System.out.println(stringWriter.toString());
        assertEquals("FILE: /sdcard/filename.txt/6/GC123.gpx\n"
                + "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
                + "<gpx>\n <wpt>\n  <name>GC123</name>\n" + "  <desc>&lt;&gt;&amp;", stringWriter
                .toString());
        verifyAll();
    }

    @Test
    public void testClose() throws IOException {
        expect(xmlPullParser.getAttributeCount()).andReturn(0).anyTimes();

        replayAll();
        xmlWriter.open("filename.txt");
        xmlWriter.startTag("wpt", "/gpx/wpt", xmlPullParser);
        xmlWriter.startTag("name", "/gpx/wpt/name", xmlPullParser);
        xmlWriter.text("/gpx/wpt/name", "GC123", xmlPullParser);
        xmlWriter.endTag("name", "/gpx/wpt/name");

        xmlWriter.startTag("wpt", "/gpx/wpt", xmlPullParser);
        xmlWriter.endTag("wpt", "/gpx/wpt");

        // System.out.println(stringWriter.toString());
        assertEquals("FILE: /sdcard/filename.txt/6/GC123.gpx\n"
                + "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
                + "<gpx>\n <wpt>\n  <name>GC123</name></wpt></gpx>\nEOF\n", stringWriter.toString());
        verifyAll();
    }

}
