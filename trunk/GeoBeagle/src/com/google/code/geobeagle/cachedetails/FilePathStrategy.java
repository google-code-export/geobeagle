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

package com.google.code.geobeagle.cachedetails;

import com.google.code.geobeagle.xmlimport.GeoBeagleEnvironment;
import com.google.inject.Inject;

public class FilePathStrategy {

    private GeoBeagleEnvironment geoBeagleEnvironment;

    @Inject
    public
    FilePathStrategy(GeoBeagleEnvironment geoBeagleEnvironment) {
        this.geoBeagleEnvironment = geoBeagleEnvironment;
    }

    private static String replaceIllegalFileChars(String wpt) {
        return wpt.replaceAll("[<\\\\/:\\*\\?\">| \\t]", "_");
    }

    public String getPath(CharSequence gpxName, String wpt, String extension) {
        String string = geoBeagleEnvironment.getDetailsDirectory() + gpxName + "/"
                + String.valueOf(Math.abs(wpt.hashCode()) % 16) + "/"
                + replaceIllegalFileChars(wpt) + "." + extension;
        return string;
    }
}
