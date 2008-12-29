
package com.google.code.geobeagle.intents;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import com.google.code.geobeagle.Destination;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.ResourceProvider;

import junit.framework.TestCase;

public class DestinationToCachePageTest extends TestCase {

    public void testConvert() {
        ResourceProvider resourceProvider = createMock(ResourceProvider.class);
        Destination destination = createMock(Destination.class);
        expect(destination.getDescription()).andReturn("GCFOO");
        expect(resourceProvider.getString(R.string.cache_page_url)).andReturn("http://coord.info/%1$s");
        
        replay(destination);
        replay(resourceProvider);
        DestinationToCachePage destinationToCachePage = new DestinationToCachePage(
                resourceProvider);
        assertEquals("http://coord.info/GCFOO", destinationToCachePage.convert(destination));
        verify(destination);
        verify(resourceProvider);
    }

}
