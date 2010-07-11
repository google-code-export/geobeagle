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

package com.google.code.geobeagle.activity.cachelist;

import com.google.code.geobeagle.actions.MenuActionMap;
import com.google.code.geobeagle.actions.MenuActionSearchOnline;
import com.google.code.geobeagle.actions.MenuActionSettings;
import com.google.code.geobeagle.actions.MenuActions;
import com.google.code.geobeagle.activity.cachelist.actions.menu.MenuActionDeleteAllCaches;
import com.google.code.geobeagle.activity.cachelist.actions.menu.MenuActionMyLocation;
import com.google.code.geobeagle.activity.cachelist.actions.menu.MenuActionSyncGpx;
import com.google.inject.Inject;

import android.content.res.Resources;

public class CacheListMenuActions extends MenuActions {
    @Inject
    public CacheListMenuActions(MenuActionSyncGpx menuActionSyncGpx,
            MenuActionDeleteAllCaches menuActionDeleteAllCaches,
            MenuActionMyLocation menuActionMyLocation,
            MenuActionSearchOnline menuActionSearchOnline, MenuActionMap menuActionMap,
            MenuActionSettings menuActionSettings, Resources resources) {
        super(resources);
        add(menuActionSyncGpx);
        add(menuActionDeleteAllCaches);
        add(menuActionMyLocation);
        add(menuActionSearchOnline);
        add(menuActionMap);
        add(menuActionSettings);
    }
}
