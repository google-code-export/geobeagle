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
import com.google.code.geobeagle.xmlimport.CachePersisterFacade;
import com.google.code.geobeagle.xmlimport.EventHandlerGpx;
import com.google.code.geobeagle.xmlimport.EventHelper;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;

import java.io.IOException;
import java.io.Reader;

public class DetailsReader {
    private final Activity mActivity;
    private final EventHandlerGpx mEventHandlerGpx;
    private final EventHelper mEventHelper;
    private final String mPath;
    private final Reader mReader;
    private final StringWriterWrapper mStringWriterWrapper;
    private XmlPullParser mXmlPullParserWrapper;

    public DetailsReader(Activity activity,
            Reader fileReader,
            String path,
            EventHelper eventHelper,
            EventHandlerGpx eventHandlerGpx,
            StringWriterWrapper stringWriterWrapper) {
        mActivity = activity;
        mPath = path;
        mEventHelper = eventHelper;
        mEventHandlerGpx = eventHandlerGpx;
        mReader = fileReader;
        mStringWriterWrapper = stringWriterWrapper;
    }

    public String read(CachePersisterFacade cachePersisterFacade) {
        try {
            mEventHelper.open(mPath, mEventHandlerGpx);
            XmlPullParser newPullParser = XmlPullParserFactory.newInstance().newPullParser();
            newPullParser.setInput(mReader);
            mXmlPullParserWrapper = newPullParser;
            int eventType;
            for (eventType = mXmlPullParserWrapper.getEventType(); eventType != XmlPullParser.END_DOCUMENT; eventType = mXmlPullParserWrapper
                    .next()) {
                mEventHelper.handleEvent(eventType, mEventHandlerGpx, cachePersisterFacade,
                        mXmlPullParserWrapper);
            }

            // Pick up END_DOCUMENT event as well.
            mEventHelper.handleEvent(eventType, mEventHandlerGpx, cachePersisterFacade,
                    newPullParser);

            return mStringWriterWrapper.getString();
        } catch (XmlPullParserException e) {
            return mActivity.getString(R.string.error_reading_details_file, mPath);
        } catch (IOException e) {
            return mActivity.getString(R.string.error_reading_details_file, mPath);
        }
    }
}
