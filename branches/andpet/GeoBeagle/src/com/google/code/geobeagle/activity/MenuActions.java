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

package com.google.code.geobeagle.activity;


import java.util.HashMap;

public class MenuActions {
    private final HashMap<Integer, MenuAction> mMenuActions;

    public MenuActions(MenuAction[] menuActions, int[] menuIds) {
        mMenuActions = new HashMap<Integer, MenuAction>(menuIds.length);
        for (int ix = 0; ix < menuIds.length; ix++) {
            mMenuActions.put(menuIds[ix], menuActions[ix]);
        }
    }

    public boolean act(int itemId) {
        final MenuAction menuAction = mMenuActions.get(itemId);
        if (menuAction == null)
            return false;
        menuAction.act();
        return true;
    }
}
