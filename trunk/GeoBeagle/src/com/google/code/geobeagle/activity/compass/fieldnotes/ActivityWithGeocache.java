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

package com.google.code.geobeagle.activity.compass.fieldnotes;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.activity.compass.CompassActivity;

import android.app.Activity;

public class ActivityWithGeocache implements HasGeocache {

    @Override
    public Geocache get(Activity activity) {
        return ((CompassActivity)activity).getGeocache();
    }
}
