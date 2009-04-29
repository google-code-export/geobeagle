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
import static org.junit.Assert.assertEquals;

import com.google.code.geobeagle.LocationControlBuffered;
import com.google.code.geobeagle.data.GeocacheVector.LocationComparator;

import org.junit.Test;

import java.util.ArrayList;

public class GeocacheVectorsTest {
    @Test
    public void testAddLocations() {
        GeocacheVectorFactory geocacheVectorFactory = createMock(GeocacheVectorFactory.class);
        LocationControlBuffered here = createMock(LocationControlBuffered.class);
        Geocache geocache = createMock(Geocache.class);
        GeocacheVector geocacheVector = createMock(GeocacheVector.class);

        expect(geocacheVectorFactory.create(geocache, here)).andReturn(geocacheVector);

        replay(geocacheVectorFactory);
        ArrayList<Geocache> locations = new ArrayList<Geocache>(0);
        locations.add(geocache);
        GeocacheVectors geocacheVectors = new GeocacheVectors(null, geocacheVectorFactory);
        geocacheVectors.addLocations(locations, here);
        assertEquals(geocacheVector, geocacheVectors.get(0));
        verify(geocacheVectorFactory);
    }

    @Test
    public void testDelete() {
        GeocacheVector destinationVector1 = createMock(GeocacheVector.class);
        GeocacheVector destinationVector2 = createMock(GeocacheVector.class);

        GeocacheVectors geocacheVectors = new GeocacheVectors(null, null);
        geocacheVectors.add(destinationVector1);
        geocacheVectors.add(destinationVector2);
        geocacheVectors.remove(0);
        assertEquals(destinationVector1, geocacheVectors.get(0));
    }

    @Test
    public void testReset() {
        IGeocacheVector destinationVector = createMock(IGeocacheVector.class);

        replay(destinationVector);
        GeocacheVectors geocacheVectors = new GeocacheVectors(null, null);
        geocacheVectors.add(destinationVector);
        geocacheVectors.reset(12); // can't test ensureCapacity.
        assertEquals(0, geocacheVectors.size());
        verify(destinationVector);
    }

    @Test
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
