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

package com.google.code.geobeagle.cacheloader;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import com.google.code.geobeagle.xmlimport.EventDispatcher;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.xmlpull.v1.XmlPullParser;

import java.io.Reader;

@PrepareForTest({})
@RunWith(PowerMockRunner.class)
public class DetailsXmlToStringTest {

    private EventDispatcher eventDispatcher;
    private Reader reader;

    @Before
    public void setUp() {
        eventDispatcher = createMock(EventDispatcher.class);
        reader = createMock(Reader.class);
    }

    @Test
    public void testDetailsReader() throws Exception {
        eventDispatcher.setInput(reader);
        eventDispatcher.open();
        expect(eventDispatcher.getEventType()).andReturn(XmlPullParser.END_DOCUMENT);
        expect(eventDispatcher.handleEvent(XmlPullParser.END_DOCUMENT)).andReturn(true);
        expect(eventDispatcher.getString()).andReturn("DETAILS");

        replayAll();
        assertEquals("DETAILS", new DetailsXmlToString(eventDispatcher).read(reader));
        verifyAll();
    }

    @Test
    public void testDetailsReaderTwo() throws Exception {
        eventDispatcher.setInput(reader);
        eventDispatcher.open();
        expect(eventDispatcher.getEventType()).andReturn(XmlPullParser.START_DOCUMENT);
        expect(eventDispatcher.handleEvent(XmlPullParser.START_DOCUMENT)).andReturn(true);

        expect(eventDispatcher.next()).andReturn(XmlPullParser.END_DOCUMENT);
        expect(eventDispatcher.handleEvent(XmlPullParser.END_DOCUMENT)).andReturn(true);

        expect(eventDispatcher.getString()).andReturn("DETAILS");

        replayAll();
        assertEquals("DETAILS", new DetailsXmlToString(eventDispatcher).read(reader));
        verifyAll();
    }
}
