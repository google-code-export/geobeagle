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

package com.google.code.geobeagle.activity.cachelist.actions.menu;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.actions.MenuActionBase;
import com.google.code.geobeagle.bcaching.ImportBCachingWorker;
import com.google.code.geobeagle.database.DbFrontend;
import com.google.inject.Inject;
import com.google.inject.Provider;

import android.util.Log;

public class MenuActionSyncBCaching extends MenuActionBase {

    private final Provider<ImportBCachingWorker> importBCachingWorkerProvider;
    private Abortable abortable;
    private ImportBCachingWorker importBCachingWorker;

    @Inject
    public MenuActionSyncBCaching(Provider<ImportBCachingWorker> importBCachingWorkerProvider,
            DbFrontend dbFrontend, Abortable abortable) {
        super(R.string.menu_sync_bcaching);
        this.importBCachingWorkerProvider = importBCachingWorkerProvider;
        this.abortable = abortable;
        Log.d("GeoBeagleDb", "Sync: " + dbFrontend);
    }

    static class FooThread extends Thread {
        @Override 
        public void run() {
            Log.d("GeoBeagle", "TEST THREAD about to sleep " + isAlive());
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.d("GeoBeagle", "TEST THREAD came back: " + isAlive());
        }
    }
    @Override
    public void act() {
        importBCachingWorker = importBCachingWorkerProvider.get();
        Log.d("GeoBeagle", "1 act thread liveness: " + importBCachingWorker.isAlive() + " " + importBCachingWorker.getName());
        importBCachingWorker.start();
        try {
            Log.d("GeoBeagle", "2 act thread liveness: " + importBCachingWorker.isAlive() + " " + importBCachingWorker.getName());
            importBCachingWorker.join();
            Log.d("GeoBeagle", "3 act thread liveness: " + importBCachingWorker.isAlive() + " " + importBCachingWorker.getName());
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d("GeoBeagle", "4 act thread liveness: " + importBCachingWorker.isAlive() + " " + importBCachingWorker.getName());
        abortable = importBCachingWorker;
    }

    public void abort() {
        Log.d("GeoBeagle", "GpxImport aborting");
        Log.d("GeoBeagle", "abort thread liveness: " + importBCachingWorker.isAlive() + " " + importBCachingWorker.getName());
        abortable.abort();
    }
}
