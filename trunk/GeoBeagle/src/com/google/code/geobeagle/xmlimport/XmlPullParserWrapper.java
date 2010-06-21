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

import android.util.Log;

import java.io.IOException;
import java.io.Reader;

public class XmlPullParserWrapper {
    private String mSource;
    private XmlPullParser mXmlPullParser;

    public XmlPullParserWrapper() {
    }

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
        Log.d("GeoBeagle", "XmlPullParserWrapper open: " + this + ", " + path + ", " + reader);
        XmlPullParser newPullParser = XmlPullParserFactory.newInstance().newPullParser();
        newPullParser.setInput(reader);
        mSource = path;
        mXmlPullParser = newPullParser;
    }
}