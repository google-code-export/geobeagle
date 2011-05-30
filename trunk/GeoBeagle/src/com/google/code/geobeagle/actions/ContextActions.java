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
import com.google.code.geobeagle.activity.cachelist.actions.context.ContextActionDelete;
import com.google.code.geobeagle.activity.cachelist.actions.context.ContextActionEdit;
import com.google.code.geobeagle.activity.cachelist.actions.context.ContextActionView;
import com.google.inject.Inject;
import com.google.inject.Injector;

import java.util.ArrayList;

public class ContextActions {
    private final ArrayList<ContextAction> contextActions = new ArrayList<ContextAction>();

    @Inject
    public ContextActions(Injector injector) {
        contextActions.add(injector.getInstance(ContextActionDelete.class));
        contextActions.add(injector.getInstance(ContextActionView.class));
        contextActions.add(injector.getInstance(ContextActionEdit.class));
    }

    public void act(int menuIndex, int position) {
        contextActions.get(menuIndex).act(position);
    }
}
