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
import com.google.code.geobeagle.GeocacheFactory;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.database.LocationSaver;
import com.google.inject.Inject;

import android.app.Activity;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;

public class EditCacheActivityDelegate {
    private final CancelButtonOnClickListener cancelButtonOnClickListener;
    private final GeocacheFactory geocacheFactory;
    private final LocationSaver locationSaver;
    private final Activity parent;

    @Inject
    public EditCacheActivityDelegate(Activity parent,
            CancelButtonOnClickListener cancelButtonOnClickListener,
            GeocacheFactory geocacheFactory,
            LocationSaver locationSaver) {
        this.parent = parent;
        this.cancelButtonOnClickListener = cancelButtonOnClickListener;
        this.geocacheFactory = geocacheFactory;
        this.locationSaver = locationSaver;
    }

    public void onCreate() {
        parent.setContentView(R.layout.cache_edit);
    }

    public void onPause() {
    }

    public void onResume() {
        final Intent intent = parent.getIntent();
        final Geocache geocache = intent.<Geocache> getParcelableExtra("geocache");
        final EditCache editCache = new EditCache(geocacheFactory, (EditText)parent
                .findViewById(R.id.edit_id), (EditText)parent.findViewById(R.id.edit_name),
                (EditText)parent.findViewById(R.id.edit_latitude), (EditText)parent
                        .findViewById(R.id.edit_longitude));

        editCache.set(geocache);
        final SetButtonOnClickListener setButtonOnClickListener = new SetButtonOnClickListener(
                parent, editCache, locationSaver);

        ((Button)parent.findViewById(R.id.edit_set)).setOnClickListener(setButtonOnClickListener);
        ((Button)parent.findViewById(R.id.edit_cancel))
                .setOnClickListener(cancelButtonOnClickListener);
    }
}
