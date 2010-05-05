
package com.google.code.geobeagle.xmlimport;

import static org.junit.Assert.assertTrue;

import com.google.code.geobeagle.GeocacheFactory.Source;
import com.google.code.geobeagle.xmlimport.GpxToCacheDI.XmlPullParserWrapper;

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
        CachePersisterFacade cachePersisterFacade = PowerMock
                .createMock(CachePersisterFacade.class);

        PowerMock.replayAll();
        new EventHandlerLoc(cachePersisterFacade).endTag("/random");
        PowerMock.verifyAll();
    }

    @Test
    public void endTagTestWaypoint() throws IOException {
        CachePersisterFacade cachePersisterFacade = PowerMock
                .createMock(CachePersisterFacade.class);

        cachePersisterFacade.endCache(Source.LOC);

        PowerMock.replayAll();
        new EventHandlerLoc(cachePersisterFacade).endTag(EventHandlerLoc.XPATH_WPT);
        PowerMock.verifyAll();
    }

    @Test
    public void startTagCoordTest() throws IOException {
        CachePersisterFacade cachePersisterFacade = PowerMock
                .createMock(CachePersisterFacade.class);
        XmlPullParserWrapper xmlPullParser = PowerMock.createMock(XmlPullParserWrapper.class);

        EasyMock.expect(xmlPullParser.getAttributeValue(null, "lat")).andReturn("37.123");
        EasyMock.expect(xmlPullParser.getAttributeValue(null, "lon")).andReturn("-122.45");
        cachePersisterFacade.wpt("37.123", "-122.45");

        PowerMock.replayAll();
        new EventHandlerLoc(cachePersisterFacade).startTag(EventHandlerLoc.XPATH_COORD,
                xmlPullParser);
        PowerMock.verifyAll();
    }

    @Test
    public void startTagWptNameTest() throws IOException {
        CachePersisterFacade cachePersisterFacade = PowerMock
                .createMock(CachePersisterFacade.class);
        XmlPullParserWrapper xmlPullParser = PowerMock.createMock(XmlPullParserWrapper.class);

        cachePersisterFacade.startCache();
        EasyMock.expect(xmlPullParser.getAttributeValue(null, "id")).andReturn("GCABC");
        cachePersisterFacade.wptName("GCABC");

        PowerMock.replayAll();
        new EventHandlerLoc(cachePersisterFacade).startTag(EventHandlerLoc.XPATH_WPTNAME,
                xmlPullParser);
        PowerMock.verifyAll();
    }

    @Test
    public void textWptNameTest() throws IOException {
        CachePersisterFacade cachePersisterFacade = PowerMock
                .createMock(CachePersisterFacade.class);

        cachePersisterFacade.groundspeakName("a nice little cache");

        PowerMock.replayAll();
        assertTrue(new EventHandlerLoc(cachePersisterFacade).text(EventHandlerLoc.XPATH_WPTNAME,
                "  a nice little cache   "));
        PowerMock.verifyAll();
    }
}
