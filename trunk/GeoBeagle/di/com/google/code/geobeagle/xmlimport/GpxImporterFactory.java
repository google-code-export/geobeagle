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
import com.google.code.geobeagle.xmlimport.GpxImporterDI.ImportThreadWrapper;
import com.google.code.geobeagle.xmlimport.GpxImporterDI.MessageHandler;
import com.google.code.geobeagle.xmlimport.GpxImporterDI.ToastFactory;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

import android.content.Context;

public class GpxImporterFactory {

    private final Injector mInjector;

    @Inject
    public GpxImporterFactory(Injector injector) {
        mInjector = injector;
    }

    public GpxImporter create() {
        final ErrorDisplayer errorDisplayer = mInjector.getInstance(ErrorDisplayer.class);
        final ImportWakeLock importWakeLock = mInjector.getInstance(ImportWakeLock.class);

        final GeoBeagleEnvironment geoBeagleEnvironment = mInjector
                .getInstance(GeoBeagleEnvironment.class);
        final Provider<MessageHandler> messageHandlerProvider = mInjector
                .getProvider(MessageHandler.class);
        final CacheTagSqlWriter cacheTagSqlWriter = mInjector.getInstance(CacheTagSqlWriter.class);
        final ImportCacheActions importCacheActions = new ImportCacheActions(cacheTagSqlWriter,
                messageHandlerProvider.get(), importWakeLock, geoBeagleEnvironment);

        final GpxToCache gpxToCache = mInjector.getInstance(GpxToCache.class);
        final GpxLoader gpxLoader = new GpxLoader(importCacheActions, errorDisplayer, gpxToCache, importWakeLock);
        final ToastFactory toastFactory = new ToastFactory();
        final MessageHandler messageHandler = mInjector.getInstance(MessageHandler.class);
        final ImportThreadWrapper importThreadWrapper = mInjector
                .getInstance(ImportThreadWrapper.class);

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
