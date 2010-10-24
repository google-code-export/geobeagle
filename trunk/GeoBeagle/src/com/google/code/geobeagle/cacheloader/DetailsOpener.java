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
import com.google.code.geobeagle.cachedetails.DetailsDatabaseReader;
import com.google.code.geobeagle.cachedetails.FileDataVersionChecker;
import com.google.code.geobeagle.cachedetails.StringWriterWrapper;
import com.google.code.geobeagle.cachedetails.reader.DetailsReader;
import com.google.code.geobeagle.xmlimport.EventHandlerGpx;
import com.google.code.geobeagle.xmlimport.EventHelper;
import com.google.inject.Inject;

import android.app.Activity;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;

class DetailsOpener {
    private final Activity activity;
    private final EventHandlerGpx eventHandlerGpx;
    private final EventHelper eventHelper;
    private final FileDataVersionChecker fileDataVersionChecker;
    private final StringWriterWrapper stringWriterWrapper;
    private final DetailsDatabaseReader detailsDatabaseReader;

    @Inject
    DetailsOpener(Activity activity,
            FileDataVersionChecker fileDataVersionChecker,
            EventHelper eventHelper,
            EventHandlerGpx eventHandlerGpx,
            StringWriterWrapper stringWriterWrapper,
            DetailsDatabaseReader detailsDatabaseReader) {
        this.activity = activity;
        this.fileDataVersionChecker = fileDataVersionChecker;
        this.eventHelper = eventHelper;
        this.eventHandlerGpx = eventHandlerGpx;
        this.stringWriterWrapper = stringWriterWrapper;
        this.detailsDatabaseReader = detailsDatabaseReader;
    }

    public DetailsReader open(File file, CharSequence cacheId) throws CacheLoaderException {
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            throw new CacheLoaderException(R.string.error_cant_read_sdroot, state);
        }
        final Reader fileReader;
        final Reader dbReader;
        String absolutePath = file.getAbsolutePath();
        dbReader = new StringReader(detailsDatabaseReader.read(cacheId));
        try {
            fileReader = new BufferedReader(new FileReader(absolutePath));
        } catch (FileNotFoundException e) {
            int error = fileDataVersionChecker.needsUpdating() ? R.string.error_details_file_version
                    : R.string.error_opening_details_file;
            throw new CacheLoaderException(error, e.getMessage());
        }
        return new DetailsReader(activity, dbReader, absolutePath, eventHelper,
 eventHandlerGpx,
                stringWriterWrapper);
    }
}
