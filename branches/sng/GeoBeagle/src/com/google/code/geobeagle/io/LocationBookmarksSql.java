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
import com.google.code.geobeagle.io.Database.CacheReader;
import com.google.code.geobeagle.io.Database.SQLiteWrapper;
import com.google.code.geobeagle.ui.ErrorDisplayer;

import java.util.ArrayList;

public class LocationBookmarksSql {
    private final Database mDatabase;
    private final Locations mLocations;
    private final SQLiteWrapper mSQLiteWrapper;
    private final LocationControl mLocationControl;
    private final CacheReader mCacheReader;
    private int mCount;

    public static LocationBookmarksSql create(LocationControl locationControl, Database database,
            DestinationFactory destinationFactory, ErrorDisplayer errorDisplayer) {
        final Locations locations = new Locations();
        final SQLiteWrapper sqliteWrapper = new SQLiteWrapper();
        final CacheReader cacheReader = CacheReader.create(sqliteWrapper);
        return new LocationBookmarksSql(cacheReader, locations, database,
                sqliteWrapper, destinationFactory, errorDisplayer, locationControl);
    }

    public LocationBookmarksSql(CacheReader cacheReader,
            Locations locations, Database database,
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
        mSQLiteWrapper.openReadableDatabase(mDatabase);

        if (mCacheReader.open(mLocationControl.getLocation())) {
            read();
            mCacheReader.close();
        }
        
        mCount = mCacheReader.getTotalCount();
        mSQLiteWrapper.close();
    }

    public void read() {
        mLocations.clear();
        do {
            mLocations.add(mCacheReader.getCache());
        } while (mCacheReader.moveToNext());
    }

    public int getCount() {
        return mCount;
    }

}
