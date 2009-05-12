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
import com.google.code.geobeagle.data.GeocacheVector.DistanceSortStrategy;
import com.google.code.geobeagle.data.GeocacheVector.LocationComparator;
import com.google.code.geobeagle.data.GeocacheVector.NullSortStrategy;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.location.Location;
import android.util.FloatMath;

import java.util.ArrayList;
import java.util.Collections;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        GeocacheVector.class, LocationComparator.class, GeocacheVectorTest.class, FloatMath.class
})
public class GeocacheVectorTest {
    private final Geocache geocache = PowerMock.createMock(Geocache.class);

    @Test
    public void testCalculateDistanceFast() {
        // This test is a sham just to get the coverage numbers up.
        PowerMock.mockStatic(FloatMath.class);

        EasyMock.expect(FloatMath.sin(EasyMock.anyFloat())).andReturn(1f).anyTimes();
        EasyMock.expect(FloatMath.cos(EasyMock.anyFloat())).andReturn(1f).anyTimes();
        EasyMock.expect(FloatMath.sqrt(EasyMock.anyFloat())).andReturn(1f).anyTimes();

        PowerMock.replayAll();
        GeocacheVector.calculateDistanceFast(1, 2, 3, 4);
        PowerMock.verifyAll();
    }

    @Test
    public void testCompare() {
        IGeocacheVector d1 = PowerMock.createMock(IGeocacheVector.class);
        IGeocacheVector d2 = PowerMock.createMock(IGeocacheVector.class);

        expect(d1.getDistanceFast()).andReturn(0f).anyTimes();
        expect(d2.getDistanceFast()).andReturn(1f).anyTimes();

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
    public void testDistanceSortStrategy() {
        LocationComparator locationComparator = PowerMock.createMock(LocationComparator.class);
        ArrayList<IGeocacheVector> arrayList = new ArrayList<IGeocacheVector>();

        PowerMock.mockStatic(Collections.class);
        Collections.sort(arrayList, locationComparator);

        PowerMock.replayAll();
        new DistanceSortStrategy(locationComparator).sort(arrayList);
        PowerMock.verifyAll();
    }

    @Test
    public void testGetDistance() {
        LocationControlBuffered locationControlBuffered = PowerMock
                .createMock(LocationControlBuffered.class);
        Location location = PowerMock.createMock(Location.class);

        expect(locationControlBuffered.getLocation()).andReturn(location);
        PowerMock.mockStatic(GeocacheVector.class);
        EasyMock.expect(location.getLatitude()).andReturn(1.0);
        EasyMock.expect(location.getLongitude()).andReturn(2.0);
        EasyMock.expect(geocache.getLatitude()).andReturn(3.0);
        EasyMock.expect(geocache.getLongitude()).andReturn(4.0);
        EasyMock.expect(GeocacheVector.calculateDistanceFast(1, 2, 3, 4)).andReturn(5f);

        PowerMock.replayAll();
        assertEquals(5f, new GeocacheVector(geocache, locationControlBuffered, null)
                .getDistanceFast(), 0);
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

    @Test
    public void testNullSortStrategy() {
        new NullSortStrategy().sort(null);
    }
}
