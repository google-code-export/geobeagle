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

import com.google.code.geobeagle.DescriptionsAndLocations;
import com.google.code.geobeagle.LifecycleManager;
import com.google.code.geobeagle.data.Destination;
import com.google.code.geobeagle.data.Destination.DestinationFactory;
import com.google.code.geobeagle.io.Database.CacheReader;
import com.google.code.geobeagle.io.Database.SQLiteWrapper;
import com.google.code.geobeagle.ui.ErrorDisplayer;

import android.app.ListActivity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.ArrayList;

public class LocationBookmarksSql implements LifecycleManager {
    private final Database mDatabase;
    private final DescriptionsAndLocations mDescriptionsAndLocations;
    private final SQLiteWrapper mSQLiteWrapper;

    public static LocationBookmarksSql create(ListActivity listActivity, Database database,
            DestinationFactory destinationFactory, ErrorDisplayer errorDisplayer) {
        final DescriptionsAndLocations descriptionsAndLocations = new DescriptionsAndLocations();
        final SQLiteWrapper sqliteWrapper = new SQLiteWrapper();
        return new LocationBookmarksSql(descriptionsAndLocations, database, sqliteWrapper,
                destinationFactory, errorDisplayer);
    }

    public LocationBookmarksSql(DescriptionsAndLocations descriptionsAndLocations,
            Database database, SQLiteWrapper sqliteWrapper, DestinationFactory destinationFactory,
            ErrorDisplayer errorDisplayer) {
        mDescriptionsAndLocations = descriptionsAndLocations;
        mDatabase = database;
        mSQLiteWrapper = sqliteWrapper;
    }

    public DescriptionsAndLocations getDescriptionsAndLocations() {
        return mDescriptionsAndLocations;
    }

    public ArrayList<CharSequence> getLocations() {
        return mDescriptionsAndLocations.getPreviousLocations();
    }

    public void onPause(Editor editor) {
    }

    public void onResume(SharedPreferences preferences) {
        readBookmarks();
    }

    private void readBookmarks() {
        mSQLiteWrapper.openReadableDatabase(mDatabase);
        CacheReader cacheReader = mDatabase.createCacheReader(mSQLiteWrapper);
        if (cacheReader.open()) {
            readBookmarks(cacheReader);
            cacheReader.close();
        }
        mSQLiteWrapper.close();
    }

    public void readBookmarks(CacheReader cacheReader) {
        mDescriptionsAndLocations.clear();
        do {
            final String location = cacheReader.getCache();
            mDescriptionsAndLocations.add(Destination.extractDescription(location), location);
        } while (cacheReader.moveToNext());
    }

}
