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

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import com.google.code.geobeagle.data.DestinationVector.DestinationVectorFactory;
import com.google.code.geobeagle.data.DestinationVector.LocationComparator;

import android.location.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

public class DestinationVectorsTest extends TestCase {
    public void testAddLocations() {
        DestinationVectorFactory destinationVectorFactory = createMock(DestinationVectorFactory.class);
        Location here = createMock(Location.class);
        DestinationVector destinationVector = createMock(DestinationVector.class);
        
        expect(destinationVectorFactory.create("a cache", here)).andReturn(destinationVector);

        replay(destinationVectorFactory);
        ArrayList<CharSequence> locations = new ArrayList<CharSequence>(0);
        locations.add("a cache");
        DestinationVectors destinationVectors = new DestinationVectors(null,
                destinationVectorFactory);
        destinationVectors.addLocations(locations, here);
        assertEquals(destinationVector, destinationVectors.get(0));
        verify(destinationVectorFactory);
    }

    public void testGetAdapterData() {
        IDestinationVector destinationVector = createMock(IDestinationVector.class);

        Map<String, Object> map = new HashMap<String, Object>(0);
        expect(destinationVector.getViewMap()).andReturn(map);

        replay(destinationVector);
        DestinationVectors destinationVectors = new DestinationVectors(null, null);
        destinationVectors.add(destinationVector);
        final ArrayList<Map<String, Object>> adapterData = destinationVectors.getAdapterData();
        assertEquals(map, adapterData.get(0));
        verify(destinationVector);
    }

    public void testGetId() {
        IDestinationVector destinationVector = createMock(IDestinationVector.class);

        expect(destinationVector.getId()).andReturn("GC123");

        replay(destinationVector);
        DestinationVectors destinationVectors = new DestinationVectors(null, null);
        destinationVectors.add(destinationVector);
        assertEquals("GC123", destinationVectors.getId(0));
        verify(destinationVector); 
    }
    
    public void testGetLocation() {
        IDestinationVector destinationVector = createMock(IDestinationVector.class);

        expect(destinationVector.getLocation()).andReturn("122 37 (GC1234)");

        replay(destinationVector);
        DestinationVectors destinationVectors = new DestinationVectors(null, null);
        destinationVectors.add(destinationVector);
        assertEquals("122 37 (GC1234)", destinationVectors.getLocation(0));
        verify(destinationVector);
    }

    public void testReset() {
        IDestinationVector destinationVector = createMock(IDestinationVector.class);

        replay(destinationVector);
        DestinationVectors destinationVectors = new DestinationVectors(null, null);
        destinationVectors.add(destinationVector);
        destinationVectors.reset(12); // can't test ensureCapacity.
        final ArrayList<Map<String, Object>> adapterData = destinationVectors.getAdapterData();
        assertEquals(0, adapterData.size());
        verify(destinationVector);
    }

    @SuppressWarnings("unchecked")
    public void testSort() {
        LocationComparator locationComparator = createMock(LocationComparator.class);

        locationComparator.sort((ArrayList<IDestinationVector>)anyObject());

        replay(locationComparator);
        DestinationVectors destinationVectors = new DestinationVectors(locationComparator, null);
        destinationVectors.sort();
        verify(locationComparator);
    }
}
