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
import com.google.code.geobeagle.data.di.DestinationFactory;
import com.google.code.geobeagle.io.di.DatabaseDI.SQLiteWrapper;
import com.google.code.geobeagle.ui.ErrorDisplayer;

public class LocationSaver {
    private final CacheWriter mCacheWriter;
    private final Database mDatabase;
    private final DestinationFactory mDestinationFactory;
    private final SQLiteWrapper mSQLiteWrapper;

    public LocationSaver(Database database, DestinationFactory destinationFactory,
            ErrorDisplayer errorDisplayer, SQLiteWrapper sqliteWrapper, CacheWriter cacheWriter) {
        mDatabase = database;
        mDestinationFactory = destinationFactory;
        mSQLiteWrapper = sqliteWrapper;
        mCacheWriter = cacheWriter;
    }

    public void saveLocation(final CharSequence location) {
        mSQLiteWrapper.openWritableDatabase(mDatabase);
        // TODO: catch errors on open
        mCacheWriter.startWriting();
        Destination destination = mDestinationFactory.create(location);
        mCacheWriter.insertAndUpdateCache(destination.getId(), destination.getName(), destination
                .getLatitude(), destination.getLongitude(), "intent");
        mCacheWriter.stopWriting();
        mSQLiteWrapper.close();
    }

}
