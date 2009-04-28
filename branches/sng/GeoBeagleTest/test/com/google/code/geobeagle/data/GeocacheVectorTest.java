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
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import com.google.code.geobeagle.LocationControlBuffered;
import com.google.code.geobeagle.data.GeocacheVector.LocationComparator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.location.Location;

import java.util.ArrayList;
import java.util.Collections;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        LocationComparator.class, GeocacheVectorTest.class
})
public class GeocacheVectorTest {
    private final Geocache geocache = PowerMock.createMock(Geocache.class);

    @Test
    public void testCompare() {
        IGeocacheVector d1 = PowerMock.createMock(IGeocacheVector.class);
        IGeocacheVector d2 = PowerMock.createMock(IGeocacheVector.class);

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

    @Test
    public void testSort() {
        PowerMock.mockStatic(Collections.class);
        ArrayList<IGeocacheVector> arrayList = new ArrayList<IGeocacheVector>();
        LocationComparator locationComparator = new LocationComparator();
        Collections.sort(arrayList, locationComparator);

        PowerMock.replay(Collections.class);
        locationComparator.sort(arrayList);
        PowerMock.verify(Collections.class);
    }

    @Test
    public void testGetDistance() {
        LocationControlBuffered locationControlBuffered = PowerMock
                .createMock(LocationControlBuffered.class);
        Location location = PowerMock.createMock(Location.class);

        expect(locationControlBuffered.getLocation()).andReturn(location);
        expect(geocache.calculateDistance(location)).andReturn(3.5f);

        PowerMock.replayAll();
        assertEquals(3.5f, new GeocacheVector(geocache, locationControlBuffered, null)
                .getDistance(), 0);
        PowerMock.verifyAll();
    }

    @Test
    public void testGetFormattedDistance() {
        DistanceFormatter distanceFormatter = PowerMock.createMock(DistanceFormatter.class);
        LocationControlBuffered locationControlBuffered = PowerMock
                .createMock(LocationControlBuffered.class);
        Location location = PowerMock.createMock(Location.class);

        expect(locationControlBuffered.getLocation()).andReturn(location);
        expect(geocache.calculateDistance(location)).andReturn(3.5f);
        expect(distanceFormatter.format(3.5f)).andReturn("3.5m");

        PowerMock.replayAll();
        GeocacheVector geocacheVector = new GeocacheVector(geocache, locationControlBuffered,
                distanceFormatter);
        assertEquals("3.5m", geocacheVector.getFormattedDistance());
        PowerMock.verifyAll();
    }

    @Test
    public void testGetGeocache() {
        PowerMock.replayAll();
        GeocacheVector geocacheVector = new GeocacheVector(geocache, null, null);
        assertEquals(geocache, geocacheVector.getGeocache());
        PowerMock.verifyAll();
    }

    @Test
    public void testGetId() {
        expect(geocache.getId()).andReturn("a geocache");

        PowerMock.replayAll();
        GeocacheVector geocacheVector = new GeocacheVector(geocache, null, null);
        assertEquals("a geocache", geocacheVector.getId());
        PowerMock.verifyAll();
    }

    @Test
    public void testGetIdAndName() {
        expect(geocache.getIdAndName()).andReturn("GC123: a geocache");

        PowerMock.replayAll();
        GeocacheVector geocacheVector = new GeocacheVector(geocache, null, null);
        assertEquals("GC123: a geocache", geocacheVector.getIdAndName());
        PowerMock.verifyAll();
    }
}
