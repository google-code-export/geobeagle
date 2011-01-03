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

import com.google.code.geobeagle.ErrorDisplayer;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.xmlimport.GpxToCache.CancelException;
import com.google.inject.Provider;

import org.xmlpull.v1.XmlPullParserException;

import android.database.sqlite.SQLiteException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;

public class GpxLoader {
    private final ErrorDisplayer errorDisplayer;
    private final GpxToCache gpxToCache;
    private final Provider<ImportWakeLock> importWakeLockProvider;
    public static final int WAKELOCK_DURATION = 15000;

    public GpxLoader(ErrorDisplayer errorDisplayer,
            GpxToCache gpxToCache,
            Provider<ImportWakeLock> importWakeLockProvider) {
        this.gpxToCache = gpxToCache;
        this.errorDisplayer = errorDisplayer;
        this.importWakeLockProvider = importWakeLockProvider;
    }

    public void end() {
        gpxToCache.end();
    }

    public void load(String path, Reader reader) throws CancelException {
        try {
            String filename = new File(path).getName();
            gpxToCache.open(path, filename, reader);

            importWakeLockProvider.get().acquire(WAKELOCK_DURATION);
            gpxToCache.load();
            return;
        } catch (SQLiteException e) {
            errorDisplayer.displayError(R.string.error_writing_cache, gpxToCache.getSource()
                    + ": " + e.getMessage());
        } catch (XmlPullParserException e) {
            errorDisplayer.displayError(R.string.error_parsing_file, gpxToCache.getSource()
                    + ": " + e.getMessage());
        } catch (FileNotFoundException e) {
            errorDisplayer.displayError(R.string.file_not_found, gpxToCache.getSource() + ": "
                    + e.getMessage());
        } catch (IOException e) {
            errorDisplayer.displayError(R.string.error_reading_file, gpxToCache.getSource()
                    + ": " + e.getMessage());
        } catch (CancelException e) {
        }

        throw new CancelException();
    }

    public void start() {
        gpxToCache.start();
    }
}
