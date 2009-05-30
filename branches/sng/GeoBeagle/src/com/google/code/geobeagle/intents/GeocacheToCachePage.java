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

package com.google.code.geobeagle.intents;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.ResourceProvider;

/*
 * Convert a Geocache to the cache page url.
 */
public class GeocacheToCachePage implements GeocacheToUri {
    private final ResourceProvider mResourceProvider;

    public GeocacheToCachePage(ResourceProvider resourceProvider) {
        mResourceProvider = resourceProvider;
    }

    // TODO: move strings into Provider enum.
    public String convert(Geocache geocache) {
        return String.format(mResourceProvider.getStringArray(R.array.cache_page_url)[geocache
                .getContentProvider().toInt()], geocache.getShortId());
    }

}
