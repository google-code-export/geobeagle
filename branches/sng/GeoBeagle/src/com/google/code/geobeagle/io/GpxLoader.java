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
import com.google.code.geobeagle.io.Database.SQLiteWrapper;
import com.google.code.geobeagle.ui.ErrorDisplayer;

import org.xmlpull.v1.XmlPullParserException;

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

    public static final String GEOBEAGLE_DIR = "/sdcard/GeoBeagle";
    public static final String GPX_PATH = "/sdcard/caches.gpx";

    public static GpxLoader create(ErrorDisplayer errorDisplayer, Database database,
            SQLiteWrapper sqlite) {
        final CacheWriter cacheWriter = database.createCacheWriter(sqlite, errorDisplayer);
        final FileFactory fileFactory = new FileFactory();
        final GpxCaches gpxCaches = GpxCaches.create(errorDisplayer);
        return new GpxLoader(cacheWriter, fileFactory, gpxCaches, errorDisplayer);
    }

    private boolean mAbortLoad;
    private final CacheWriter mCacheWriter;
    private final FileFactory mFileFactory;
    private final GpxCaches mGpxCaches;

    public GpxLoader(CacheWriter cacheWriter, FileFactory fileFactory, GpxCaches gpxCaches,
            ErrorDisplayer errorDisplayer) {
        mCacheWriter = cacheWriter;
        mFileFactory = fileFactory;
        mAbortLoad = false;
        mGpxCaches = gpxCaches;
    }

    public void abortLoad() {
        mAbortLoad = true;
    }

    public void load(GpxImporter.MessageHandler messageHandler) {
        File file = mFileFactory.createFile(GpxLoader.GEOBEAGLE_DIR);
        file.mkdirs();

        mCacheWriter.clearCaches(GPX_PATH);
        mCacheWriter.startWriting();
        mAbortLoad = false;
        int nCache = 0;
        for (final Cache cache : mGpxCaches) {
            messageHandler.update(++nCache + ": " + cache.mName);
            if (!mCacheWriter.insertAndUpdateCache(cache.mId, cache.mName, cache.mLatitude,
                    cache.mLongitude, mGpxCaches.getSource())
                    || mAbortLoad)
                break;
        }
        mCacheWriter.stopWriting();
    }

    public void open() throws FileNotFoundException, XmlPullParserException, IOException {
        mGpxCaches.open(GPX_PATH);
    }
}
