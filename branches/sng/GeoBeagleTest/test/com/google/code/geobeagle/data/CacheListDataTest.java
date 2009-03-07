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

package com.google.code.geobeagle.data;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import com.google.code.geobeagle.data.di.DestinationVectorFactory;

import android.location.Location;

import java.util.ArrayList;
import java.util.Map;

import junit.framework.TestCase;

public class CacheListDataTest extends TestCase {
    public void testAdd() {
        DestinationVectorFactory destinationVectorFactory = createMock(DestinationVectorFactory.class);
        DestinationVectors destinationVectors = createMock(DestinationVectors.class);
        IDestinationVector myLocation = createMock(IDestinationVector.class);
        Location here = createMock(Location.class);

        ArrayList<CharSequence> locations = new ArrayList<CharSequence>(0);
        destinationVectors.reset(0);
        destinationVectors.addLocations(locations, here);
        expect(destinationVectorFactory.createMyLocation()).andReturn(myLocation);
        destinationVectors.add(myLocation);
        destinationVectors.sort();

        replay(destinationVectorFactory);
        replay(destinationVectors);
        new CacheListData(destinationVectors, destinationVectorFactory).add(locations, here);
        verify(destinationVectorFactory);
        verify(destinationVectors);
    }

    public void testGetAdapterData() {
        DestinationVectors destinationVectors = createMock(DestinationVectors.class);
        ArrayList<Map<String, Object>> adapterData = new ArrayList<Map<String, Object>>(0);

        expect(destinationVectors.getAdapterData()).andReturn(adapterData);

        replay(destinationVectors);
        assertEquals(adapterData, new CacheListData(destinationVectors, null).getAdapterData());
        verify(destinationVectors);
    }

    public void testGetId() {
        DestinationVectors destinationVectors = createMock(DestinationVectors.class);

        expect(destinationVectors.getId(8)).andReturn("GC123");

        replay(destinationVectors);
        assertEquals("GC123", new CacheListData(destinationVectors, null).getId(8));
        verify(destinationVectors);
    }

    public void testGetLocation() {
        DestinationVectors destinationVectors = createMock(DestinationVectors.class);

        expect(destinationVectors.getLocation(8)).andReturn("a cache");

        replay(destinationVectors);
        assertEquals("a cache", new CacheListData(destinationVectors, null).getLocation(8));
        verify(destinationVectors);
    }
}
