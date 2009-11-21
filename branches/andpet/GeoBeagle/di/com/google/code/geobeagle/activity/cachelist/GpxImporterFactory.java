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

package com.google.code.geobeagle.activity.cachelist;

import com.google.code.geobeagle.CacheTypeFactory;
import com.google.code.geobeagle.ErrorDisplayer;
import com.google.code.geobeagle.GeoFixProvider;
import com.google.code.geobeagle.database.CacheWriter;
import com.google.code.geobeagle.xmlimport.GpxImporter;
import com.google.code.geobeagle.xmlimport.GpxImporterDI;
import com.google.code.geobeagle.xmlimport.GpxImporterDI.MessageHandler;
import com.google.code.geobeagle.xmlimport.GpxToCache.Aborter;
import com.google.code.geobeagle.xmlimport.GpxToCacheDI.XmlPullParserWrapper;

import android.app.ListActivity;

public class GpxImporterFactory {

    private final Aborter mAborter;
    private final ErrorDisplayer mErrorDisplayer;
    private final GeoFixProvider mGeoFixProvider;
    private final ListActivity mListActivity;
    private final MessageHandler mMessageHandler;
    private final XmlPullParserWrapper mXmlPullParserWrapper;
    private final CacheTypeFactory mCacheTypeFactory;

    public GpxImporterFactory(Aborter aborter,
            ErrorDisplayer errorDisplayer,
            GeoFixProvider geoFixProvider, ListActivity listActivity,
            MessageHandler messageHandler, XmlPullParserWrapper xmlPullParserWrapper,
            CacheTypeFactory cacheTypeFactory) {
        mAborter = aborter;
        mErrorDisplayer = errorDisplayer;
        mGeoFixProvider = geoFixProvider;
        mListActivity = listActivity;
        mMessageHandler = messageHandler;
        mXmlPullParserWrapper = xmlPullParserWrapper;
        mCacheTypeFactory = cacheTypeFactory;
    }

    public GpxImporter create(CacheWriter cacheWriter) {
        return GpxImporterDI.create(mListActivity, mXmlPullParserWrapper, mErrorDisplayer,
                mGeoFixProvider, mAborter, mMessageHandler,
                cacheWriter, mCacheTypeFactory);
    }
}
