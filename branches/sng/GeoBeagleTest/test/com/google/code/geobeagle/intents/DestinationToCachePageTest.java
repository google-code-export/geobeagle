
package com.google.code.geobeagle.intents;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import com.google.code.geobeagle.Destination;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.ResourceProvider;
import com.google.code.geobeagle.ui.ContentSelector;

import junit.framework.TestCase;

public class DestinationToCachePageTest extends TestCase {

    public void testConvert() {
        ResourceProvider resourceProvider = createMock(ResourceProvider.class);
        Destination destination = createMock(Destination.class);
        ContentSelector contentSelector = createMock(ContentSelector.class);
        expect(destination.getId()).andReturn("FOO");
        expect(destination.getContentIndex()).andReturn(0);
        expect(resourceProvider.getStringArray(R.array.cache_page_url)).andReturn(new String[] {
                "http://coord.info/GC%1$s", ""
        });
        expect(contentSelector.getIndex()).andReturn(0);

        replay(destination);
        replay(resourceProvider);
        DestinationToCachePage destinationToCachePage = new DestinationToCachePage(
                resourceProvider, contentSelector);
        assertEquals("http://coord.info/GCFOO", destinationToCachePage.convert(destination));
        verify(destination);
        verify(resourceProvider);
    }

}
