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

import com.google.code.geobeagle.database.CacheWriter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileAlreadyLoadedChecker {

    private final CacheWriter mCacheWriter;

    FileAlreadyLoadedChecker(CacheWriter cacheWriter) {
        mCacheWriter = cacheWriter;
    }

    boolean isAlreadyLoaded(String source) {
        int len = source.length();
        String extension = source.substring(Math.max(0, len - 4), len).toLowerCase();

        if (!extension.equalsIgnoreCase(".loc"))
            return false;

        String sqlDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(new File(
                source).lastModified()));
        if (mCacheWriter.isGpxAlreadyLoaded(new File(source).getName(), sqlDate)) {
            return true;
        }
        return false;
    }

}
