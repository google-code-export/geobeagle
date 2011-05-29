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

import com.google.android.maps.GeoPoint;
import com.google.android.maps.Projection;
import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.cachelist.GeoBeagleTest;
import com.google.code.geobeagle.activity.map.click.MapClickIntentFactory;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        View.class, Log.class, CachePinsOverlay.class
})
public class CachePinsOverlayFactoryTest extends GeoBeagleTest {
    private Resources resources;
    private CacheItemFactory cacheItemFactory;
    private Activity activity;
    private ArrayList<Geocache> list;
    private Context context;
    private MapClickIntentFactory mapClickIntentFactory;

    @Before
    public void setUp() {
        resources = PowerMock.createMock(Resources.class);
        cacheItemFactory = PowerMock.createMock(CacheItemFactory.class);
        activity = PowerMock.createMock(Activity.class);
        list = new ArrayList<Geocache>();
        context = PowerMock.createMock(Context.class);
        mapClickIntentFactory = PowerMock.createMock(MapClickIntentFactory.class);
    }

    @Test
    public void testGetCachePinsOverlay() throws Exception {
        GeoMapView geoMapView = PowerMock.createMock(GeoMapView.class);
        QueryManager queryManager = PowerMock.createMock(QueryManager.class);
        CachePinsOverlay cachePinsOverlay = PowerMock.createMock(CachePinsOverlay.class);
        Projection projection = PowerMock.createMock(Projection.class);
        GeoPoint newTopLeft = PowerMock.createMock(GeoPoint.class);
        GeoPoint newBottomRight = PowerMock.createMock(GeoPoint.class);

        EasyMock.expect(geoMapView.getProjection()).andReturn(projection);
        EasyMock.expect(activity.findViewById(R.id.mapview)).andReturn(geoMapView);
        EasyMock.expect(projection.fromPixels(0, 0)).andReturn(newTopLeft);
        EasyMock.expect(geoMapView.getRight()).andReturn(100);
        EasyMock.expect(geoMapView.getBottom()).andReturn(200);
        EasyMock.expect(projection.fromPixels(100, 200)).andReturn(newBottomRight);
        EasyMock.expect(queryManager.needsLoading(newTopLeft, newBottomRight)).andReturn(true);

        EasyMock.expect(queryManager.load(newTopLeft, newBottomRight, null)).andReturn(list);
        PowerMock.expectNew(CachePinsOverlay.class, resources, cacheItemFactory, activity, list,
                mapClickIntentFactory).andReturn(cachePinsOverlay);
        PowerMock.expectNew(CachePinsOverlay.class, resources, cacheItemFactory, activity, list,
                mapClickIntentFactory).andReturn(cachePinsOverlay);

        PowerMock.replayAll();
        CachePinsOverlayFactory cachePinsOverlayFactory = new CachePinsOverlayFactory(activity,
                cacheItemFactory, queryManager, resources, null, mapClickIntentFactory);
        assertEquals(cachePinsOverlay, cachePinsOverlayFactory.getCachePinsOverlay());
        PowerMock.verifyAll();
    }

    @Test
    public void testGetCachePinsOverlayDoesntNeedLoading() throws Exception {
        GeoMapView geoMapView = PowerMock.createMock(GeoMapView.class);
        QueryManager queryManager = PowerMock.createMock(QueryManager.class);
        CachePinsOverlay cachePinsOverlay = PowerMock.createMock(CachePinsOverlay.class);
        Projection projection = PowerMock.createMock(Projection.class);
        GeoPoint newTopLeft = PowerMock.createMock(GeoPoint.class);
        GeoPoint newBottomRight = PowerMock.createMock(GeoPoint.class);

        EasyMock.expect(activity.findViewById(R.id.mapview)).andReturn(geoMapView);
        EasyMock.expect(geoMapView.getProjection()).andReturn(projection);
        EasyMock.expect(projection.fromPixels(0, 0)).andReturn(newTopLeft);
        EasyMock.expect(geoMapView.getRight()).andReturn(100);
        EasyMock.expect(geoMapView.getBottom()).andReturn(200);
        EasyMock.expect(projection.fromPixels(100, 200)).andReturn(newBottomRight);
        EasyMock.expect(queryManager.needsLoading(newTopLeft, newBottomRight)).andReturn(false);
        PowerMock.expectNew(CachePinsOverlay.class, resources, cacheItemFactory, activity, list,
                mapClickIntentFactory).andReturn(cachePinsOverlay);

        PowerMock.replayAll();
        CachePinsOverlayFactory cachePinsOverlayFactory = new CachePinsOverlayFactory(
                activity, cacheItemFactory, queryManager, resources, null, mapClickIntentFactory);
        assertEquals(cachePinsOverlay, cachePinsOverlayFactory.getCachePinsOverlay());
        PowerMock.verifyAll();
    }
}
