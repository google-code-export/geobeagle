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

import com.google.inject.Inject;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;

import java.io.IOException;
import java.io.Reader;

public class GpxToCache {
    public static class Aborter {
        private static boolean mAborted = false;

        Aborter() {
            mAborted = false;
        }

        public void abort() {
            Log.d("GeoBeagle", this + ": aborting");
            mAborted = true;
        }

        public boolean isAborted() {
            return mAborted;
        }

        public void reset() {
            mAborted = false;
        }
    }

    @SuppressWarnings("serial")
    public static class CancelException extends Exception {
    }

    private final Aborter mAborter;
    private final XmlPullParserWrapper mXmlPullParserWrapper;
    private String mSource;
    private final FileAlreadyLoadedChecker mTestLocAlreadyLoaded;
    private String mFilename;

    @Inject
    GpxToCache(XmlPullParserWrapper xmlPullParserWrapper, Aborter aborter,
            FileAlreadyLoadedChecker fileAlreadyLoadedChecker) {
        mXmlPullParserWrapper = xmlPullParserWrapper;
        mAborter = aborter;
        mTestLocAlreadyLoaded = fileAlreadyLoadedChecker;
    }

    public void abort() {
        Log.d("GeoBeagle", "GpxToCache aborting");
        mAborter.abort();
    }

    public String getSource() {
        return mXmlPullParserWrapper.getSource();
    }

    /**
     * @param eventHelper
     * @return false if this file has already been loaded.
     * @throws XmlPullParserException
     * @throws IOException
     * @throws CancelException
     */
    public boolean load(EventHelper eventHelper) throws XmlPullParserException, IOException,
            CancelException {
        Log.d("GeoBeagle", this + ": GpxToCache: load");

        if (mTestLocAlreadyLoaded.isAlreadyLoaded(mSource)) {
            return true;
        }
        eventHelper.open(mFilename);
        int eventType;
        for (eventType = mXmlPullParserWrapper.getEventType(); eventType != XmlPullParser.END_DOCUMENT; eventType = mXmlPullParserWrapper
                .next()) {
//            Log.d("GeoBeagle", "event: " + eventType);
            if (mAborter.isAborted()) {
                Log.d("GeoBeagle", "isAborted: " + mAborter.isAborted());
                throw new CancelException();
            }
            // File already loaded.
            if (!eventHelper.handleEvent(eventType))
                return true;
        }

        // Pick up END_DOCUMENT event as well.
        eventHelper.handleEvent(eventType);
        return false;
    }

    public void open(String source, String filename, Reader reader) throws XmlPullParserException {
        mSource = source;
        mFilename = filename;
        mXmlPullParserWrapper.open(source, reader);
    }
}
