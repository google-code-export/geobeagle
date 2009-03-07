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


import com.google.code.geobeagle.io.di.GpxToCacheDI;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.FileNotFoundException;
import java.io.IOException;

public class GpxToCache {
    private final EventHelper mEventHelper;
    private final GpxToCacheDI.XmlPullParserWrapper mXmlPullParserWrapper;
    private boolean mAbort;

    public GpxToCache(GpxToCacheDI.XmlPullParserWrapper xmlPullParserWrapper, EventHelper eventHelper) {
        mXmlPullParserWrapper = xmlPullParserWrapper;
        mEventHelper = eventHelper;
    }

    public void open(String source) throws FileNotFoundException, XmlPullParserException {
        mXmlPullParserWrapper.open(source);
        mAbort = false;
    }

    public void load() throws XmlPullParserException, IOException {
        int eventType;
        for (eventType = mXmlPullParserWrapper.getEventType(); !mAbort
                && eventType != XmlPullParser.END_DOCUMENT; eventType = mXmlPullParserWrapper
                .next()) {
            mEventHelper.handleEvent(eventType);
        }
        
        // Pick up END_DOCUMENT event as well.
        mEventHelper.handleEvent(eventType);
    }

    public String getSource() {
        return mXmlPullParserWrapper.getSource();
    }

    public void abort() {
        mAbort = true;
    }
}
