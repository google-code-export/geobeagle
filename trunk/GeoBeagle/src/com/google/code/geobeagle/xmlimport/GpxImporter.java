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
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.cachelist.Pausable;
import com.google.code.geobeagle.activity.cachelist.actions.menu.Abortable;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh;
import com.google.code.geobeagle.activity.cachelist.presenter.GeocacheListPresenter;
import com.google.code.geobeagle.xmlimport.GpxImporterDI.MessageHandler;
import com.google.code.geobeagle.xmlimport.GpxImporterDI.ToastFactory;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

import android.content.Context;
import android.widget.Toast;

public class GpxImporter implements Abortable {

    private final ErrorDisplayer mErrorDisplayer;
    private final ImportThreadWrapper mImportThreadWrapper;
    private final Provider<Context> mContextProvider;
    private final MessageHandler mMessageHandler;
    private final ToastFactory mToastFactory;
    private final Pausable mGeocacheListPresenter;
    private final Provider<CacheListRefresh> mCacheListRefreshProvider;
    private final Injector mInjector;
    private final Aborter mAborter;

    GpxImporter(GeocacheListPresenter geocacheListPresenter,
            Provider<Context> contextProvider,
            ImportThreadWrapper importThreadWrapper,
            MessageHandler messageHandler,
            ToastFactory toastFactory,
            ErrorDisplayer errorDisplayer,
            Provider<CacheListRefresh> cacheListRefreshProvider,
            Aborter aborter,
            Injector injector) {
        mContextProvider = contextProvider;
        mImportThreadWrapper = importThreadWrapper;
        mMessageHandler = messageHandler;
        mErrorDisplayer = errorDisplayer;
        mToastFactory = toastFactory;
        mGeocacheListPresenter = geocacheListPresenter;
        mCacheListRefreshProvider = cacheListRefreshProvider;
        mAborter = aborter;
        mInjector = injector;
    }

    @Inject
    GpxImporter(Injector injector) {
        mContextProvider = injector.getProvider(Context.class);
        mAborter = injector.getInstance(Aborter.class);
        mImportThreadWrapper = injector.getInstance(ImportThreadWrapper.class);
        mMessageHandler = injector.getInstance(MessageHandler.class);
        mErrorDisplayer = injector.getInstance(ErrorDisplayer.class);
        mToastFactory = injector.getInstance(ToastFactory.class);
        mGeocacheListPresenter = injector.getInstance(GeocacheListPresenter.class);
        mCacheListRefreshProvider = injector.getProvider(CacheListRefresh.class);
        mInjector = injector;
    }

    @Override
    public void abort() {
        mMessageHandler.abortLoad();
        mAborter.abort();
        if (mImportThreadWrapper.isAlive()) {
            mImportThreadWrapper.join();
            mToastFactory.showToast(mContextProvider.get(), R.string.import_canceled,
                    Toast.LENGTH_SHORT);
        }
    }

    public void importGpxs() {
        mGeocacheListPresenter.onPause();

        mImportThreadWrapper.open(mCacheListRefreshProvider.get(), mErrorDisplayer,
                mInjector);
        mImportThreadWrapper.start();
    }
}
