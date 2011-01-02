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
import com.google.inject.Inject;
import com.google.inject.Provider;

import org.xmlpull.v1.XmlPullParser;

class DetailsXmlToStringFactory {
    private final StringWriterWrapper stringWriterWrapper;
    private final Provider<XmlPullParser> xmlPullParserProvider;

    @Inject
    DetailsXmlToStringFactory(StringWriterWrapper stringWriterWrapper,
            Provider<XmlPullParser> xmlPullParserProvider) {
        this.stringWriterWrapper = stringWriterWrapper;
        this.xmlPullParserProvider = xmlPullParserProvider;
    }

    DetailsXmlToString create(EventDispatcher eventDispatcher) {
        return new DetailsXmlToString(eventDispatcher, stringWriterWrapper, xmlPullParserProvider);
    }
}