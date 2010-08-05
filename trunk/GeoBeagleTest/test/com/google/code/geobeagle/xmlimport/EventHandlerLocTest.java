
package com.google.code.geobeagle.xmlimport;

import static org.junit.Assert.assertTrue;

import com.google.code.geobeagle.GeocacheFactory.Source;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;

@RunWith(PowerMockRunner.class)
public class EventHandlerLocTest {
    @Test
    public void endTagTest() throws IOException {
        ImportCacheActions importCacheActions = PowerMock
                .createMock(ImportCacheActions.class);

        PowerMock.replayAll();
        new EventHandlerLoc(importCacheActions).endTag(null, "/random");
        PowerMock.verifyAll();
    }

    @Test
    public void endTagTestWaypoint() throws IOException {
        ImportCacheActions importCacheActions = PowerMock
                .createMock(ImportCacheActions.class);

        importCacheActions.endCache(Source.LOC);

        PowerMock.replayAll();
        new EventHandlerLoc(importCacheActions).endTag(null, EventHandlerLoc.XPATH_WPT);
        PowerMock.verifyAll();
    }

    @Test
    public void startTagCoordTest() throws IOException {
        ImportCacheActions importCacheActions = PowerMock
                .createMock(ImportCacheActions.class);
        XmlPullParserWrapper xmlPullParser = PowerMock.createMock(XmlPullParserWrapper.class);

        EasyMock.expect(xmlPullParser.getAttributeValue(null, "lat")).andReturn("37.123");
        EasyMock.expect(xmlPullParser.getAttributeValue(null, "lon")).andReturn("-122.45");
        importCacheActions.wpt("37.123", "-122.45");

        PowerMock.replayAll();
        new EventHandlerLoc(importCacheActions).startTag(null, EventHandlerLoc.XPATH_COORD,
                xmlPullParser);
        PowerMock.verifyAll();
    }

    @Test
    public void startTagWptNameTest() throws IOException {
        ImportCacheActions importCacheActions = PowerMock
                .createMock(ImportCacheActions.class);
        XmlPullParserWrapper xmlPullParser = PowerMock.createMock(XmlPullParserWrapper.class);

        importCacheActions.startCache();
        EasyMock.expect(xmlPullParser.getAttributeValue(null, "id")).andReturn("GCABC");
        importCacheActions.wptName("GCABC");

        PowerMock.replayAll();
        new EventHandlerLoc(importCacheActions).startTag(null, EventHandlerLoc.XPATH_WPTNAME,
                xmlPullParser);
        PowerMock.verifyAll();
    }

    @Test
    public void textWptNameTest() throws IOException {
        ImportCacheActions importCacheActions = PowerMock
                .createMock(ImportCacheActions.class);

        importCacheActions.groundspeakName("a nice little cache");

        PowerMock.replayAll();
        assertTrue(new EventHandlerLoc(importCacheActions).text(EventHandlerLoc.XPATH_WPTNAME,
                "  a nice little cache   ", null));
        PowerMock.verifyAll();
    }
}
