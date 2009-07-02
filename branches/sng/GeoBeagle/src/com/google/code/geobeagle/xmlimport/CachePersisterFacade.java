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

import com.google.code.geobeagle.GeocacheFactory.Source;
import com.google.code.geobeagle.cachedetails.CacheDetailsWriter;
import com.google.code.geobeagle.xmlimport.CachePersisterFacadeDI.FileFactory;
import com.google.code.geobeagle.xmlimport.GpxImporterDI.MessageHandler;

import android.os.PowerManager.WakeLock;

import java.io.File;
import java.io.IOException;

public class CachePersisterFacade {
    public static final int WAKELOCK_DURATION = 5000;

    private final CacheDetailsWriter mCacheDetailsWriter;
    private String mCacheName = "";
    private final CacheTagWriter mCacheTagWriter;
    private final FileFactory mFileFactory;
    private final MessageHandler mMessageHandler;
    private final WakeLock mWakeLock;

    CachePersisterFacade(CacheTagWriter cacheTagWriter, FileFactory fileFactory,
            CacheDetailsWriter cacheDetailsWriter, MessageHandler messageHandler, WakeLock wakeLock) {
        mCacheTagWriter = cacheTagWriter;
        mFileFactory = fileFactory;
        mCacheDetailsWriter = cacheDetailsWriter;
        mMessageHandler = messageHandler;
        mWakeLock = wakeLock;
    }

    void close(boolean success) {
        mCacheTagWriter.stopWriting(success);
    }

    void end() {
        mCacheTagWriter.end();
    }

    void endCache(Source source) throws IOException {
        mMessageHandler.updateName(mCacheName);
        mCacheDetailsWriter.close();
        mCacheTagWriter.write(source);
    }

    boolean gpxTime(String gpxTime) {
        return mCacheTagWriter.gpxTime(gpxTime);
    }

    void groundspeakName(String text) {
        mCacheTagWriter.cacheName(text);
    }

    void hint(String text) throws IOException {
        mCacheDetailsWriter.writeHint(text);
    }

    void line(String text) throws IOException {
        mCacheDetailsWriter.writeLine(text);
    }

    void logDate(String text) throws IOException {
        mCacheDetailsWriter.writeLogDate(text);
    }

    void open(String path) {
        mMessageHandler.updateSource(path);
        mCacheTagWriter.startWriting();
        mCacheTagWriter.gpxName(path);
    }

    void start() {
        File file = mFileFactory.createFile(CacheDetailsWriter.GEOBEAGLE_DIR);
        file.mkdirs();
    }

    void startCache() {
        mCacheName = "";
        mCacheTagWriter.clear();
    }

    void symbol(String text) {
        mCacheTagWriter.symbol(text);
    }

    void wpt(String latitude, String longitude) {
        mCacheTagWriter.latitudeLongitude(latitude, longitude);
        mCacheDetailsWriter.latitudeLongitude(latitude, longitude);
    }

    void wptDesc(String cacheName) {
        mCacheName = cacheName;
        mCacheTagWriter.cacheName(cacheName);
    }

    void wptName(String wpt) throws IOException {
        mCacheDetailsWriter.open(wpt);
        mCacheDetailsWriter.writeWptName(wpt);
        mCacheTagWriter.id(wpt);
        mMessageHandler.updateWaypointId(wpt);
        mWakeLock.acquire(WAKELOCK_DURATION);
    }
}
