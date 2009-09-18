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

import com.google.android.maps.MapView;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.MenuAction;
import com.google.code.geobeagle.activity.MenuActions;
import com.google.code.geobeagle.activity.cachelist.CacheList;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

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
public class GeoMapActivityDelegate {

    static class MenuActionCacheList implements MenuAction {
        private final Activity mActivity;

        MenuActionCacheList(Activity activity) {
            mActivity = activity;
        }

        @Override
        public void act() {
            mActivity.startActivity(new Intent(mActivity, CacheList.class));
        }
    }

    public static class MenuActionToggleSatellite implements MenuAction {
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

    public GeoMapActivityDelegate(GeoMapView mapView, MenuActions menuActions) {
        mMapView = mapView;
        mMenuActions = menuActions;
    }

    public boolean onMenuOpened(Menu menu) {
        menu.findItem(R.id.menu_toggle_satellite).setTitle(
                mMapView.isSatellite() ? R.string.map_view : R.string.satellite_view);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return mMenuActions.act(item.getItemId());
    }
}
