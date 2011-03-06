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

package com.google.code.geobeagle.activity.map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.android.maps.GeoPoint;
import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.compass.fieldnotes.Toaster;
import com.google.code.geobeagle.activity.map.QueryManager.CachedNeedsLoading;
import com.google.code.geobeagle.activity.map.QueryManager.LoaderImpl;
import com.google.code.geobeagle.database.DbFrontend;
import com.google.code.geobeagle.database.WhereFactoryFixedArea;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        DensityPatchManager.class, Log.class, QueryManager.class
})
public class QueryManagerTest {
    @Before
    public void ignoreLogs() {
        PowerMock.mockStatic(Log.class);
        EasyMock.expect(Log.d((String)EasyMock.anyObject(), (String)EasyMock.anyObject()))
                .andReturn(0).anyTimes();
    }

    @Test
    public void testLoad() throws Exception {
        GeoPoint topLeft = PowerMock.createMock(GeoPoint.class);
        GeoPoint bottomRight = PowerMock.createMock(GeoPoint.class);
        QueryManager.PeggedLoader peggedLoader = PowerMock
                .createMock(QueryManager.PeggedLoader.class);
        WhereFactoryFixedArea whereFactoryFixedArea = PowerMock
                .createMock(WhereFactoryFixedArea.class);
        ArrayList<Geocache> list = new ArrayList<Geocache>();
        int[] latLonMinMax = {
                0, 0, 0, 0
        };

        PowerMock.expectNew(WhereFactoryFixedArea.class, 35.99, -122.02, 37.01, -120.98).andReturn(
                whereFactoryFixedArea);
        EasyMock.expect(bottomRight.getLatitudeE6()).andReturn(36000000);
        EasyMock.expect(bottomRight.getLongitudeE6()).andReturn(-121000000);
        EasyMock.expect(topLeft.getLatitudeE6()).andReturn(37000000);
        EasyMock.expect(topLeft.getLongitudeE6()).andReturn(-122000000);
        EasyMock.expect(
                peggedLoader.load(35990000, -122020000, 37010000, -120980000,
                        whereFactoryFixedArea, latLonMinMax)).andReturn(list);
        PowerMock.replayAll();

        QueryManager queryManager = new QueryManager(null, latLonMinMax);
        assertEquals(list, queryManager.load(topLeft, bottomRight, peggedLoader));
        PowerMock.verifyAll();
    }

    @Test
    public void testNeedsLoadingFalse() {
        GeoPoint newTopLeft = PowerMock.createMock(GeoPoint.class);
        GeoPoint newBottomRight = PowerMock.createMock(GeoPoint.class);
        CachedNeedsLoading cachedNeedsLoading = PowerMock.createMock(CachedNeedsLoading.class);

        EasyMock.expect(cachedNeedsLoading.needsLoading(newTopLeft, newBottomRight))
                .andReturn(true);
        EasyMock.expect(newTopLeft.getLatitudeE6()).andReturn(36000000);
        EasyMock.expect(newBottomRight.getLatitudeE6()).andReturn(35000000);

        EasyMock.expect(newTopLeft.getLongitudeE6()).andReturn(-121000000);
        EasyMock.expect(newBottomRight.getLongitudeE6()).andReturn(-120000000);

        PowerMock.replayAll();
        int[] latLonMinMax = {
                34000000, -122000000, 37000000, -119000000
        };
        QueryManager queryManager = new QueryManager(cachedNeedsLoading, latLonMinMax);
        assertEquals(false, queryManager.needsLoading(newTopLeft, newBottomRight));
        PowerMock.verifyAll();
    }

    @Test
    public void testNeedsLoadingTrue() {
        GeoPoint newTopLeft = PowerMock.createMock(GeoPoint.class);
        GeoPoint newBottomRight = PowerMock.createMock(GeoPoint.class);
        CachedNeedsLoading cachedNeedsLoading = PowerMock.createMock(CachedNeedsLoading.class);

        EasyMock.expect(cachedNeedsLoading.needsLoading(newTopLeft, newBottomRight))
                .andReturn(true);
        EasyMock.expect(newTopLeft.getLatitudeE6()).andReturn(37000000);

        PowerMock.replayAll();
        int[] latLonMinMax = {
                0, 0, 0, 0
        };
        QueryManager queryManager = new QueryManager(cachedNeedsLoading);
        assertEquals(true, queryManager.needsLoading(newTopLeft, newBottomRight));
        PowerMock.verifyAll();
    }

    @Test
    public void testLoaderImpl() {
        DbFrontend dbFrontend = PowerMock.createMock(DbFrontend.class);
        WhereFactoryFixedArea where = PowerMock.createMock(WhereFactoryFixedArea.class);
        ArrayList<Geocache> fullList = new ArrayList<Geocache>();
        int[] newBounds = {
                0, 0, 0, 0
        };

        EasyMock.expect(dbFrontend.loadCaches(0, 0, where)).andReturn(fullList);

        PowerMock.replayAll();
        new LoaderImpl(dbFrontend).load(0, 1, 2, 3, where, newBounds);
        PowerMock.verifyAll();
    }

    @Test
    public void testPeggedLoader() {
        DbFrontend geocachesLoader = PowerMock.createMock(DbFrontend.class);
        WhereFactoryFixedArea where = PowerMock.createMock(WhereFactoryFixedArea.class);
        LoaderImpl loaderImpl = PowerMock.createMock(LoaderImpl.class);
        int[] newBounds = {
                0, 0, 0, 0
        };
        ArrayList<Geocache> fullList = new ArrayList<Geocache>();

        EasyMock.expect(geocachesLoader.count(0, 0, where)).andReturn(100);
        EasyMock.expect(loaderImpl.load(0, 1, 2, 3, where, newBounds)).andReturn(fullList);

        PowerMock.replayAll();
        final ArrayList<Geocache> list = new QueryManager.PeggedLoader(geocachesLoader, null,
                loaderImpl).load(0, 1, 2, 3, where, newBounds);
        assertEquals(fullList, list);
        PowerMock.verifyAll();
    }

    @Test
    public void testCachedNeedsLoading() {
        GeoPoint topLeft = PowerMock.createMock(GeoPoint.class);
        GeoPoint bottomRight = PowerMock.createMock(GeoPoint.class);

        PowerMock.replayAll();
        CachedNeedsLoading cachedNeedsLoading = new CachedNeedsLoading(topLeft, bottomRight);
        assertFalse(cachedNeedsLoading.needsLoading(topLeft, bottomRight));
        assertTrue(cachedNeedsLoading.needsLoading(bottomRight, topLeft));
        assertFalse(cachedNeedsLoading.needsLoading(bottomRight, topLeft));
        PowerMock.verifyAll();
    }

    @Test
    public void testPeggedLoaderTooMany() {
        DbFrontend dbFrontend = PowerMock.createMock(DbFrontend.class);
        WhereFactoryFixedArea where = PowerMock.createMock(WhereFactoryFixedArea.class);
        Toaster toaster = PowerMock.createMock(Toaster.class);
        LoaderImpl loaderImpl = PowerMock.createMock(LoaderImpl.class);

        ArrayList<Geocache> nullList = new ArrayList<Geocache>();
        int[] newBounds = {
                0, 0, 0, 0
        };
        toaster.toast(R.string.too_many_caches, Toast.LENGTH_SHORT);
        EasyMock.expect(dbFrontend.count(0, 0, where)).andReturn(2000);

        PowerMock.replayAll();
        final ArrayList<Geocache> list = new QueryManager.PeggedLoader(dbFrontend, toaster,
                loaderImpl).load(0, 1, 2, 3, where, newBounds);
        assertEquals(nullList, list);
        assertEquals(newBounds[0], 0);
        assertEquals(newBounds[1], 0);
        assertEquals(newBounds[2], 0);
        assertEquals(newBounds[3], 0);
        PowerMock.verifyAll();
    }

}
