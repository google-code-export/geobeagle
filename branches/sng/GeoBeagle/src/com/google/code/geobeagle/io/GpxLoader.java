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

package com.google.code.geobeagle.io;

import com.google.code.geobeagle.io.Database.CacheWriter;
import com.google.code.geobeagle.ui.ErrorDisplayer;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class GpxLoader {

    public static class Cache {
        public String mId;
        public double mLatitude;
        public double mLongitude;
        public String mName;

        public Cache() {
            mId = "";
            mName = "";
        }

        public Cache(String id, String name, double latitude, double longitude) {
            mId = id;
            mName = name;
            mLatitude = latitude;
            mLongitude = longitude;
        }
    }

    public static class FileFactory {
        public File createFile(String path) {
            return new File(path);
        }
    }

    public static final String GPX_PATH = "/sdcard/caches.gpx";

    public static GpxLoader create(Context context, ErrorDisplayer errorDisplayer,
            Database database, SQLiteDatabase sqlite) throws XmlPullParserException, IOException,
            FileNotFoundException {
        final CacheWriter cacheWriter = database.createCacheWriter(sqlite, errorDisplayer);
        final GpxCaches gpxCaches = GpxCaches.create(errorDisplayer, GPX_PATH);
        final FileFactory fileFactory = new FileFactory();

        return new GpxLoader(cacheWriter, gpxCaches, fileFactory);
    }

    private final CacheWriter mCacheWriter;
    private final FileFactory mFileFactory;
    private final GpxCaches mGpxCaches;
    private boolean mAbortLoad;
    public static final String GEOBEAGLE_DIR = "/sdcard/GeoBeagle";

    public GpxLoader(CacheWriter cacheWriter, GpxCaches gpxCaches, FileFactory fileFactory) {
        mCacheWriter = cacheWriter;
        mGpxCaches = gpxCaches;
        mFileFactory = fileFactory;
        mAbortLoad = false;
    }

    public void abortLoad() {
        mAbortLoad = true;
    }

    public void load(GpxImporter.CacheProgressUpdater cacheProgressUpdater) {
        File file = mFileFactory.createFile(GpxLoader.GEOBEAGLE_DIR);
        file.mkdirs();

        mCacheWriter.clear(GPX_PATH);
        mCacheWriter.startWriting();
        int nCache = 0;
        for (final Cache cache : mGpxCaches) {
            cacheProgressUpdater.update(++nCache + ": " + cache.mName);
            if (!mCacheWriter.write(cache.mId, cache.mName, cache.mLatitude, cache.mLongitude,
                    mGpxCaches.getSource())
                    || mAbortLoad)
                break;
        }
        mCacheWriter.stopWriting();
    }
}
