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

package com.google.code.geobeagle.io;

import com.google.code.geobeagle.io.DatabaseDI.SQLiteWrapper;
import com.google.code.geobeagle.io.GpxImporterDI.MessageHandler;
import com.google.code.geobeagle.ui.ErrorDisplayer;

import android.app.Activity;

public class GpxLoaderDI {
    public static GpxLoader create(Activity activity, Database database,
            SQLiteWrapper sqliteWrapper, MessageHandler messageHandler,
            ErrorDisplayer errorDisplayer) {
        final CachePersisterFacade cachePersisterFacade = CachePersisterFacadeDI.create(activity,
                messageHandler, database, sqliteWrapper);
        final GpxToCache gpxToCache = GpxToCacheDI.create(activity, cachePersisterFacade);

        return new GpxLoader(gpxToCache, cachePersisterFacade, errorDisplayer);
    }
}
