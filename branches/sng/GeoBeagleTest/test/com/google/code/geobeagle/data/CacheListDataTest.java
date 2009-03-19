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

import com.google.code.geobeagle.data.di.GeocacheVectorFactory;

import android.location.Location;

import java.util.ArrayList;
import java.util.Map;

import junit.framework.TestCase;

public class CacheListDataTest extends TestCase {
    public void testAdd() {
        GeocacheVectorFactory geocacheVectorFactory = createMock(GeocacheVectorFactory.class);
        GeocacheVectors geocacheVectors = createMock(GeocacheVectors.class);
        IGeocacheVector myLocation = createMock(IGeocacheVector.class);
        Location here = createMock(Location.class);

        ArrayList<CharSequence> locations = new ArrayList<CharSequence>(0);
        geocacheVectors.reset(0);
        geocacheVectors.addLocations(locations, here);
        expect(geocacheVectorFactory.createMyLocation()).andReturn(myLocation);
        geocacheVectors.add(myLocation);
        geocacheVectors.sort();

        replay(geocacheVectorFactory);
        replay(geocacheVectors);
        new CacheListData(geocacheVectors, geocacheVectorFactory).add(locations, here);
        verify(geocacheVectorFactory);
        verify(geocacheVectors);
    }

    public void testGetAdapterData() {
        GeocacheVectors geocacheVectors = createMock(GeocacheVectors.class);
        ArrayList<Map<String, Object>> adapterData = new ArrayList<Map<String, Object>>(0);

        expect(geocacheVectors.getAdapterData()).andReturn(adapterData);

        replay(geocacheVectors);
        assertEquals(adapterData, new CacheListData(geocacheVectors, null).getAdapterData());
        verify(geocacheVectors);
    }

    public void testGetId() {
        GeocacheVectors geocacheVectors = createMock(GeocacheVectors.class);

        expect(geocacheVectors.getId(8)).andReturn("GC123");

        replay(geocacheVectors);
        assertEquals("GC123", new CacheListData(geocacheVectors, null).getId(8));
        verify(geocacheVectors);
    }

    public void testGetLocation() {
        GeocacheVectors geocacheVectors = createMock(GeocacheVectors.class);

        expect(geocacheVectors.getCoordinatesIdAndName(8)).andReturn("a cache");

        replay(geocacheVectors);
        assertEquals("a cache", new CacheListData(geocacheVectors, null).getCoordinatesIdAndName(8));
        verify(geocacheVectors);
    }
}
