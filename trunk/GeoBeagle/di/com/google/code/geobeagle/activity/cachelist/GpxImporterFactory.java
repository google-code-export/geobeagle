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

import com.google.code.geobeagle.ErrorDisplayer;
import com.google.code.geobeagle.activity.cachelist.presenter.GeocacheListPresenter;
import com.google.code.geobeagle.database.CacheWriter;
import com.google.code.geobeagle.database.GpxWriter;
import com.google.code.geobeagle.xmlimport.GpxImporter;
import com.google.code.geobeagle.xmlimport.GpxImporterDI;
import com.google.code.geobeagle.xmlimport.CachePersisterFacadeDI.CachePersisterFacadeFactory;
import com.google.code.geobeagle.xmlimport.GpxImporterDI.MessageHandler;
import com.google.code.geobeagle.xmlimport.GpxToCache.Aborter;
import com.google.code.geobeagle.xmlimport.GpxToCacheDI.XmlPullParserWrapper;
import com.google.inject.Inject;
import com.google.inject.Injector;

import android.content.Context;

public class GpxImporterFactory {

    private final Aborter mAborter;
    private final CachePersisterFacadeFactory mCachePersisterFacadeFactory;
    private final ErrorDisplayer mErrorDisplayer;
    private final GeocacheListPresenter mGeocacheListPresenter;
    private final Context mContext;
    private final MessageHandler mMessageHandler;
    private final XmlPullParserWrapper mXmlPullParserWrapper;
    private final Injector mInjector;

    @Inject
    public GpxImporterFactory(Aborter aborter,
            CachePersisterFacadeFactory cachePersisterFacadeFactory, ErrorDisplayer errorDisplayer,
            GeocacheListPresenter geocacheListPresenter, Context context,
            MessageHandler messageHandler,
            XmlPullParserWrapper xmlPullParserWrapper, Injector injector) {
        mAborter = aborter;
        mCachePersisterFacadeFactory = cachePersisterFacadeFactory;
        mErrorDisplayer = errorDisplayer;
        mGeocacheListPresenter = geocacheListPresenter;
        mContext = context;
        mMessageHandler = messageHandler;
        mXmlPullParserWrapper = xmlPullParserWrapper;
        mInjector = injector;
    }

    public GpxImporter create(CacheWriter cacheWriter, GpxWriter gpxWriter) {
        return GpxImporterDI.create(mContext, mXmlPullParserWrapper, mErrorDisplayer,
                mGeocacheListPresenter, mAborter, mMessageHandler, mCachePersisterFacadeFactory,
                cacheWriter, gpxWriter, mInjector);
    }
}
