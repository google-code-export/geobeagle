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

package com.google.code.geobeagle.cachedetails.reader;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.cachedetails.StringWriterWrapper;
import com.google.code.geobeagle.xmlimport.EventHelper;
import com.google.inject.Inject;
import com.google.inject.Provider;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;

import java.io.IOException;
import java.io.Reader;

public class DetailsReader {
    private final Activity mActivity;
    private EventHelper mEventHelper;
    private String mPath;
    private Reader mReader;
    private final StringWriterWrapper mStringWriterWrapper;
    private final Provider<XmlPullParser> mXmlPullParserProvider;

    @Inject
    public DetailsReader(Activity activity,
            StringWriterWrapper stringWriterWrapper,
            Provider<XmlPullParser> xmlPullParserProvider) {
        mActivity = activity;
        mStringWriterWrapper = stringWriterWrapper;
        mXmlPullParserProvider = xmlPullParserProvider;
    }

    public void open(String path, EventHelper eventHelper, Reader reader) {
        mPath = path;
        mEventHelper = eventHelper;
        mReader = reader;
    }

    public String read() {
        try {
            XmlPullParser xmlPullParser = mXmlPullParserProvider.get();
            mEventHelper.open(mPath, xmlPullParser);
            xmlPullParser.setInput(mReader);
            int eventType;
            for (eventType = xmlPullParser.getEventType(); eventType != XmlPullParser.END_DOCUMENT; eventType = xmlPullParser
                    .next()) {
                mEventHelper.handleEvent(eventType);
            }

            // Pick up END_DOCUMENT event as well.
            mEventHelper.handleEvent(eventType);

            return mStringWriterWrapper.getString();
        } catch (XmlPullParserException e) {
            return mActivity.getString(R.string.error_reading_details_file, mPath);
        } catch (IOException e) {
            return mActivity.getString(R.string.error_reading_details_file, mPath);
        }
    }
}
