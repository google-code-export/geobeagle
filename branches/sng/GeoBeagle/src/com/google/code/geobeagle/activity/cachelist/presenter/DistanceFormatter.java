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

package com.google.code.geobeagle.activity.cachelist.presenter;

public class DistanceFormatter {
    private static final String[] ARROWS = {
            "^", ">", "v", "<",
    };

    public CharSequence formatDistance(float distance) {
        if (distance == -1) {
            return "";
        }
        if (distance >= 1000) {
            return String.format("%1$1.2fkm", distance / 1000.0);
        }
        return String.format("%1$dm", (int)distance);
    }

    public String formatBearing(float currentBearing) {
        return ARROWS[((((int)(currentBearing) + 45 + 720) % 360) / 90)];
    }
}
