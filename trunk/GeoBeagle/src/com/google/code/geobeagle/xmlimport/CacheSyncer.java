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
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListPresenter;
import com.google.code.geobeagle.activity.compass.fieldnotes.Toaster;
import com.google.code.geobeagle.xmlimport.ImportThread.ImportThreadFactory;
import com.google.inject.Inject;
import com.google.inject.Injector;

import android.util.Log;
import android.widget.Toast;

public class CacheSyncer {

    private final MessageHandler messageHandler;
    private final Toaster toaster;
    private final Pausable geocacheListPresenter;
    private final AbortState abortState;
    private final ImportThreadFactory importThreadFactory;
    private ImportThread importThread;

    CacheSyncer(CacheListPresenter cacheListPresenter,
            MessageHandler messageHandler,
            Toaster toaster,
            AbortState abortState,
            ImportThreadFactory importThreadFactory) {
        this.messageHandler = messageHandler;
        this.toaster = toaster;
        this.geocacheListPresenter = cacheListPresenter;
        this.abortState = abortState;
        this.importThreadFactory = importThreadFactory;
    }

    @Inject
    CacheSyncer(Injector injector) {
        abortState = injector.getInstance(AbortState.class);
        messageHandler = injector.getInstance(MessageHandler.class);
        toaster = injector.getInstance(Toaster.class);
        geocacheListPresenter = injector.getInstance(CacheListPresenter.class);
        importThreadFactory = injector.getInstance(ImportThreadFactory.class);
    }

    public void abort() {
        Log.d("GeoBeagle", "CacheSyncer:abort() " + isAlive());
        //TODO(sng): Why not use AbortState()?
        messageHandler.abortLoad();
        abortState.abort();
        if (isAlive()) {
            join();
            toaster.toast(R.string.import_canceled, Toast.LENGTH_SHORT);
        }
        Log.d("GeoBeagle", "CacheSyncer:abort() ending: " + isAlive());
    }

    boolean isAlive() {
        return importThread.isAliveHack();
    }

    void join() {
        try {
            while (isAlive()) {
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            // Ignore; we are aborting anyway.
        }
    }

    public void syncGpxs() {
        // TODO(sng): check when onResume is called.
        geocacheListPresenter.onPause();
        importThread = importThreadFactory.create();
        importThread.start();
    }
}
