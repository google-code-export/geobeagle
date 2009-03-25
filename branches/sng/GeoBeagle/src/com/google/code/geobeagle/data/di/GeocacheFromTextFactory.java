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

import com.google.code.geobeagle.ResourceProvider;
import com.google.code.geobeagle.Util;
import com.google.code.geobeagle.data.Geocache;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GeocacheFromTextFactory {
    public static CharSequence extractDescription(CharSequence location) {
        return Util.splitCoordsAndDescription(location)[1];
    }

    public static Pattern[] getDestinationPatterns(ResourceProvider resourceProvider) {
        String[] contentPrefixes = new String[] {
                "LB", "GC"
        };
        Pattern mContentSelectors[] = new Pattern[contentPrefixes.length];
        for (int ix = 0; ix < contentPrefixes.length; ix++) {
            mContentSelectors[ix] = Pattern.compile("(?:" + contentPrefixes[ix] + ")(\\w*)");
        }
        return mContentSelectors;
    }

    private final Pattern[] mGeocachePatterns;

    public GeocacheFromTextFactory(ResourceProvider resourceProvider) {
        mGeocachePatterns = GeocacheFromTextFactory.getDestinationPatterns(resourceProvider);
    }

    public Geocache create(CharSequence location) {
        return GeocacheFromTextFactory.create(location, mGeocachePatterns);
    }

    public static Geocache create(CharSequence location, Pattern destinationPatterns[]) {
        int contentSelectorIndex;
        double latitude = 0;
        double longitude = 0;

        CharSequence fullId = "";
        CharSequence name = "";
        CharSequence latLonDescription[] = Util.splitLatLonDescription(location);
        try {
            latitude = Util.parseCoordinate(latLonDescription[0]);
            longitude = Util.parseCoordinate(latLonDescription[1]);
        } catch (NumberFormatException numberFormatException) {
            // TODO: Looks like this case is unreachable; remove this after
            // the
            // destination input method has been reworked.
        }

        CharSequence description = latLonDescription[2];

        for (contentSelectorIndex = destinationPatterns.length - 1; contentSelectorIndex >= 0; contentSelectorIndex--) {
            Matcher matcher = destinationPatterns[contentSelectorIndex].matcher(description);
            if (matcher.find()) {
                fullId = matcher.group();
                break;
            }
        }

        if (fullId.length() == 0) {
            name = description;
        } else {
            if (description.length() > fullId.length() + 2)
                name = description.subSequence(fullId.length() + 2, description.length());
        }
        return new Geocache(contentSelectorIndex, fullId, name, latitude, longitude);
    }
}
