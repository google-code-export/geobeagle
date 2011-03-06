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

package com.google.code.geobeagle.activity.compass.view;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.activity.cachelist.GeocacheListController;
import com.google.code.geobeagle.database.LocationSaver;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

public class SetButtonOnClickListener implements OnClickListener {
    private final Activity activity;
    private final EditCache editCache;
    private final LocationSaver locationSaver;

    public SetButtonOnClickListener(Activity activity, EditCache editCache,
            LocationSaver locationSaver) {
        this.activity = activity;
        this.editCache = editCache;
        this.locationSaver = locationSaver;
    }

    @Override
    public void onClick(View v) {
        final Geocache geocache = editCache.get();
        locationSaver.saveLocation(geocache);
        final Intent i = new Intent();
        i.setAction(GeocacheListController.SELECT_CACHE);
        i.putExtra("geocache", geocache);
        activity.setResult(Activity.RESULT_OK, i);
        activity.finish();
    }
}
