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

package com.google.code.geobeagle.activity.main.intents;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.GeocacheFactory.Source;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.cacheloader.CacheLoaderException;
import com.google.code.geobeagle.cacheloader.CacheUrlLoader;
import com.google.inject.Inject;

import android.content.res.Resources;


/*
 * Convert a Geocache to the cache page url.
 */
public class GeocacheToCachePage implements GeocacheToUri {
    private final CacheUrlLoader cacheUrlLoader;
    private final Resources resources;

    @Inject
    public GeocacheToCachePage(CacheUrlLoader cacheUrlLoader, Resources resources) {
        this.cacheUrlLoader = cacheUrlLoader;
        this.resources = resources;
    }

    @Override
    public String convert(Geocache geocache) throws CacheLoaderException {
        if (geocache.getSourceType() == Source.GPX) {
            return cacheUrlLoader.load(geocache.getSourceName(), geocache.getId());
        }
        return String.format(resources.getStringArray(R.array.cache_page_url)[geocache
                .getContentProvider().toInt()], geocache.getShortId());

    }

}
