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

package com.google.code.geobeagle.bcaching;

import com.google.inject.Inject;

import android.text.format.Time;
import android.util.Log;

class TimeRecorder {
    private final BCachingLastUpdated bcachingLastUpdated;

    @Inject
    TimeRecorder(BCachingLastUpdated bcachingLastUpdated) {
        this.bcachingLastUpdated = bcachingLastUpdated;
    }

    public void saveTime(String lastModified) {
        Time time = new Time();
        time.parse3339(lastModified + ".000Z");
        Log.d("GeoBeagle", "LAST MODIFIED TIME: " + time.format3339(false) + ", " + lastModified
                + ", " + time.toMillis(false));
        bcachingLastUpdated.putLastUpdateTime(time.toMillis(false) + 1000);
    }
}
