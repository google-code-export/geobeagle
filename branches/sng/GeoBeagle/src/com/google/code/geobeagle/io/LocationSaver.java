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

import com.google.code.geobeagle.data.Destination;
import com.google.code.geobeagle.data.Destination.DestinationFactory;
import com.google.code.geobeagle.io.DatabaseFactory.CacheWriter;
import com.google.code.geobeagle.ui.ErrorDisplayer;

import android.database.sqlite.SQLiteDatabase;

public class LocationSaver {
    private final DatabaseFactory mDatabaseFactory;
    private final DestinationFactory mDestinationFactory;
    private final ErrorDisplayer mErrorDisplayer;

    public LocationSaver(DatabaseFactory databaseFactory, DestinationFactory destinationFactory,
            ErrorDisplayer errorDisplayer) {
        mDatabaseFactory = databaseFactory;
        mDestinationFactory = destinationFactory;
        mErrorDisplayer = errorDisplayer;
    }

    public void saveLocation(final CharSequence location) {
        SQLiteDatabase sqlite = mDatabaseFactory.openOrCreateCacheDatabase();
        if (sqlite != null) {
            CacheWriter cacheWriter = mDatabaseFactory.createCacheWriter(sqlite, mErrorDisplayer);
            cacheWriter.startWriting();
            Destination destination = mDestinationFactory.create(location);
            cacheWriter.write(destination.getFullId(), destination.getName(), destination
                    .getLatitude(), destination.getLongitude(), "intent");
            cacheWriter.stopWriting();
            sqlite.close();
        }
    }

}
