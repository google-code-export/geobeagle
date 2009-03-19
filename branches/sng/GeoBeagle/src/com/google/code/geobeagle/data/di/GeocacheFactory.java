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

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.ResourceProvider;
import com.google.code.geobeagle.Util;
import com.google.code.geobeagle.data.Geocache;

import java.util.regex.Pattern;

public class GeocacheFactory {
    public static CharSequence extractDescription(CharSequence location) {
        return Util.splitCoordsAndDescription(location)[1];
    }

    public static Pattern[] getDestinationPatterns(ResourceProvider resourceProvider) {
        String[] contentPrefixes = resourceProvider.getStringArray(R.array.content_prefixes);
        Pattern mContentSelectors[] = new Pattern[contentPrefixes.length];
        for (int ix = 0; ix < contentPrefixes.length; ix++) {
            mContentSelectors[ix] = Pattern.compile("(?:" + contentPrefixes[ix] + ")(\\w*)");
        }
        return mContentSelectors;
    }

    private final Pattern[] mGeocachePatterns;

    public GeocacheFactory(ResourceProvider resourceProvider) {
        mGeocachePatterns = GeocacheFactory.getDestinationPatterns(resourceProvider);
    }

    public Geocache create(CharSequence location) {
        return Geocache.create(location, mGeocachePatterns);
    }
}
