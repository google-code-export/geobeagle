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
import com.google.code.geobeagle.xmlimport.CacheTagHandler;
import com.google.code.geobeagle.xmlimport.EventHandlerGpx;
import com.google.code.geobeagle.xmlimport.EventHelper;
import com.google.inject.Inject;
import com.google.inject.Provider;

import org.xmlpull.v1.XmlPullParser;

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
    private final EventHelper eventHelper;
    private final FileDataVersionChecker fileDataVersionChecker;
    private final StringWriterWrapper stringWriterWrapper;
    private final DetailsDatabaseReader detailsDatabaseReader;
    private final Provider<XmlPullParser> xmlPullParserProvider;

    @Inject
    DetailsOpener(Activity activity,
            FileDataVersionChecker fileDataVersionChecker,
            EventHelper eventHelper,
            StringWriterWrapper stringWriterWrapper,
            DetailsDatabaseReader detailsDatabaseReader,
            Provider<XmlPullParser> xmlPullParserProvider) {
        this.activity = activity;
        this.fileDataVersionChecker = fileDataVersionChecker;
        this.eventHelper = eventHelper;
        this.stringWriterWrapper = stringWriterWrapper;
        this.detailsDatabaseReader = detailsDatabaseReader;
        this.xmlPullParserProvider = xmlPullParserProvider;
    }

    public DetailsReader open(File file, CharSequence cacheId, CacheTagHandler cacheTagHandler)
            throws CacheLoaderException {
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            throw new CacheLoaderException(R.string.error_cant_read_sdroot, state);
        }
        EventHandlerGpx eventHandlerGpx = new EventHandlerGpx(cacheTagHandler);
        eventHelper.setEventHandler(eventHandlerGpx);
        String absolutePath = file.getAbsolutePath();
        String detailsFromDatabase = detailsDatabaseReader.read(cacheId);
        Reader reader = createReader(absolutePath, detailsFromDatabase);
        return new DetailsReader(activity, reader, absolutePath, eventHelper, stringWriterWrapper,
                xmlPullParserProvider);
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
