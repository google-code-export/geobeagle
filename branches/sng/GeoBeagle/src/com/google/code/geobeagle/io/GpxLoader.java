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

import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class GpxLoader {
    public static class Factory {
        private final ErrorDisplayer mErrorDisplayer;
        private final Database mDatabase;

        public Factory(Database database, ErrorDisplayer errorDisplayer) {
            mDatabase = database;
            mErrorDisplayer = errorDisplayer;
        }

        public GpxLoader create(SQLiteDatabase sqlite) {
            return GpxLoader.create(mErrorDisplayer, mDatabase, sqlite);
        }
    }

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
            SQLiteDatabase sqlite) {
        final CacheWriter cacheWriter = database.createCacheWriter(sqlite, errorDisplayer);
        final FileFactory fileFactory = new FileFactory();

        return new GpxLoader(cacheWriter, fileFactory, null, errorDisplayer);
    }

    private boolean mAbortLoad;
    private final CacheWriter mCacheWriter;
    private ErrorDisplayer mErrorDisplayer;
    private final FileFactory mFileFactory;
    private GpxCaches mGpxCaches;

    public GpxLoader(CacheWriter cacheWriter, FileFactory fileFactory, GpxCaches gpxCaches,
            ErrorDisplayer errorDisplayer) {
        mCacheWriter = cacheWriter;
        mFileFactory = fileFactory;
        mErrorDisplayer = errorDisplayer;
        mAbortLoad = false;
        mGpxCaches = gpxCaches;
    }

    public void abortLoad() {
        mAbortLoad = true;
    }

    public void load(GpxImporter.MessageHandler messageHandler) {
        File file = mFileFactory.createFile(GpxLoader.GEOBEAGLE_DIR);
        file.mkdirs();

        mCacheWriter.clear(GPX_PATH);
        mCacheWriter.startWriting();
        int nCache = 0;
        for (final Cache cache : mGpxCaches) {
            messageHandler.update(++nCache + ": " + cache.mName);
            if (!mCacheWriter.write(cache.mId, cache.mName, cache.mLatitude, cache.mLongitude,
                    mGpxCaches.getSource())
                    || mAbortLoad)
                break;
        }
        mCacheWriter.stopWriting();
    }

    public void open() throws FileNotFoundException, XmlPullParserException, IOException {
        mGpxCaches = GpxCaches.create(mErrorDisplayer, GPX_PATH);
    }
}
