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

import com.google.code.geobeagle.io.CacheDetailsWriter.CacheDetailsWriterFactory;
import com.google.code.geobeagle.io.Database.CacheWriter;
import com.google.code.geobeagle.io.di.CachePersisterFacadeDI;
import com.google.code.geobeagle.io.di.GpxToCacheDI;
import com.google.code.geobeagle.io.di.HtmlWriterFactory;
import com.google.code.geobeagle.io.di.GpxImporterDI.MessageHandler;

import android.os.PowerManager.WakeLock;

import java.io.File;
import java.io.IOException;

public class CachePersisterFacade {

    public static class Cache {
        public String mId;
        public double mLatitude;
        public double mLongitude;
        public String mName;
        public String mSymbol;

        public Cache() {
            mId = "";
            mName = "";
            mSymbol = "";
        }

        public Cache(String id, String name, double latitude, double longitude) {
            mId = id;
            mName = name;
            mLatitude = latitude;
            mLongitude = longitude;
        }
    }

    public static final String GEOBEAGLE_DIR = "/sdcard/GeoBeagle";
    public static final int WAKELOCK_DURATION = 5000;

    private final Cache mCache;
    private int mCacheCount;
    private CacheDetailsWriter mCacheDetailsWriter;
    private final CacheDetailsWriterFactory mCacheDetailsWriterFactory;
    private final CacheWriter mCacheWriter;
    private final CachePersisterFacadeDI.FileFactory mFileFactory;
    private String mFilename;
    private final HtmlWriterFactory mHtmlWriterFactory;
    private MessageHandler mMessageHandler;
    private final WakeLock mWakeLock;

    public CachePersisterFacade(CacheWriter cacheWriter,
            CachePersisterFacadeDI.FileFactory fileFactory,
            CacheDetailsWriterFactory cacheDetailsWriterFactory,
            CacheDetailsWriter cacheDetailsWriter, HtmlWriterFactory htmlWriterFactory,
            MessageHandler messageHandler, Cache cache, WakeLock wakeLock) {
        mCacheWriter = cacheWriter;
        mFileFactory = fileFactory;
        mCacheDetailsWriterFactory = cacheDetailsWriterFactory;
        mCacheDetailsWriter = cacheDetailsWriter;
        mCache = cache;
        mHtmlWriterFactory = htmlWriterFactory;
        mMessageHandler = messageHandler;
        mWakeLock = wakeLock;
    }

    public void close() {
        mCacheWriter.stopWriting();
    }

    void endTag() throws IOException {
        mCacheDetailsWriter.writeEndTag();
        if (!mCache.mSymbol.equals("Geocache Found"))
            mCacheWriter.insertAndUpdateCache(mCache.mId, mCache.mName, mCache.mLatitude,
                    mCache.mLongitude);
    }

    void groundspeakName(String text) {
        mCache.mName = text;
    }

    public void hint(String text) throws IOException {
        mCacheDetailsWriter.writeHint(text);
    }

    void line(String text) throws IOException {
        mCacheDetailsWriter.writeLine(text);
    }

    void logDate(String text) throws IOException {
        mCacheDetailsWriter.writeLogDate(text);
    }

    void open(String text) {
        mFilename = text;
        mCacheWriter.clearCaches(text);
        mCacheWriter.startWriting();
    }

    void start() {
        File file = mFileFactory.createFile(GEOBEAGLE_DIR);
        file.mkdirs();
        mCacheCount = 0;
    }

    public void symbol(String text) {
        mCache.mSymbol = text;
    }

    void wpt(GpxToCacheDI.XmlPullParserWrapper mXmlPullParser) {
        mCache.mLatitude = Double.parseDouble(mXmlPullParser.getAttributeValue(null, "lat"));
        mCache.mLongitude = Double.parseDouble(mXmlPullParser.getAttributeValue(null, "lon"));
    }

    void wptName(String wpt) throws IOException {
        HtmlWriter htmlWriter = mHtmlWriterFactory.create(GEOBEAGLE_DIR + "/" + wpt + ".html");
        mCacheDetailsWriter = mCacheDetailsWriterFactory.create(htmlWriter);
        mCacheDetailsWriter.writeWptName(wpt, mCache.mLatitude, mCache.mLongitude);
        mCache.mId = wpt;
        mCacheCount++;
        mMessageHandler.workerSendUpdate(mCacheCount + ": " + mFilename + " - " + wpt + " - "
                + mCache.mName);
        mWakeLock.acquire(WAKELOCK_DURATION);
    }
}
