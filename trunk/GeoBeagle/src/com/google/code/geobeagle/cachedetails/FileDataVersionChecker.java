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

import java.io.File;

public class FileDataVersionChecker {
    static final String VERSION_DIR = CacheDetailsLoader.DETAILS_DIR;
    static final String VERSION_PATH = VERSION_DIR + "/VERSION";

    public boolean needsUpdating() {
        return !new File(VERSION_PATH).exists();
    }
}
