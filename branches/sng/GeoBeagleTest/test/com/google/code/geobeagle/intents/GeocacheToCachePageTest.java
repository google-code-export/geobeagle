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

package com.google.code.geobeagle.intents;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.ResourceProvider;
import com.google.code.geobeagle.data.Geocache;
import com.google.code.geobeagle.data.Geocache.Provider;

import junit.framework.TestCase;

public class GeocacheToCachePageTest extends TestCase {

    public void testConvert() {
        ResourceProvider resourceProvider = createMock(ResourceProvider.class);
        Geocache geocache = createMock(Geocache.class);

        expect(geocache.getShortId()).andReturn("FOO");
        expect(geocache.getContentProvider()).andReturn(Provider.GROUNDSPEAK);
        expect(resourceProvider.getStringArray(R.array.cache_page_url)).andReturn(new String[] {
                "", "http://coord.info/GC%1$s",
        });

        replay(geocache);
        replay(resourceProvider);
        GeocacheToCachePage geocacheToCachePage = new GeocacheToCachePage(resourceProvider);
        assertEquals("http://coord.info/GCFOO", geocacheToCachePage.convert(geocache));
        verify(geocache);
        verify(resourceProvider);
    }

}
