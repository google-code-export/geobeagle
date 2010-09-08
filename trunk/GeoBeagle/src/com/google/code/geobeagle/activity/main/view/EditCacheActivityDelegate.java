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

package com.google.code.geobeagle.activity.main.view;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.GeocacheFactory;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.cachelist.GeocacheListController;
import com.google.code.geobeagle.activity.main.Util;
import com.google.code.geobeagle.database.LocationSaver;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class EditCacheActivityDelegate {
    public static class CancelButtonOnClickListener implements OnClickListener {
        private final Activity activity;

        public CancelButtonOnClickListener(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void onClick(View v) {
            // TODO: replace magic number.
            activity.setResult(-1, null);
            activity.finish();
        }
    }

    public static class EditCache {
        private final GeocacheFactory geocacheFactory;
        private final EditText id;
        private final EditText latitude;
        private final EditText longitude;
        private final EditText name;
        private Geocache originalGeocache;

        public EditCache(GeocacheFactory geocacheFactory, EditText id, EditText name,
                EditText latitude, EditText longitude) {
            this.geocacheFactory = geocacheFactory;
            this.id = id;
            this.name = name;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        Geocache get() {
            return geocacheFactory.create(id.getText(), name.getText(), Util
                    .parseCoordinate(latitude.getText()), Util.parseCoordinate(longitude
                    .getText()), originalGeocache.getSourceType(), originalGeocache
                    .getSourceName(), originalGeocache.getCacheType(), originalGeocache
                    .getDifficulty(), originalGeocache.getTerrain(), originalGeocache
                    .getContainer(), originalGeocache.getAvailable(), originalGeocache
                    .getArchived());
        }

        void set(Geocache geocache) {
            originalGeocache = geocache;
            id.setText(geocache.getId());
            name.setText(geocache.getName());
            latitude.setText(Util.formatDegreesAsDecimalDegreesString(geocache.getLatitude()));
            longitude.setText(Util.formatDegreesAsDecimalDegreesString(geocache.getLongitude()));

            latitude.requestFocus();
        }
    }

    public static class SetButtonOnClickListener implements OnClickListener {
        private final Activity activity;
        private final EditCache geocacheView;
        private final LocationSaver locationSaver;

        public SetButtonOnClickListener(Activity activity, EditCache editCache,
                LocationSaver locationSaver) {
            this.activity = activity;
            this.geocacheView = editCache;
            this.locationSaver = locationSaver;
        }

        @Override
        public void onClick(View v) {
            final Geocache geocache = geocacheView.get();
            locationSaver.saveLocation(geocache);
            final Intent i = new Intent();
            i.setAction(GeocacheListController.SELECT_CACHE);
            i.putExtra("geocache", geocache);
            activity.setResult(0, i);
            activity.finish();
        }
    }

    private final CancelButtonOnClickListener cancelButtonOnClickListener;
    private final GeocacheFactory geocacheFactory;
    private final Activity parent;
    private final LocationSaver locationSaver;

    public EditCacheActivityDelegate(Activity parent,
            CancelButtonOnClickListener cancelButtonOnClickListener,
            GeocacheFactory geocacheFactory, LocationSaver locationSaver) {
        this.parent = parent;
        this.cancelButtonOnClickListener = cancelButtonOnClickListener;
        this.geocacheFactory = geocacheFactory;
        this.locationSaver = locationSaver;
    }

    public void onCreate() {
        parent.setContentView(R.layout.cache_edit);
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
