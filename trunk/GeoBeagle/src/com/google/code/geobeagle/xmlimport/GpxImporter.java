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
import com.google.code.geobeagle.xmlimport.GpxImporterDI.ImportThreadWrapper;
import com.google.code.geobeagle.xmlimport.GpxImporterDI.MessageHandler;
import com.google.code.geobeagle.xmlimport.GpxImporterDI.ToastFactory;

import android.content.Context;
import android.widget.Toast;

public class GpxImporter implements Abortable {

    private final ErrorDisplayer mErrorDisplayer;
    private final EventHandlers mEventHandlers;
    private final GpxLoader mGpxLoader;
    private final ImportThreadWrapper mImportThreadWrapper;
    private final Context mContext;
    private final MessageHandler mMessageHandler;
    private final ToastFactory mToastFactory;
    private final Pausable mGeocacheListPresenter;

    GpxImporter(Pausable geocacheListPresenter, GpxLoader gpxLoader, Context context,
            ImportThreadWrapper importThreadWrapper, MessageHandler messageHandler,
            ToastFactory toastFactory, EventHandlers eventHandlers, ErrorDisplayer errorDisplayer) {
        mContext = context;
        mGpxLoader = gpxLoader;
        mEventHandlers = eventHandlers;
        mImportThreadWrapper = importThreadWrapper;
        mMessageHandler = messageHandler;
        mErrorDisplayer = errorDisplayer;
        mToastFactory = toastFactory;
        mGeocacheListPresenter = geocacheListPresenter;
    }

    public void abort() {
        mMessageHandler.abortLoad();
        mGpxLoader.abort();
        if (mImportThreadWrapper.isAlive()) {
            mImportThreadWrapper.join();
            mToastFactory.showToast(mContext, R.string.import_canceled, Toast.LENGTH_SHORT);
        }
    }

    public void importGpxs(CacheListRefresh cacheListRefresh) {
        mGeocacheListPresenter.onPause();

        mImportThreadWrapper.open(cacheListRefresh, mGpxLoader, mEventHandlers, mErrorDisplayer);
        mImportThreadWrapper.start();
    }
}
