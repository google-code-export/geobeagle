
package com.google.code.geobeagle.io.di;

import com.google.code.geobeagle.io.CachePersisterFacade;
import com.google.code.geobeagle.io.Database;
import com.google.code.geobeagle.io.GpxLoader;
import com.google.code.geobeagle.io.GpxToCache;
import com.google.code.geobeagle.io.Database.SQLiteWrapper;
import com.google.code.geobeagle.ui.ErrorDisplayer;

import android.app.Activity;

public class GpxLoaderDI {

    public static GpxLoader create(Activity activity, Database database,
            SQLiteWrapper sqliteWrapper, GpxImporterDI.MessageHandler messageHandler,
            ErrorDisplayer errorDisplayer) {
        final CachePersisterFacade cachePersisterFacade = CachePersisterFacadeDI.create(activity,
                messageHandler, database, sqliteWrapper);
        final GpxToCache gpxToCache = GpxToCacheDI.create(activity, cachePersisterFacade);
        return new GpxLoader(gpxToCache, cachePersisterFacade, errorDisplayer);
    }
}
