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

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.ResourceProvider;
import com.google.code.geobeagle.data.GeocacheVector.LocationComparator;
import com.google.code.geobeagle.data.GeocacheVector.MyLocation;

import java.util.Map;

import junit.framework.TestCase;

public class GeocacheVectorTest extends TestCase {
    private final Geocache geocache = createMock(Geocache.class);

    public void testCompare() {
        IGeocacheVector d1 = createMock(IGeocacheVector.class);
        IGeocacheVector d2 = createMock(IGeocacheVector.class);

        expect(d1.getDistance()).andReturn(0f).anyTimes();
        expect(d2.getDistance()).andReturn(1f).anyTimes();

        replay(d1);
        replay(d2);
        LocationComparator locationComparator = new LocationComparator();
        assertEquals(-1, locationComparator.compare(d1, d2));
        assertEquals(1, locationComparator.compare(d2, d1));
        assertEquals(0, locationComparator.compare(d1, d1));
        verify(d1);
        verify(d2);

    }

    public void testDestinationGetCacheListDisplayMap() {
        DistanceFormatter distanceFormatter = createMock(DistanceFormatter.class);
        expect(geocache.getIdAndName()).andReturn("a geocache");
        expect(distanceFormatter.format(3.5f)).andReturn("3.5m");

        replay(geocache);
        replay(distanceFormatter);
        final Map<String, Object> viewMap = new GeocacheVector(geocache, 3.5f, distanceFormatter)
                .getViewMap();
        assertEquals("a geocache", viewMap.get("cache"));
        assertEquals("3.5m", viewMap.get("distance"));
        verify(geocache);
        verify(distanceFormatter);
    }

    public void testDestinationGetDistance() {
        assertEquals(3.5f, new GeocacheVector(geocache, 3.5f, null).getDistance());
    }

    public void testDestinationGetId() {
        expect(geocache.getId()).andReturn("a geocache");

        replay(geocache);
        GeocacheVector geocacheVector = new GeocacheVector(geocache, 3.5f, null);
        assertEquals("a geocache", geocacheVector.getId());
        verify(geocache);
    }

    public void testDestinationGetLocation() {
        expect(geocache.getCoordinatesIdAndName()).andReturn("343 2323 (a geocache)");

        replay(geocache);
        GeocacheVector geocacheVector = new GeocacheVector(geocache, 3.5f, null);
        assertEquals("343 2323 (a geocache)", geocacheVector.getCoordinatesIdAndName());
        verify(geocache);
    }

    public void testMyLocation() {
        ResourceProvider resourceProvider = createMock(ResourceProvider.class);
        expect(resourceProvider.getString(R.string.my_current_location)).andReturn(
                "My Current Location").anyTimes();

        replay(resourceProvider);
        MyLocation myLocation = new MyLocation(resourceProvider);
        assertEquals(null, myLocation.getCoordinatesIdAndName());
        assertEquals(null, myLocation.getDestination());
        assertEquals(-1.0f, myLocation.getDistance());
        assertEquals("My Current Location", myLocation.getId());
        final Map<String, Object> viewMap = myLocation.getViewMap();
        assertEquals("My Current Location", viewMap.get("cache"));
        verify(resourceProvider);
    }
}
