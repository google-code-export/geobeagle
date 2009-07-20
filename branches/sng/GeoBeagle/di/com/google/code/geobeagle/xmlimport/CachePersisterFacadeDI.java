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

import com.google.code.geobeagle.cachedetails.CacheDetailsWriter;
import com.google.code.geobeagle.cachedetails.HtmlWriter;
import com.google.code.geobeagle.cachedetails.WriterWrapper;
import com.google.code.geobeagle.database.CacheWriter;
import com.google.code.geobeagle.xmlimport.CacheTagWriter.CacheTagParser;
import com.google.code.geobeagle.xmlimport.GpxImporterDI.MessageHandler;

import android.app.Activity;
import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

import java.io.File;

public class CachePersisterFacadeDI {

    public static class FileFactory {
        public File createFile(String path) {
            return new File(path);
        }
    }

    public static CachePersisterFacade create(Activity activity, MessageHandler messageHandler,
            CacheWriter cacheWriter) {
        final FileFactory fileFactory = new FileFactory();
        final WriterWrapper writerWrapper = new WriterWrapper();
        final HtmlWriter htmlWriter = new HtmlWriter(writerWrapper);
        final CacheDetailsWriter cacheDetailsWriter = new CacheDetailsWriter(htmlWriter);
        final PowerManager powerManager = (PowerManager)activity
                .getSystemService(Context.POWER_SERVICE);
        final WakeLock wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK,
                "Importing");

        final CacheTagParser cacheTagParser = new CacheTagParser();
        final CacheTagWriter cacheTagWriter = new CacheTagWriter(cacheWriter, cacheTagParser);
        return new CachePersisterFacade(cacheTagWriter, fileFactory, cacheDetailsWriter,
                messageHandler, wakeLock);
    }

}
