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
import com.google.code.geobeagle.cachedetails.FilePathStrategy;
import com.google.code.geobeagle.xmlimport.EventHandlerGpx;
import com.google.code.geobeagle.xmlimport.EventHelper;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;

public class CacheLoader {

    private final EventHandlerGpx eventHandlerGpx;
    private final FilePathStrategy filePathStrategy;
    private final FileDataVersionChecker fileDataVersionChecker;
    private final EventHelper eventHelper;
    private final DetailsDatabaseReader detailsDatabaseReader;
    private final DetailsReader detailsReader;

    CacheLoader(FilePathStrategy filePathStrategy,
            EventHandlerGpx eventHandlerGpx,
            FileDataVersionChecker fileDataVersionChecker,
            EventHelper eventHelper,
            DetailsDatabaseReader detailsDatabaseReader,
            DetailsReader detailsReader) {
        this.filePathStrategy = filePathStrategy;
        this.eventHandlerGpx = eventHandlerGpx;
        this.fileDataVersionChecker = fileDataVersionChecker;
        this.eventHelper = eventHelper;
        this.detailsDatabaseReader = detailsDatabaseReader;
        this.detailsReader = detailsReader;
    }

    public String load(CharSequence sourceName, CharSequence cacheId) throws CacheLoaderException {
        String path = filePathStrategy.getPath(sourceName, cacheId.toString(), "gpx");
        File file = new File(path);
        return open(file, cacheId, eventHandlerGpx).read();
    }

    private DetailsReader open(File file, CharSequence cacheId, EventHandlerGpx eventHandlerGpx)
            throws CacheLoaderException {
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            throw new CacheLoaderException(R.string.error_cant_read_sdroot, state);
        }
        eventHelper.setEventHandler(eventHandlerGpx);
        String absolutePath = file.getAbsolutePath();
        String detailsFromDatabase = detailsDatabaseReader.read(cacheId);
        Reader reader = createReader(absolutePath, detailsFromDatabase);
        detailsReader.open(absolutePath, eventHelper, reader);
        return detailsReader;
    }

    private Reader createReader(String absolutePath, String detailsFromDatabase)
            throws CacheLoaderException {
        try {
            if (detailsFromDatabase == null)
                return new BufferedReader(new FileReader(absolutePath));

            return new StringReader(detailsFromDatabase);
        } catch (FileNotFoundException e) {
            int error = fileDataVersionChecker.needsUpdating() ? R.string.error_details_file_version
                    : R.string.error_opening_details_file;
            throw new CacheLoaderException(error, e.getMessage());
        }
    }
}
