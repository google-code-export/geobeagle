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
import com.google.code.geobeagle.activity.cachelist.presenter.GeocacheListPresenter;
import com.google.code.geobeagle.activity.main.fieldnotes.Toaster;
import com.google.inject.Inject;
import com.google.inject.Injector;

import android.widget.Toast;

public class GpxImporter implements Abortable {

    private final ErrorDisplayer mErrorDisplayer;
    private final ImportThreadWrapper mImportThreadWrapper;
    private final MessageHandler mMessageHandler;
    private final Toaster mToaster;
    private final Pausable mGeocacheListPresenter;
    private final Injector mInjector;
    private final Aborter mAborter;

    GpxImporter(GeocacheListPresenter geocacheListPresenter,
            ImportThreadWrapper importThreadWrapper,
            MessageHandler messageHandler,
            Toaster toaster,
            ErrorDisplayer errorDisplayer,
            Aborter aborter,
            Injector injector) {
        mImportThreadWrapper = importThreadWrapper;
        mMessageHandler = messageHandler;
        mErrorDisplayer = errorDisplayer;
        mToaster = toaster;
        mGeocacheListPresenter = geocacheListPresenter;
        mAborter = aborter;
        mInjector = injector;
    }

    @Inject
    GpxImporter(Injector injector) {
        mAborter = injector.getInstance(Aborter.class);
        mImportThreadWrapper = injector.getInstance(ImportThreadWrapper.class);
        mMessageHandler = injector.getInstance(MessageHandler.class);
        mErrorDisplayer = injector.getInstance(ErrorDisplayer.class);
        mToaster = injector.getInstance(Toaster.class);
        mGeocacheListPresenter = injector.getInstance(GeocacheListPresenter.class);
        mInjector = injector;
    }

    @Override
    public void abort() {
        mMessageHandler.abortLoad();
        mAborter.abort();
        if (mImportThreadWrapper.isAlive()) {
            mImportThreadWrapper.join();
            mToaster.toast(R.string.import_canceled, Toast.LENGTH_SHORT);
        }
    }

    public void importGpxs() {
        mGeocacheListPresenter.onPause();

        mImportThreadWrapper.open(mErrorDisplayer, mInjector);
        mImportThreadWrapper.start();
    }
}
