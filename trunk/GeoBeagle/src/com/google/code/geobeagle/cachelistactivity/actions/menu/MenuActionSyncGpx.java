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

package com.google.code.geobeagle.cachelistactivity.actions.menu;

import com.google.code.geobeagle.cachelistactivity.presenter.CacheListRefresh;
import com.google.code.geobeagle.xmlimport.GpxImporter;

public class MenuActionSyncGpx implements MenuAction {
    private final GpxImporter mGpxImporter;
    private final CacheListRefresh mMenuActionRefresh;

    public MenuActionSyncGpx(GpxImporter gpxImporter, CacheListRefresh cacheListRefresh) {
        mGpxImporter = gpxImporter;
        mMenuActionRefresh = cacheListRefresh;
    }

    public void act() {
        mGpxImporter.importGpxs(mMenuActionRefresh);
    }
}
