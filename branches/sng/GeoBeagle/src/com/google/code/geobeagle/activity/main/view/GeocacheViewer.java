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
import com.google.code.geobeagle.activity.main.GeoUtils;
import com.google.code.geobeagle.activity.main.RadarView;

import android.view.View;
import android.widget.TextView;

public class GeocacheViewer {

    public static final String FNAME_RECENT_LOCATIONS = "RECENT_LOCATIONS";
    public static final String PREFS_LOCATION = "Location";
    private final TextView mId;
    private final TextView mName;
    private final RadarView mRadarView;

    public GeocacheViewer(RadarView radarView, TextView gcid, TextView gcname) {
        mRadarView = radarView;
        mId = gcid;
        mName = gcname;
    }

    public void set(Geocache geocache) {
        final double latitude = geocache.getLatitude();
        final double longitude = geocache.getLongitude();
        mRadarView.setTarget((int)(latitude * GeoUtils.MILLION),
                (int)(longitude * GeoUtils.MILLION));
        mId.setText(geocache.getId());
        final CharSequence name = geocache.getName();
        if (name.length() > 0) {
            mName.setText(name);
            mName.setVisibility(View.VISIBLE);
        } else
            mName.setVisibility(View.GONE);
    }
}
