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

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.code.geobeagle.CacheListActivityStarter;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.actions.Action;
import com.google.code.geobeagle.actions.MenuActionBase;
import com.google.code.geobeagle.actions.MenuActionCacheList;
import com.google.code.geobeagle.actions.MenuActions;
import com.google.inject.Inject;

import android.app.Activity;
import android.content.res.Resources;
import android.view.Menu;
import android.view.MenuItem;

public class GeoMapActivityDelegate {
    public static class MenuActionCenterLocation implements Action {
        private final MyLocationOverlay mMyLocationOverlay;
        private final MapController mMapController;

        public MenuActionCenterLocation(MapController mapController,
                MyLocationOverlay myLocationOverlay) {
            mMyLocationOverlay = myLocationOverlay;
            mMapController = mapController;
        }

        @Override
        public void act() {
            final GeoPoint myLocation = mMyLocationOverlay.getMyLocation();
            if (myLocation == null)
                return;
            mMapController.animateTo(myLocation);
        }
    }

    public static class MenuActionToggleSatellite implements Action {
        private final MapView mMapView;

        public MenuActionToggleSatellite(MapView mapView) {
            mMapView = mapView;
        }

        @Override
        public void act() {
            mMapView.setSatellite(!mMapView.isSatellite());
        }
    }

    private final GeoMapView mMapView;
    private final MenuActions mMenuActions;

    @Inject
    public GeoMapActivityDelegate(Resources resources,
            Activity activity,
            CacheListActivityStarter cacheListActivityStarter) {
        final MenuActions menuActions = new MenuActions(resources);
        final GeoMapView geoMapView = (GeoMapView)activity.findViewById(R.id.mapview);
        menuActions.add(new MenuActionBase(R.string.menu_toggle_satellite,
                new MenuActionToggleSatellite(geoMapView)));
        menuActions.add(new MenuActionBase(R.string.menu_cache_list, new MenuActionCacheList(
                cacheListActivityStarter)));
        final MyLocationOverlay fixedMyLocationOverlay = ((GeoMapActivity)activity)
                .getMyLocationOverlay();
        menuActions.add(new MenuActionBase(R.string.menu_center_location,
                new MenuActionCenterLocation(geoMapView.getController(), fixedMyLocationOverlay)));
        mMapView = (GeoMapView)activity.findViewById(R.id.mapview);
        mMenuActions = menuActions;
    }

    // For testing.
    public GeoMapActivityDelegate(GeoMapView geoMapView, MenuActions menuActions) {
        this.mMapView = geoMapView;
        this.mMenuActions = menuActions;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return mMenuActions.onCreateOptionsMenu(menu);
    }

    public boolean onMenuOpened(Menu menu) {
        menu.findItem(R.string.menu_toggle_satellite).setTitle(
                mMapView.isSatellite() ? R.string.map_view : R.string.satellite_view);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return mMenuActions.act(item.getItemId());
    }

}
