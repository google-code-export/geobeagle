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

package com.google.code.geobeagle.ui;

import com.google.code.geobeagle.GeoBeagle;
import com.google.code.geobeagle.ui.WebPageAndDetailsButtonEnabler;

import android.view.View;

public class Misc {
    public static class Time {
        public long getCurrentTime() {
            return System.currentTimeMillis();
        }
    }

    public static WebPageAndDetailsButtonEnabler create(GeoBeagle geoBeagle, View cachePageButton,
            View detailsButton) {
        return new WebPageAndDetailsButtonEnabler(geoBeagle, cachePageButton, detailsButton);
    }
}
