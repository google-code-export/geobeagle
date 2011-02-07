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
import com.google.code.geobeagle.activity.cachelist.presenter.GeocacheListPresenter;
import com.google.code.geobeagle.activity.main.fieldnotes.Toaster;
import com.google.inject.Inject;
import com.google.inject.Injector;

import android.util.Log;
import android.widget.Toast;

public class CacheSyncer {

    private final MessageHandler messageHandler;
    private final Toaster toaster;
    private final Pausable geocacheListPresenter;
    private final Aborter aborter;
    private final ImportThread importThread;

    CacheSyncer(GeocacheListPresenter geocacheListPresenter,
            MessageHandler messageHandler,
            Toaster toaster,
            Aborter aborter,
            ImportThread importThread) {
        this.messageHandler = messageHandler;
        this.toaster = toaster;
        this.geocacheListPresenter = geocacheListPresenter;
        this.aborter = aborter;
        this.importThread = importThread;
    }

    @Inject
    CacheSyncer(Injector injector) {
        aborter = injector.getInstance(Aborter.class);
        messageHandler = injector.getInstance(MessageHandler.class);
        toaster = injector.getInstance(Toaster.class);
        geocacheListPresenter = injector.getInstance(GeocacheListPresenter.class);
        importThread = injector.getInstance(ImportThread.class);
    }

    public void abort() {
        Log.d("GeoBeagle", "CacheSyncer:abort() " + isAlive());
        messageHandler.abortLoad();
        aborter.abort();
        if (isAlive()) {
            join();
            toaster.toast(R.string.import_canceled, Toast.LENGTH_SHORT);
        }
        Log.d("GeoBeagle", "CacheSyncer:abort() ending: " + isAlive());
    }

    boolean isAlive() {
        if (importThread != null)
            return importThread.isAliveHack();
        return false;
    }

    void join() {
        if (importThread != null)
            try {
                while (isAlive()) {
                    Log.d("GeoBeagle", "Sleeping while gpx import completes");
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                // Ignore; we are aborting anyway.
            }
    }

    public void syncGpxs() {
        geocacheListPresenter.onPause();
        importThread.init();
        importThread.start();
    }
}
