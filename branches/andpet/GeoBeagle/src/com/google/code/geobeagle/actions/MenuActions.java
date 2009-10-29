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

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

public class MenuActions {
    private ArrayList<MenuAction> mMenuActions = new ArrayList<MenuAction>();

    public MenuActions() {
    }
    
    public MenuActions(MenuAction[] menuActions) {
        for (int ix = 0; ix < menuActions.length; ix++) {
            add(menuActions[ix]);
        }
    }

    public boolean act(int itemId) {
        mMenuActions.get(itemId).act();
        return true;
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
            menu.add(0, ix, ix, action.getLabel());
            ix++;
        }
        return true;
    }
 
    /** Give the menu items a chance to update the text */
    public boolean onMenuOpened(Menu menu) {
        int ix = 0;
        for (MenuAction action : mMenuActions) {
            MenuItem item = menu.getItem(ix);
            String label = action.getLabel();
            if (!item.getTitle().equals(label))
                item.setTitle(label);
            ix++;
        }
        return true;
    }
}
