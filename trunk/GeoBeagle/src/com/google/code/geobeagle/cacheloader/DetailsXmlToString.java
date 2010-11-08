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

package com.google.code.geobeagle.cacheloader;

import com.google.code.geobeagle.cachedetails.StringWriterWrapper;
import com.google.code.geobeagle.xmlimport.EventDispatcher;
import com.google.inject.Provider;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.Reader;

class DetailsXmlToString {
    private final StringWriterWrapper mStringWriterWrapper;
    private final Provider<XmlPullParser> mXmlPullParserProvider;
    private final EventDispatcher mEventHelper;

    DetailsXmlToString(EventDispatcher eventDispatcher,
            StringWriterWrapper stringWriterWrapper,
            Provider<XmlPullParser> xmlPullParserProvider) {
        mStringWriterWrapper = stringWriterWrapper;
        mXmlPullParserProvider = xmlPullParserProvider;
        mEventHelper = eventDispatcher;
    }

    String read(Reader reader) throws XmlPullParserException,
            IOException {
        XmlPullParser xmlPullParser = mXmlPullParserProvider.get();
        xmlPullParser.setInput(reader);
        mEventHelper.open(xmlPullParser);
        int eventType;
        for (eventType = xmlPullParser.getEventType(); eventType != XmlPullParser.END_DOCUMENT; eventType = xmlPullParser
                .next()) {
            mEventHelper.handleEvent(eventType);
        }

        // Pick up END_DOCUMENT event as well.
        mEventHelper.handleEvent(eventType);

        return mStringWriterWrapper.getString();
    }
}
