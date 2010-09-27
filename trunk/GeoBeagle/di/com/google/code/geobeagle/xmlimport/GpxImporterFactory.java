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

import com.google.code.geobeagle.ErrorDisplayer;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh;
import com.google.code.geobeagle.activity.cachelist.presenter.GeocacheListPresenter;
import com.google.code.geobeagle.database.CacheWriter;
import com.google.code.geobeagle.database.GpxWriter;
import com.google.code.geobeagle.xmlimport.GpxImporterDI.ImportThreadWrapper;
import com.google.code.geobeagle.xmlimport.GpxImporterDI.MessageHandler;
import com.google.code.geobeagle.xmlimport.GpxImporterDI.ToastFactory;
import com.google.code.geobeagle.xmlimport.GpxToCache.Aborter;
import com.google.inject.Inject;
import com.google.inject.Injector;

import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

public class GpxImporterFactory {

    private final Injector mInjector;

    @Inject
    public GpxImporterFactory(Injector injector) {
        mInjector = injector;
    }

    public GpxImporter create() {
        final ErrorDisplayer errorDisplayer = mInjector.getInstance(ErrorDisplayer.class);
        final Context context = mInjector.getInstance(Context.class);
        final PowerManager powerManager = (PowerManager)context
                .getSystemService(Context.POWER_SERVICE);
        final WakeLock wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK,
                "Importing");

        final GeoBeagleEnvironment geoBeagleEnvironment = mInjector
                .getInstance(GeoBeagleEnvironment.class);
        final CachePersisterFacadeFactory cachePersisterFacadeFactory = mInjector
                .getInstance(CachePersisterFacadeFactory.class);
        final CacheWriter cacheWriter = mInjector.getInstance(CacheWriter.class);
        final GpxWriter gpxWriter = mInjector.getInstance(GpxWriter.class);
        final ImportCacheActions importCacheActions = cachePersisterFacadeFactory.create(
                cacheWriter, gpxWriter, wakeLock, geoBeagleEnvironment);

        final XmlPullParserWrapper xmlPullParserWrapper = mInjector
                .getInstance(XmlPullParserWrapper.class);
        final Aborter aborter = mInjector.getInstance(Aborter.class);
        final GpxLoader gpxLoader = GpxLoaderDI.create(importCacheActions, xmlPullParserWrapper,
                aborter, errorDisplayer, wakeLock, gpxWriter);
        final ToastFactory toastFactory = new ToastFactory();
        final MessageHandler messageHandler = mInjector.getInstance(MessageHandler.class);
        final ImportThreadWrapper importThreadWrapper = new ImportThreadWrapper(messageHandler,
                xmlPullParserWrapper, aborter);

        final EventHandlerComposite eventHandlerComposite = mInjector
                .getInstance(EventHandlerComposite.class);
        final GeocacheListPresenter geocacheListPresenter = mInjector
                .getInstance(GeocacheListPresenter.class);
        return new GpxImporter(geocacheListPresenter, gpxLoader,
                mInjector.getProvider(Context.class), importThreadWrapper, messageHandler,
                toastFactory, eventHandlerComposite, errorDisplayer,
                mInjector.getProvider(CacheListRefresh.class), mInjector);
    }
}
