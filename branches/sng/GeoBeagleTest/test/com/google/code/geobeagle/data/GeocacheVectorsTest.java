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

import com.google.code.geobeagle.data.GeocacheVector.LocationComparator;
import com.google.code.geobeagle.data.di.GeocacheVectorFactory;

import android.location.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

public class GeocacheVectorsTest extends TestCase {
    public void testAddLocations() {
        GeocacheVectorFactory geocacheVectorFactory = createMock(GeocacheVectorFactory.class);
        Location here = createMock(Location.class);
        GeocacheVector geocacheVector = createMock(GeocacheVector.class);

        expect(geocacheVectorFactory.create("a cache", here)).andReturn(geocacheVector);

        replay(geocacheVectorFactory);
        ArrayList<CharSequence> locations = new ArrayList<CharSequence>(0);
        locations.add("a cache");
        GeocacheVectors geocacheVectors = new GeocacheVectors(null,
                geocacheVectorFactory);
        geocacheVectors.addLocations(locations, here);
        assertEquals(geocacheVector, geocacheVectors.get(0));
        verify(geocacheVectorFactory);
    }

    public void testDelete() {
        GeocacheVector destinationVector1 = createMock(GeocacheVector.class);
        GeocacheVector destinationVector2 = createMock(GeocacheVector.class);

        GeocacheVectors geocacheVectors = new GeocacheVectors(null, null);
        geocacheVectors.add(destinationVector1);
        geocacheVectors.add(destinationVector2);
        geocacheVectors.delete(0);
        assertEquals(destinationVector1, geocacheVectors.get(0));
    }

    public void testGetAdapterData() {
        IGeocacheVector destinationVector = createMock(IGeocacheVector.class);

        Map<String, Object> map = new HashMap<String, Object>(0);
        expect(destinationVector.getViewMap()).andReturn(map);

        replay(destinationVector);
        GeocacheVectors geocacheVectors = new GeocacheVectors(null, null);
        geocacheVectors.add(destinationVector);
        final ArrayList<Map<String, Object>> adapterData = geocacheVectors.getAdapterData();
        assertEquals(map, adapterData.get(0));
        verify(destinationVector);
    }

    public void testGetId() {
        IGeocacheVector destinationVector = createMock(IGeocacheVector.class);

        expect(destinationVector.getId()).andReturn("GC123");

        replay(destinationVector);
        GeocacheVectors geocacheVectors = new GeocacheVectors(null, null);
        geocacheVectors.add(destinationVector);
        assertEquals("GC123", geocacheVectors.getId(0));
        verify(destinationVector);
    }

    public void testGetLocation() {
        IGeocacheVector destinationVector = createMock(IGeocacheVector.class);

        expect(destinationVector.getCoordinatesIdAndName()).andReturn("122 37 (GC1234)");

        replay(destinationVector);
        GeocacheVectors geocacheVectors = new GeocacheVectors(null, null);
        geocacheVectors.add(destinationVector);
        assertEquals("122 37 (GC1234)", geocacheVectors.getCoordinatesIdAndName(0));
        verify(destinationVector);
    }

    public void testReset() {
        IGeocacheVector destinationVector = createMock(IGeocacheVector.class);

        replay(destinationVector);
        GeocacheVectors geocacheVectors = new GeocacheVectors(null, null);
        geocacheVectors.add(destinationVector);
        geocacheVectors.reset(12); // can't test ensureCapacity.
        final ArrayList<Map<String, Object>> adapterData = geocacheVectors.getAdapterData();
        assertEquals(0, adapterData.size());
        verify(destinationVector);
    }

    @SuppressWarnings("unchecked")
    public void testSort() {
        LocationComparator locationComparator = createMock(LocationComparator.class);

        locationComparator.sort((ArrayList<IGeocacheVector>)anyObject());

        replay(locationComparator);
        GeocacheVectors geocacheVectors = new GeocacheVectors(locationComparator, null);
        geocacheVectors.sort();
        verify(locationComparator);
    }
}
