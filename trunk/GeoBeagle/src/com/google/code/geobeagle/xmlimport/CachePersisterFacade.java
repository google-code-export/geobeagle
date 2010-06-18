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
import com.google.code.geobeagle.xmlimport.CachePersisterFacadeDI.FileFactory;
import com.google.code.geobeagle.xmlimport.XmlimportAnnotations.DetailsDirectory;
import com.google.inject.Inject;

import android.os.PowerManager.WakeLock;

import java.io.IOException;

public class CachePersisterFacade implements ICachePersisterFacade {
    private String mCacheName = "";
    private final CacheTagSqlWriter mCacheTagWriter;
    private final FileFactory mFileFactory;
    private MessageHandlerInterface mMessageHandler;
    private final WakeLock mWakeLock;
    private String mLastModified;
    private String mDetailsDirectory;

    @Inject
    CachePersisterFacade(CacheTagSqlWriter cacheTagSqlWriter, FileFactory fileFactory,
            MessageHandlerInterface messageHandler,
            WakeLock wakeLock, @DetailsDirectory String detailsDirectory) {
        mCacheTagWriter = cacheTagSqlWriter;
        mFileFactory = fileFactory;
        mMessageHandler = messageHandler;
        mWakeLock = wakeLock;
        mDetailsDirectory = detailsDirectory;
    }

    @Override
    public void cacheType(String text) {
        mCacheTagWriter.cacheType(text);
    }

    @Override
    public void close(boolean success) {
        mCacheTagWriter.stopWriting(success);
    }

    @Override
    public void container(String text) {
        mCacheTagWriter.container(text);
    }

    @Override
    public void difficulty(String text) {
        mCacheTagWriter.difficulty(text);
    }

    @Override
    public void end() {
        mCacheTagWriter.end();
    }

    @Override
    public void endCache(Source source) throws IOException {
        mMessageHandler.updateName(mCacheName);
        mCacheTagWriter.write(source);
    }

    @Override
    public boolean gpxTime(String gpxTime) {
        return mCacheTagWriter.gpxTime(gpxTime);
    }

    @Override
    public void groundspeakName(String text) {
        mCacheTagWriter.cacheName(text);
    }

    @Override
    public void hint(String text) throws IOException {
    }

    @Override
    public void line(String text) throws IOException {
    }

    @Override
    public void logDate(String text) throws IOException {
    }

    @Override
    public void open(String path) {
        mMessageHandler.updateSource(path);
        mCacheTagWriter.startWriting();
        mCacheTagWriter.gpxName(path);
    }

    @Override
    public void start() {
        mFileFactory.createFile(mDetailsDirectory).mkdirs();
    }

    @Override
    public void startCache() {
        mCacheName = "";
        mCacheTagWriter.clear();
    }

    @Override
    public void symbol(String text) {
        mCacheTagWriter.symbol(text);
    }

    @Override
    public void terrain(String text) {
        mCacheTagWriter.terrain(text);
    }

    @Override
    public void wpt(String latitude, String longitude) {
        mCacheTagWriter.latitudeLongitude(latitude, longitude);
    }

    @Override
    public void wptDesc(String cacheName) {
        mCacheName = cacheName;
        mCacheTagWriter.cacheName(cacheName);
    }

    @Override
    public void wptName(String wpt) throws IOException {
        mCacheTagWriter.id(wpt);
        mMessageHandler.updateWaypointId(wpt);
        mWakeLock.acquire(GpxLoader.WAKELOCK_DURATION);
    }

    @Override
    public void lastModified(String trimmedText) {
        mLastModified = trimmedText;
    }

    @Override
    public String getLastModified() {
        return mLastModified;
    }

    @Override
    public void archived(String attributeValue) {
        if (attributeValue != null)
            mCacheTagWriter.archived(attributeValue.equalsIgnoreCase("True"));
    }

    @Override
    public void available(String attributeValue) {
        if (attributeValue != null)
            mCacheTagWriter.available(attributeValue.equalsIgnoreCase("True"));
    }

    @Override
    public void logText(String trimmedText, boolean attributeValue) throws IOException {
    }

    @Override
    public void logType(String trimmedText) throws IOException {
    }

}
