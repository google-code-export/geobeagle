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

package com.google.code.geobeagle.actions;


import android.content.res.Resources;
import android.util.Log;
import android.view.Menu;

import java.util.ArrayList;

public class MenuActions {
    private ArrayList<MenuAction> mMenuActions = new ArrayList<MenuAction>();
    private Resources mResources;

    public MenuActions(Resources resources) {
        mResources = resources;
    }
    
    public MenuActions(Resources resources, MenuAction[] menuActions) {
        mResources = resources;
        for (int ix = 0; ix < menuActions.length; ix++) {
            add(menuActions[ix]);
        }
    }

    public boolean act(int itemId) {
        for (MenuAction action : mMenuActions) {
            if (action.getId() == itemId) {
                action.act();
                return true;
            }
        }
        
        return false;
    }
    
    public void add(MenuAction action) {
        mMenuActions.add(action);
    }

    /** Creates an Options Menu from the items in this MenuActions */
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mMenuActions.isEmpty()) {
            Log.w("GeoBeagle", "MenuActions.onCreateOptionsMenu: menu is empty, will not be shown");
            return false;
        }

        menu.clear();
        int ix = 0;
        for (MenuAction action : mMenuActions) {
            final int id = action.getId();
            menu.add(0, id, ix, mResources.getString(id));
            ix++;
        }
        return true;
    }
}
