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

import com.google.code.geobeagle.io.GpxLoader.Cache;
import com.google.code.geobeagle.ui.ErrorDisplayer;

import org.xmlpull.v1.XmlPullParserException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

public class GpxCaches implements Iterable<Cache> {
    public class CacheIterator implements Iterator<Cache> {
        private Cache mCache;

        // TODO: hasNext() has a side effect, and next() does not, which is
        // backwards.
        public boolean hasNext() {
            try {
                mCache = mGpxToCache.load();
                return mCache != null;
            } catch (XmlPullParserException e) {
                mErrorDisplayer.displayErrorAndStack(e);
                return false;
            } catch (IOException e) {
                mErrorDisplayer.displayErrorAndStack(e);
                return false;
            }
        }

        public Cache next() {
            return mCache;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public static GpxCaches create(ErrorDisplayer errorDisplayer, String path)
            throws XmlPullParserException, IOException, FileNotFoundException {
        final GpxToCache gpxToCache = GpxToCache.create(path);
        return new GpxCaches(gpxToCache, path, errorDisplayer);
    }
    
    private final ErrorDisplayer mErrorDisplayer;
    private final GpxToCache mGpxToCache;
    private final String mSource;

    public GpxCaches(GpxToCache gpxToCache, String source, ErrorDisplayer errorDisplayer)
            throws XmlPullParserException, IOException {
        mGpxToCache = gpxToCache;
        mErrorDisplayer = errorDisplayer;
        mSource = source;
    }

    public String getSource() {
        return mSource;
    }

    public Iterator<Cache> iterator() {
        return new CacheIterator();
    }
}
