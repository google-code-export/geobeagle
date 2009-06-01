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

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class GpxToCacheDI {

    public static class XmlPullParserWrapper {
        private String mSource;
        private XmlPullParser mXmlPullParser;

        public String getAttributeValue(String namespace, String name) {
            return mXmlPullParser.getAttributeValue(namespace, name);
        }

        public int getEventType() throws XmlPullParserException {
            return mXmlPullParser.getEventType();
        }

        public String getName() {
            return mXmlPullParser.getName();
        }

        public String getSource() {
            return mSource;
        }

        public String getText() {
            return mXmlPullParser.getText();
        }

        public int next() throws XmlPullParserException, IOException {
            return mXmlPullParser.next();
        }

        public void open(String path, Reader reader) throws XmlPullParserException {
            final XmlPullParser newPullParser = XmlPullParserFactory.newInstance().newPullParser();
            newPullParser.setInput(reader);
            mSource = path;
            mXmlPullParser = newPullParser;
        }

    }

    public static XmlPullParser createPullParser(String path) throws FileNotFoundException,
            XmlPullParserException {
        final XmlPullParser newPullParser = XmlPullParserFactory.newInstance().newPullParser();
        final Reader reader = new BufferedReader(new FileReader(path));
        newPullParser.setInput(reader);
        return newPullParser;
    }

}
