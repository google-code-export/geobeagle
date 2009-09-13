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

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.main.GeoBeagle;
import com.google.code.geobeagle.activity.main.view.EditCacheActivity;

import android.content.Intent;

public class MenuActionEditGeocache implements MenuAction {
    private final GeoBeagle mParent;

    public MenuActionEditGeocache(GeoBeagle parent) {
        mParent = parent;
    }

    @Override
    public void act() {
        final Intent intent = new Intent(mParent, EditCacheActivity.class);
        intent.putExtra("geocache", mParent.getGeocache());
        mParent.startActivityForResult(intent, 0);
    }

    @Override
    public int getId() {
        return R.string.menu_edit_geocache;
    }
}