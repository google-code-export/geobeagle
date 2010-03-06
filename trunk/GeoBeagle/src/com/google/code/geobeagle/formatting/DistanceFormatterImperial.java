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

package com.google.code.geobeagle.formatting;

public class DistanceFormatterImperial implements DistanceFormatter {

    public CharSequence formatDistance(float distance) {
        if (distance == -1) {
            return "";
        }
        final float miles = distance / 1609.344f;
        if (miles > 0.05)
            return String.format("%1$1.2fmi", miles);
        final int feet = (int)(miles * 5280.0f);
        return String.format("%1$1dft", feet);
    }
}
