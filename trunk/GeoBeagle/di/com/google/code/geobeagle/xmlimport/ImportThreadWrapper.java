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

import com.google.code.geobeagle.xmlimport.ImportThread.ImportThreadFactory;
import com.google.inject.Inject;
import com.google.inject.Injector;

import android.util.Log;

public class ImportThreadWrapper {
    private ImportThread mImportThread;
    private final ImportThreadFactory importThreadFactory;

    @Inject
    ImportThreadWrapper(ImportThreadFactory importThreadFactory) {
        this.importThreadFactory = importThreadFactory;
    }
    public boolean isAlive() {
        if (mImportThread != null)
            return mImportThread.isAliveHack();
        return false;
    }

    public void join() {
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

    public void open(Injector injector) {
        mImportThread = importThreadFactory.create(injector);
    }

    public void start() {
        if (mImportThread != null)
            mImportThread.start();
    }
}
