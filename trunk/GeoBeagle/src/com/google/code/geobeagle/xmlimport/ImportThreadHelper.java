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

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.bcaching.BCachingModule;
import com.google.code.geobeagle.xmlimport.gpx.IGpxReader;

import org.xmlpull.v1.XmlPullParserException;

import android.content.SharedPreferences;

import java.io.IOException;

public class ImportThreadHelper {
    private final GpxLoader mGpxLoader;
    private boolean mHasFiles;
    private final MessageHandlerInterface mMessageHandler;
    private final OldCacheFilesCleaner mOldCacheFilesCleaner;
    private final GeoBeagleEnvironment mGeoBeagleEnvironment;
    private final SharedPreferences mSharedPreferences;

    public ImportThreadHelper(GpxLoader gpxLoader, MessageHandlerInterface messageHandler,
            OldCacheFilesCleaner oldCacheFilesCleaner,
            SharedPreferences sharedPreferences,
            GeoBeagleEnvironment geoBeagleEnvironment) {
        mGpxLoader = gpxLoader;
        mMessageHandler = messageHandler;
        mHasFiles = false;
        mOldCacheFilesCleaner = oldCacheFilesCleaner;
        mSharedPreferences = sharedPreferences;
        mGeoBeagleEnvironment = geoBeagleEnvironment;
    }

    public void cleanup() {
        mMessageHandler.loadComplete();
    }

    public void end() throws ImportException {
        mGpxLoader.end();
        if (!mHasFiles
                && mSharedPreferences.getString(BCachingModule.BCACHING_USERNAME, "").length() == 0)
            throw new ImportException(R.string.error_no_gpx_files, mGeoBeagleEnvironment
                    .getImportFolder());
    }

    public boolean processFile(IGpxReader gpxReader) throws XmlPullParserException, IOException {
        String filename = gpxReader.getFilename();

        mHasFiles = true;
        mGpxLoader.open(filename, gpxReader.open());
        return mGpxLoader.load();
    }

    public void start() {
        mOldCacheFilesCleaner.clean();
        mGpxLoader.start();
    }

    public void startBCachingImport() {
        mMessageHandler.startBCachingImport();
    }
}
