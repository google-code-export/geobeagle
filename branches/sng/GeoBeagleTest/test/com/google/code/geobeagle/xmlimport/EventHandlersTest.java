
package com.google.code.geobeagle.xmlimport;

import static org.junit.Assert.assertEquals;

import org.easymock.classextension.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class EventHandlersTest {

    @Test
    public void testGet() {
        EventHandler eventHandler1 = EasyMock.createMock(EventHandler.class);
        EventHandler eventHandler2 = EasyMock.createMock(EventHandler.class);

        EventHandlers eventHandlers = new EventHandlers();
        eventHandlers.add(".gpx", eventHandler1);
        eventHandlers.add(".loc", eventHandler2);

        assertEquals(null, eventHandlers.get("f"));
        assertEquals(null, eventHandlers.get("foo"));
        assertEquals(null, eventHandlers.get("foo.gpxx"));
        assertEquals(eventHandler1, eventHandlers.get("foo.gpx"));
        assertEquals(eventHandler2, eventHandlers.get("bar.lOc"));
    }
}
