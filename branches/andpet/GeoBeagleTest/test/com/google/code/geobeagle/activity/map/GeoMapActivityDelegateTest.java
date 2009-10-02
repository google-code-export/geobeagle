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

import static org.junit.Assert.*;

import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.actions.MenuActions;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.view.Menu;
import android.view.MenuItem;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
    GeoMapView.class, MapController.class, GeoMapActivityDelegate.class
})
public class GeoMapActivityDelegateTest {

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
/*
    @Test
    public void testOnCreateOptionsMenu() {
        MenuActions menuActions = PowerMock.createMock(MenuActions.class);
        Menu menu = PowerMock.createMock(Menu.class);

        EasyMock.expect(menuActions.onCreateOptionsMenu(menu)).andReturn(true);

        PowerMock.replayAll();
        assertTrue(new GeoMapActivityDelegate(null, menuActions).onCreateOptionsMenu(menu));
        PowerMock.verifyAll();
    }

    @Test
    public void testOnMenuOpened() {
        Menu menu = PowerMock.createMock(Menu.class);
        MenuItem menuItem = PowerMock.createMock(MenuItem.class);
        GeoMapView mapView = PowerMock.createMock(GeoMapView.class);

        EasyMock.expect(mapView.isSatellite()).andReturn(false);
        EasyMock.expect(menu.findItem(R.string.menu_toggle_satellite)).andReturn(menuItem);
        EasyMock.expect(menuItem.setTitle(R.string.map_view)).andReturn(menuItem);

        EasyMock.expect(mapView.isSatellite()).andReturn(true);
        EasyMock.expect(menu.findItem(R.string.menu_toggle_satellite)).andReturn(menuItem);
        EasyMock.expect(menuItem.setTitle(R.string.satellite_view)).andReturn(menuItem);

        PowerMock.replayAll();
        final GeoMapActivityDelegate geoMapActivityDelegate = new GeoMapActivityDelegate(mapView,
                null);
        geoMapActivityDelegate.onMenuOpened(menu);
        geoMapActivityDelegate.onMenuOpened(menu);
        PowerMock.verifyAll();
    }

    @Test
    public void testOnOptionsSelected() {
        MenuActions menuActions = PowerMock.createMock(MenuActions.class);
        MenuItem menuItem = PowerMock.createMock(MenuItem.class);

        EasyMock.expect(menuItem.getItemId()).andReturn(12);
        EasyMock.expect(menuActions.act(12)).andReturn(true);

        PowerMock.replayAll();
        new GeoMapActivityDelegate(null, menuActions).onOptionsItemSelected(menuItem);
        PowerMock.verifyAll();
    }
    */
}
