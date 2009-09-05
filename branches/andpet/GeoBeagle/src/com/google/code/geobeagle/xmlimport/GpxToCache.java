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

import com.google.code.geobeagle.xmlimport.GpxToCacheDI.XmlPullParserWrapper;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.Reader;

public class GpxToCache {
    public static class Aborter {
        private static boolean mAborted = false;

        public Aborter() {
            mAborted = false;
        }

        public void abort() {
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

    GpxToCache(XmlPullParserWrapper xmlPullParserWrapper, Aborter aborter) {
        mXmlPullParserWrapper = xmlPullParserWrapper;
        mAborter = aborter;
    }

    public void abort() {
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
        int eventType;
        for (eventType = mXmlPullParserWrapper.getEventType(); eventType != XmlPullParser.END_DOCUMENT; eventType = mXmlPullParserWrapper
                .next()) {
            if (mAborter.isAborted())
                throw new CancelException();

            // File already loaded.
            if (!eventHelper.handleEvent(eventType))
                return true;
        }

        // Pick up END_DOCUMENT event as well.
        eventHelper.handleEvent(eventType);
        return false;
    }

    public void open(String source, Reader reader) throws XmlPullParserException, IOException {
        mXmlPullParserWrapper.open(source, reader);
    }
}
