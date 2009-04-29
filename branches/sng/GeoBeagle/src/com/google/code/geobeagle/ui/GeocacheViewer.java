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

import com.google.code.geobeagle.Util;
import com.google.code.geobeagle.data.Geocache;

import android.widget.TextView;

public class GeocacheViewer {

    public static final String FNAME_RECENT_LOCATIONS = "RECENT_LOCATIONS";
    public static final String PREFS_LOCATION = "Location";
    private final TextView mCoords;
    private final TextView mId;
    private final TextView mName;

    public GeocacheViewer(TextView gcid, TextView gcname, TextView gccoords) {
        mId = gcid;
        mName = gcname;
        mCoords = gccoords;
    }

    public void set(Geocache geocache) {
        mId.setText(geocache.getId());
        mName.setText(geocache.getName());
        mCoords.setText(Util.formatDegreesAsDecimalDegreesString(geocache.getLatitude()) + ", "
                + Util.formatDegreesAsDecimalDegreesString(geocache.getLongitude()));
    }
}
