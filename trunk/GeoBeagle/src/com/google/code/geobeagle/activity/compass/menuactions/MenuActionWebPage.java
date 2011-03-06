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
package com.google.code.geobeagle.activity.compass.menuactions;

import com.google.code.geobeagle.actions.Action;
import com.google.code.geobeagle.activity.compass.IntentStarterViewCachePage;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class MenuActionWebPage implements Action {
    private final IntentStarterViewCachePage intentStarterViewCachePage;

    @Inject
    public MenuActionWebPage(Injector injector) {
        this.intentStarterViewCachePage = injector.getInstance(IntentStarterViewCachePage.class);
    }

    @Override
    public void act() {
        intentStarterViewCachePage.startIntent();
    }
}
