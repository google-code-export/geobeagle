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

package com.google.code.geobeagle.cacheloader;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.cachedetails.FilePathStrategy;
import com.google.inject.Inject;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

class CacheReaderFromFile {
    private final FilePathStrategy filePathStrategy;

    @Inject
    CacheReaderFromFile(FilePathStrategy filePathStrategy) {
        this.filePathStrategy = filePathStrategy;
    }

    Reader getReader(CharSequence sourceName, CharSequence cacheId) throws CacheLoaderException {
        String path = filePathStrategy.getPath(sourceName, cacheId.toString(), "gpx");
        File file = new File(path);
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            throw new CacheLoaderException(R.string.error_cant_read_sdroot, state);
        }
        String absolutePath = file.getAbsolutePath();
        try {
            return new BufferedReader(new FileReader(absolutePath));
        } catch (FileNotFoundException e) {
            throw new CacheLoaderException(R.string.error_opening_details_file, e.getMessage());
        }
    }
}