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

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.cachelist.Pausable;
import com.google.code.geobeagle.activity.cachelist.actions.menu.Abortable;
import com.google.code.geobeagle.activity.cachelist.presenter.GeocacheListPresenter;
import com.google.code.geobeagle.activity.main.fieldnotes.Toaster;
import com.google.code.geobeagle.xmlimport.ImportThread.ImportThreadFactory;
import com.google.inject.Inject;
import com.google.inject.Injector;

import android.util.Log;
import android.widget.Toast;

public class GpxImporter implements Abortable {

    private final MessageHandler mMessageHandler;
    private final Toaster mToaster;
    private final Pausable mGeocacheListPresenter;
    private final Aborter mAborter;
    private final ImportThreadFactory mImportThreadFactory;
    private ImportThread mImportThread;

    GpxImporter(GeocacheListPresenter geocacheListPresenter,
            MessageHandler messageHandler,
            Toaster toaster,
            Aborter aborter,
            ImportThreadFactory importThreadFactory) {
        mMessageHandler = messageHandler;
        mToaster = toaster;
        mGeocacheListPresenter = geocacheListPresenter;
        mAborter = aborter;
        mImportThreadFactory = importThreadFactory;
    }

    @Inject
    GpxImporter(Injector injector) {
        mAborter = injector.getInstance(Aborter.class);
        mMessageHandler = injector.getInstance(MessageHandler.class);
        mToaster = injector.getInstance(Toaster.class);
        mGeocacheListPresenter = injector.getInstance(GeocacheListPresenter.class);
        mImportThreadFactory = injector.getInstance(ImportThreadFactory.class);
    }

    @Override
    public void abort() {
        mMessageHandler.abortLoad();
        mAborter.abort();
        if (isAlive()) {
            join();
            mToaster.toast(R.string.import_canceled, Toast.LENGTH_SHORT);
        }
    }

    boolean isAlive() {
        if (mImportThread != null)
            return mImportThread.isAliveHack();
        return false;
    }

    void join() {
        if (mImportThread != null)
            try {
                while (!isAlive()) {
                    Log.d("GeoBeagle", "Sleeping while gpx import completes");
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                // Ignore; we are aborting anyway.
            }
    }

    public void importGpxs() {
        mGeocacheListPresenter.onPause();

        mImportThread = mImportThreadFactory.create();
        mImportThread.start();
    }
}
