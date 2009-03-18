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

import com.google.code.geobeagle.Locations;
import com.google.code.geobeagle.LocationControl;
import com.google.code.geobeagle.data.di.DestinationFactory;
import com.google.code.geobeagle.io.di.DatabaseDI;
import com.google.code.geobeagle.io.di.DatabaseDI.SQLiteWrapper;
import com.google.code.geobeagle.ui.ErrorDisplayer;

import java.util.ArrayList;

public class LocationBookmarksSql {
    private final Database mDatabase;
    private final Locations mLocations;
    private final SQLiteWrapper mSQLiteWrapper;
    private final LocationControl mLocationControl;
    private final CacheReader mCacheReader;

    public static LocationBookmarksSql create(LocationControl locationControl, Database database,
            DestinationFactory destinationFactory, ErrorDisplayer errorDisplayer) {
        final Locations locations = new Locations();
        final SQLiteWrapper sqliteWrapper = new SQLiteWrapper(null);
        final CacheReader cacheReader = DatabaseDI.createCacheReader(sqliteWrapper);
        return new LocationBookmarksSql(cacheReader, locations, database, sqliteWrapper,
                destinationFactory, errorDisplayer, locationControl);
    }

    public LocationBookmarksSql(CacheReader cacheReader, Locations locations, Database database,
            SQLiteWrapper sqliteWrapper, DestinationFactory destinationFactory,
            ErrorDisplayer errorDisplayer, LocationControl locationControl) {
        mLocations = locations;
        mDatabase = database;
        mSQLiteWrapper = sqliteWrapper;
        mLocationControl = locationControl;
        mCacheReader = cacheReader;
    }

    public Locations getDescriptionsAndLocations() {
        return mLocations;
    }

    public ArrayList<CharSequence> getLocations() {
        return mLocations.getPreviousLocations();
    }

    public void load() {
        // TODO: This has to be writable for upgrade to work; we should open one
        // readable and one writable at the activity level, and then pass it
        // down.
        mSQLiteWrapper.openWritableDatabase(mDatabase);
        if (mCacheReader.open(mLocationControl.getLocation())) {
            read();
            mCacheReader.close();
        }

        mSQLiteWrapper.close();
    }

    public void read() {
        mLocations.clear();
        do {
            mLocations.add(mCacheReader.getCache());
        } while (mCacheReader.moveToNext());
    }

    public int getCount() {
        mSQLiteWrapper.openWritableDatabase(mDatabase);
        int count = mCacheReader.getTotalCount();
        mSQLiteWrapper.close();
        return count;
    }

}
