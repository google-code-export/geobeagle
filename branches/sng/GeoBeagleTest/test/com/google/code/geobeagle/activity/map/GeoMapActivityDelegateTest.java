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
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.MenuActions;
import com.google.code.geobeagle.activity.cachelist.CacheList;
import com.google.code.geobeagle.database.GeocachesSql;
import com.google.code.geobeagle.database.WhereFactoryNearestCaches;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        MapView.class, MapController.class, GeoMapActivityDelegate.class
})
public class GeoMapActivityDelegateTest {
    @Test
    public void testInitialize() throws Exception {
        MapView mapView = PowerMock.createMock(MapView.class);
        Context context = PowerMock.createMock(Context.class);
        MyLocationOverlay myLocationOverlay = PowerMock.createMock(MyLocationOverlay.class);
        GeoMapActivity geoMapActivity = PowerMock.createMock(GeoMapActivity.class);
        GeocachesSql geocachesSql = PowerMock.createMock(GeocachesSql.class);
        WhereFactoryNearestCaches whereFactory = PowerMock
                .createMock(WhereFactoryNearestCaches.class);
        MapItemizedOverlay cachesOverlay = PowerMock.createMock(MapItemizedOverlay.class);
        Intent intent = PowerMock.createMock(Intent.class);
        GeoPoint geoPoint = PowerMock.createMock(GeoPoint.class);
        MapController mapController = PowerMock.createMock(MapController.class);

        List<Overlay> mapOverlays = new ArrayList<Overlay>();
        mapView.setBuiltInZoomControls(true);
        mapView.setSatellite(false);
        EasyMock.expect(intent.getFloatExtra("latitude", 0)).andReturn(122.0f);
        EasyMock.expect(intent.getFloatExtra("longitude", 0)).andReturn(37.0f);
        PowerMock.expectNew(GeoPoint.class, 122000000, 37000000).andReturn(geoPoint);
        mapController.setCenter(geoPoint);
        EasyMock.expect(mapController.setZoom(14)).andReturn(14);
        cachesOverlay.addCaches(context, 122, 37, geocachesSql, whereFactory);

        PowerMock.replayAll();
        new GeoMapActivityDelegate(geoMapActivity, mapView, context, myLocationOverlay, null)
                .initialize(intent, geocachesSql, whereFactory, cachesOverlay, mapController,
                        mapOverlays);
        assertEquals(cachesOverlay, mapOverlays.get(0));
        assertEquals(myLocationOverlay, mapOverlays.get(1));
        PowerMock.verifyAll();
    }

    @Test
    public void testOnMenuOpened() {
        Menu menu = PowerMock.createMock(Menu.class);
        MenuItem menuItem = PowerMock.createMock(MenuItem.class);
        MapView mapView = PowerMock.createMock(MapView.class);

        EasyMock.expect(mapView.isSatellite()).andReturn(false);
        EasyMock.expect(menu.findItem(R.id.menu_toggle_satellite)).andReturn(menuItem);
        EasyMock.expect(menuItem.setTitle(R.string.map_view)).andReturn(menuItem);

        EasyMock.expect(mapView.isSatellite()).andReturn(true);
        EasyMock.expect(menu.findItem(R.id.menu_toggle_satellite)).andReturn(menuItem);
        EasyMock.expect(menuItem.setTitle(R.string.satellite_view)).andReturn(menuItem);

        PowerMock.replayAll();
        final GeoMapActivityDelegate geoMapActivityDelegate = new GeoMapActivityDelegate(null,
                mapView, null, null, null);
        geoMapActivityDelegate.onMenuOpened(0, menu);
        geoMapActivityDelegate.onMenuOpened(0, menu);
        PowerMock.verifyAll();
    }

    @Test
    public void testOnOptionsSelected() {
        MenuActions menuActions = PowerMock.createMock(MenuActions.class);
        MenuItem menuItem = PowerMock.createMock(MenuItem.class);

        EasyMock.expect(menuItem.getItemId()).andReturn(12);
        EasyMock.expect(menuActions.act(12)).andReturn(true);

        PowerMock.replayAll();
        new GeoMapActivityDelegate(null, null, null, null, menuActions)
                .onOptionsItemSelected(menuItem);
        PowerMock.verifyAll();
    }

    @Test
    public void testOnPause() {
        MyLocationOverlay myLocationOverlay = PowerMock.createMock(MyLocationOverlay.class);

        myLocationOverlay.disableCompass();
        myLocationOverlay.disableMyLocation();

        PowerMock.replayAll();
        new GeoMapActivityDelegate(null, null, null, myLocationOverlay, null).onPause();
        PowerMock.verifyAll();
    }

    @Test
    public void testOnResume() {
        MyLocationOverlay myLocationOverlay = PowerMock.createMock(MyLocationOverlay.class);

        EasyMock.expect(myLocationOverlay.enableCompass()).andReturn(true);
        EasyMock.expect(myLocationOverlay.enableMyLocation()).andReturn(true);

        PowerMock.replayAll();
        new GeoMapActivityDelegate(null, null, null, myLocationOverlay, null).onResume();
        PowerMock.verifyAll();
    }

    @Test
    public void testMenuActionToggleSatellite() {
        MapView mapView = PowerMock.createMock(MapView.class);

        EasyMock.expect(mapView.isSatellite()).andReturn(true);
        mapView.setSatellite(false);
        EasyMock.expect(mapView.isSatellite()).andReturn(false);
        mapView.setSatellite(true);

        PowerMock.replayAll();
        final GeoMapActivityDelegate.MenuActionToggleSatellite menuActionToggleSatellite = new GeoMapActivityDelegate.MenuActionToggleSatellite(
                mapView);
        menuActionToggleSatellite.act();
        menuActionToggleSatellite.act();
        PowerMock.verifyAll();
    }

    @Test
    public void testMenuActionCacheList() throws Exception {
        Intent intent = PowerMock.createMock(Intent.class);
        Activity activity = PowerMock.createMock(Activity.class);

        PowerMock.expectNew(Intent.class, activity, CacheList.class).andReturn(intent);
        activity.startActivity(intent);

        PowerMock.replayAll();
        new GeoMapActivityDelegate.MenuActionCacheList(activity).act();
        PowerMock.verifyAll();
    }
}
