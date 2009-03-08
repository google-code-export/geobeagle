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

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.ui.ErrorDisplayer;

import org.xmlpull.v1.XmlPullParserException;

import android.database.sqlite.SQLiteException;

import java.io.FileNotFoundException;
import java.io.IOException;

public class GpxLoader {
    private final CachePersisterFacade mCachePersisterFacade;
    private final ErrorDisplayer mErrorDisplayer;
    private final GpxToCache mGpxToCache;

    public GpxLoader(GpxToCache gpxToCache, CachePersisterFacade cachePersisterFacade,
            ErrorDisplayer errorDisplayer) {
        mGpxToCache = gpxToCache;
        mCachePersisterFacade = cachePersisterFacade;
        mErrorDisplayer = errorDisplayer;
    }

    public void abort() {
        mGpxToCache.abort();
    }

    public boolean load() {
        boolean success = false;
        try {
            success = mGpxToCache.load();
        } catch (final SQLiteException e) {
            mErrorDisplayer.displayError(R.string.error_writing_cache, e.getMessage());
        } catch (XmlPullParserException e) {
            mErrorDisplayer.displayError(R.string.error_parsing_file, e.getMessage());
        } catch (IOException e) {
            mErrorDisplayer.displayError(R.string.error_reading_file, mGpxToCache.getSource());
        } finally {
            mCachePersisterFacade.close();
        }
        return success;
    }

    public void open(String path) throws FileNotFoundException, XmlPullParserException, IOException {
        mGpxToCache.open(path);
        mCachePersisterFacade.open(path);
    }

    public void start() {
        mCachePersisterFacade.start();
    }
}
