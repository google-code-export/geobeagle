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

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.cachelist.GeocacheListController;

import android.content.Context;
import android.content.Intent;

public class CacheActionView implements CacheAction {
    private final Context mContext;
    private final Intent mGeoBeagleMainIntent;

    public CacheActionView(Context context, Intent intent) {
        mContext = context;
        mGeoBeagleMainIntent = intent;
    }

    @Override
    public void act(Geocache cache) {
        mGeoBeagleMainIntent.putExtra("geocache", cache)
          .setAction(GeocacheListController.SELECT_CACHE);
        mContext.startActivity(mGeoBeagleMainIntent);
    }

    @Override
    public int getId() {
        return R.string.menu_view_geocache;
    }
}
