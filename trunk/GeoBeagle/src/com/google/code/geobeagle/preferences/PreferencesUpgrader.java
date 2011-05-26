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

package com.google.code.geobeagle.preferences;

import com.google.code.geobeagle.activity.preferences.Preferences;
import com.google.code.geobeagle.xmlimport.GeoBeagleEnvironment;
import com.google.inject.Inject;

public class PreferencesUpgrader {

    private final DependencyUpgrader dependencyUpgrader;

    @Inject
    PreferencesUpgrader(DependencyUpgrader dependencyUpgrader) {
        this.dependencyUpgrader = dependencyUpgrader;
    }

    public void upgrade(int oldVersion) {
        if (oldVersion <= 14) {
            dependencyUpgrader.upgrade(Preferences.BCACHING_ENABLED, Preferences.BCACHING_USERNAME);
            dependencyUpgrader.upgrade(Preferences.SDCARD_ENABLED,
                    GeoBeagleEnvironment.IMPORT_FOLDER);
        }
    }
}
