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

package com.google.code.geobeagle.activity.main;

import com.google.code.geobeagle.actions.MenuAction;
import com.google.code.geobeagle.actions.MenuActionCacheList;
import com.google.code.geobeagle.actions.MenuActionEditGeocache;
import com.google.code.geobeagle.actions.MenuActionSettings;
import com.google.code.geobeagle.actions.MenuActions;
import com.google.code.geobeagle.activity.main.menuactions.MenuActionWebPage;
import com.google.inject.Inject;

import android.app.Activity;
import android.content.res.Resources;

class GeoBeagleActivityMenuActions extends MenuActions {
    @Inject
    public GeoBeagleActivityMenuActions(Resources resources, Activity geoBeagle,
            MenuActionWebPage menuActionWebPage) {
        super(resources);
        final MenuAction[] menuActionArray = {
                new MenuActionCacheList(geoBeagle), new MenuActionEditGeocache(geoBeagle),
                new MenuActionSettings(geoBeagle), menuActionWebPage
        };
        for (int ix = 0; ix < menuActionArray.length; ix++) {
            add(menuActionArray[ix]);
        }
    }

}
