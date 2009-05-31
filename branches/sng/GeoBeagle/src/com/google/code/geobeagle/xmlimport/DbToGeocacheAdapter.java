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

package com.google.code.geobeagle.xmlimport;

import com.google.code.geobeagle.mainactivity.GeocacheFactory.Source;

public class DbToGeocacheAdapter {
    public Source sourceNameToSourceType(String sourceName) {
        if (sourceName.equals("intent"))
            return Source.WEB_URL;
        else if (sourceName.equals("mylocation"))
            return Source.MY_LOCATION;
        else if (sourceName.toLowerCase().endsWith((".loc")))
            return Source.LOC;
        return Source.GPX;
    }

    public String sourceTypeToSourceName(Source source, String sourceName) {
        if (source == Source.MY_LOCATION)
            return "mylocation";
        else if (source == Source.WEB_URL)
            return "intent";
        return sourceName;
    }
}
