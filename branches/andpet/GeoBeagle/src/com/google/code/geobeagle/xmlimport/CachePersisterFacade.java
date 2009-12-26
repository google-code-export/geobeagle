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

import com.google.code.geobeagle.Tags;
import com.google.code.geobeagle.GeocacheFactory.Source;
import com.google.code.geobeagle.cachedetails.CacheDetailsWriter;
import com.google.code.geobeagle.xmlimport.FileFactory;
import com.google.code.geobeagle.xmlimport.GpxImporterDI.MessageHandler;

import android.os.PowerManager.WakeLock;
import java.io.File;
import java.io.IOException;

public class CachePersisterFacade {
    private final CacheDetailsWriter mCacheDetailsWriter;
    private String mCacheName = "";
    private final CacheTagSqlWriter mCacheTagWriter;
    private final FileFactory mFileFactory;
    private final MessageHandler mMessageHandler;
    private final WakeLock mWakeLock;
    private final String mUsername;

    static interface TextHandler {
        public void text(String fullpath) throws IOException;
    }

    TextHandler wptName = new TextHandler() {
        @Override
        public void text(String fullpath) throws IOException {
            mCacheDetailsWriter.open(fullpath);
            mCacheDetailsWriter.writeWptName(fullpath);
            mCacheTagWriter.id(fullpath);
            mMessageHandler.updateWaypointId(fullpath);
            mWakeLock.acquire(GpxLoader.WAKELOCK_DURATION);
        }
    };

    TextHandler wptDesc = new TextHandler() {
        @Override
        public void text(String fullpath) throws IOException {
            mCacheName = fullpath;
            mCacheTagWriter.cacheName(fullpath);
        }
    };

    TextHandler logDate = new TextHandler() {
        @Override
        public void text(String fullpath) throws IOException {
            mCacheDetailsWriter.writeLogDate(fullpath);
        }
    };

    TextHandler hint = new TextHandler() {
        @Override
        public void text(String fullpath) throws IOException {
            if (!fullpath.equals(""))
                mCacheDetailsWriter.writeHint(fullpath);
        }
    };
    
    TextHandler cacheType = new TextHandler() {
        @Override
        public void text(String fullpath) throws IOException {
            mCacheTagWriter.cacheType(fullpath);
        }
    };

    TextHandler difficulty = new TextHandler() {
        @Override
        public void text(String fullpath) throws IOException {
            mCacheTagWriter.difficulty(fullpath);
        }
    };

    TextHandler container = new TextHandler() {
        @Override
        public void text(String fullpath) throws IOException {
            mCacheTagWriter.container(fullpath);
        }
    };

    TextHandler terrain = new TextHandler() {
        @Override
        public void text(String fullpath) throws IOException {
            mCacheTagWriter.terrain(fullpath);
        }
    };

    TextHandler groundspeakName = new TextHandler() {
        @Override
        public void text(String fullpath) throws IOException {
            mCacheTagWriter.cacheName(fullpath);
        }
    };

    TextHandler placedBy = new TextHandler() {
        @Override
        public void text(String fullpath) throws IOException {
            boolean isMine = (!mUsername.equals("") && mUsername
                    .equalsIgnoreCase(fullpath));
            mCacheTagWriter.setTag(Tags.MINE, isMine);
        }
    };

    CachePersisterFacade(CacheTagSqlWriter cacheTagSqlWriter,
            FileFactory fileFactory, CacheDetailsWriter cacheDetailsWriter,
            MessageHandler messageHandler, WakeLock wakeLock, String username) {
        mCacheDetailsWriter = cacheDetailsWriter;
        mCacheTagWriter = cacheTagSqlWriter;
        mFileFactory = fileFactory;
        mMessageHandler = messageHandler;
        mWakeLock = wakeLock;
        mUsername = username;
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

    void line(String text) throws IOException {
        mCacheDetailsWriter.writeLine(text);
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

    public void setTag(int tag, boolean set) {
        mCacheTagWriter.setTag(tag, set);
    }

    void wpt(String latitude, String longitude) {
        mCacheTagWriter.latitudeLongitude(latitude, longitude);
        mCacheDetailsWriter.latitudeLongitude(latitude, longitude);
    }

}
