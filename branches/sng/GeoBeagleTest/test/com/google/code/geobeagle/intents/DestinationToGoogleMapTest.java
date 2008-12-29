package com.google.code.geobeagle.intents;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import com.google.code.geobeagle.Destination;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.ResourceProvider;

import junit.framework.TestCase;

public class DestinationToGoogleMapTest extends TestCase {
    public void testConvert() {
        ResourceProvider resourceProvider = createMock(ResourceProvider.class);
        Destination destination = createMock(Destination.class);
        expect(destination.getDescription()).andReturn("GCFOO");
        expect(destination.getLatitude()).andReturn(37.123);
        expect(destination.getLongitude()).andReturn(122.345);
        expect(resourceProvider.getString(R.string.map_intent)).andReturn("geo:0,0?q=%1$.5f,%2$.5f (%3$s)");
        
        replay(destination);
        replay(resourceProvider);
        DestinationToGoogleMap destinationToCachePage = new DestinationToGoogleMap(
                resourceProvider);
        assertEquals("geo:0,0?q=37.12300,122.34500 (GCFOO)", destinationToCachePage.convert(destination));
        verify(destination);
        verify(resourceProvider);
    }
}
