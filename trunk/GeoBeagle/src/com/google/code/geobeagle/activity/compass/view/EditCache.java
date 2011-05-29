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
import com.google.code.geobeagle.activity.compass.Util;

import android.widget.EditText;

public class EditCache {
    private final GeocacheFactory geocacheFactory;
    private final EditText id;
    private final EditText latitude;
    private final EditText longitude;
    private final EditText name;
    private Geocache mOriginalGeocache;

    public EditCache(GeocacheFactory geocacheFactory, EditText id, EditText name,
            EditText latitude, EditText longitude) {
        this.geocacheFactory = geocacheFactory;
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    Geocache get() {
        return geocacheFactory.create(id.getText(), name.getText(),
                Util.parseCoordinate(latitude.getText()),
                Util.parseCoordinate(longitude.getText()), mOriginalGeocache.getSourceType(),
                mOriginalGeocache.getSourceName(), mOriginalGeocache.getCacheType(),
                mOriginalGeocache.getDifficulty(), mOriginalGeocache.getTerrain(),
                mOriginalGeocache.getContainer(), mOriginalGeocache.getAvailable(),
                mOriginalGeocache.getArchived());
    }

    void set(Geocache geocache) {
        mOriginalGeocache = geocache;
        id.setText(geocache.getId());
        name.setText(geocache.getName());
        latitude.setText(Util.formatDegreesAsDecimalDegreesString(geocache.getLatitude()));
        longitude.setText(Util.formatDegreesAsDecimalDegreesString(geocache.getLongitude()));

        latitude.requestFocus();
    }
}
