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

package com.google.code.geobeagle.activity.compass;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.actions.MenuActionBase;
import com.google.code.geobeagle.actions.MenuActionCacheList;
import com.google.code.geobeagle.actions.MenuActionEditGeocache;
import com.google.code.geobeagle.actions.MenuActionSettings;
import com.google.code.geobeagle.actions.MenuActions;
import com.google.code.geobeagle.activity.compass.menuactions.MenuActionWebPage;
import com.google.inject.Inject;
import com.google.inject.Injector;

import android.content.res.Resources;

class GeoBeagleActivityMenuActions extends MenuActions {
    @Inject
    public GeoBeagleActivityMenuActions(Injector injector) {
        super(injector.getInstance(Resources.class));
        add(new MenuActionBase(R.string.menu_cache_list,
                injector.getInstance(MenuActionCacheList.class)));
        add(new MenuActionBase(R.string.menu_edit_geocache,
                injector.getInstance(MenuActionEditGeocache.class)));
        add(new MenuActionBase(R.string.menu_settings,
                injector.getInstance(MenuActionSettings.class)));
        add(new MenuActionBase(R.string.web_page, injector.getInstance(MenuActionWebPage.class)));
    }

}
