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

package com.google.code.geobeagle.data.di;

import com.google.code.geobeagle.data.Geocache;


public class GeocacheFactory {
    public Geocache create(int contentSelectorIndex, CharSequence id, CharSequence name,
            double latitude, double longitude) {
        return new Geocache(contentSelectorIndex, id, name, latitude, longitude);
    }
}
