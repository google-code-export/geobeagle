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

import com.google.code.geobeagle.activity.cachelist.actions.context.ContextAction;

import java.util.ArrayList;

public class ContextActions {
    private final ArrayList<ContextAction> contextActions = new ArrayList<ContextAction>();

    public ContextActions(ContextAction[] contextActions) {
        for (int ix = 0; ix < contextActions.length; ix++) {
            this.contextActions.add(contextActions[ix]);
        }
    }

    public void act(int menuIndex, int position) {
        contextActions.get(menuIndex).act(position);
    }
}
