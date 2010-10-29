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
import com.google.inject.Provider;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;

import java.io.IOException;
import java.io.Reader;

public class GpxToCache {
    @SuppressWarnings("serial")
    public static class CancelException extends Exception {
    }

    private final Aborter mAborter;
    private final Provider<XmlPullParser> mXmlPullParserProvider;
    private String mSource;
    private final FileAlreadyLoadedChecker mTestLocAlreadyLoaded;
    private String mFilename;
    private XmlPullParser mXmlPullParser;
    private final EventHelper mEventHelper;
    private final EventHandlerComposite mEventHandlerComposite;

    @Inject
    GpxToCache(Provider<XmlPullParser> xmlPullParserProvider,
            Aborter aborter,
            FileAlreadyLoadedChecker fileAlreadyLoadedChecker,
            EventHandlerComposite eventHandlerComposite,
            EventHelper eventHelper) {
        mXmlPullParserProvider = xmlPullParserProvider;
        mAborter = aborter;
        mTestLocAlreadyLoaded = fileAlreadyLoadedChecker;
        mEventHandlerComposite = eventHandlerComposite;
        mEventHelper = eventHelper;
    }

    public void abort() {
        Log.d("GeoBeagle", "GpxToCache aborting");
        mAborter.abort();
    }

    public String getSource() {
        return mSource;
    }

    /**
     * @return false if this file has already been loaded.
     */
    public boolean load()
            throws XmlPullParserException, IOException, CancelException {
        Log.d("GeoBeagle", this + ": GpxToCache: load");

        mEventHelper.setEventHandler(mEventHandlerComposite);
        if (mTestLocAlreadyLoaded.isAlreadyLoaded(mSource)) {
            return true;
        }
        mEventHandlerComposite.start(mXmlPullParser);
        mEventHelper.open(mFilename);
        int eventType;
        for (eventType = mXmlPullParser.getEventType(); eventType != XmlPullParser.END_DOCUMENT; eventType = mXmlPullParser
                .next()) {
            // Log.d("GeoBeagle", "event: " + eventType);
            if (mAborter.isAborted()) {
                Log.d("GeoBeagle", "isAborted: " + mAborter.isAborted());
                throw new CancelException();
            }
            // File already loaded.
            if (!mEventHelper.handleEvent(eventType, mXmlPullParser))
                return true;
        }

        // Pick up END_DOCUMENT event as well.
        mEventHelper.handleEvent(eventType, mXmlPullParser);
        return false;
    }

    public void open(String source, String filename, Reader reader) throws XmlPullParserException {
        mSource = source;
        mFilename = filename;
        mXmlPullParser = mXmlPullParserProvider.get();
        mXmlPullParser.setInput(reader);
    }
}
