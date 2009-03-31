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

import com.google.code.geobeagle.io.GpxToCacheDI.XmlPullParserWrapper;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class GpxToCache {
    @SuppressWarnings("serial")
    public static class CancelException extends Exception {
    }

    private boolean mAbort;
    private final EventHelper mEventHelper;
    private final XmlPullParserWrapper mXmlPullParserWrapper;

    GpxToCache(XmlPullParserWrapper xmlPullParserWrapper, EventHelper eventHelper) {
        mXmlPullParserWrapper = xmlPullParserWrapper;
        mEventHelper = eventHelper;
    }

    public void abort() {
        mAbort = true;
    }

    public String getSource() {
        return mXmlPullParserWrapper.getSource();
    }

    /**
     * @return false if this file has already been loaded.
     * @throws XmlPullParserException
     * @throws IOException
     * @throws CancelException
     */
    public boolean load() throws XmlPullParserException, IOException, CancelException {
        int eventType;
        for (eventType = mXmlPullParserWrapper.getEventType(); eventType != XmlPullParser.END_DOCUMENT; eventType = mXmlPullParserWrapper
                .next()) {
            if (mAbort)
                throw new CancelException();

            // File already loaded.
            if (!mEventHelper.handleEvent(eventType))
                return true;
        }

        // Pick up END_DOCUMENT event as well.
        mEventHelper.handleEvent(eventType);
        return false;
    }

    public void open(String source, Reader reader) throws XmlPullParserException, IOException {
        Reader bufferedReader = new BufferedReader(reader);
        mXmlPullParserWrapper.open(source, bufferedReader);
        mEventHelper.reset();
        mAbort = false;
    }
}
