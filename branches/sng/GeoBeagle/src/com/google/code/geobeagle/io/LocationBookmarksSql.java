/*
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
import com.google.code.geobeagle.io.DatabaseFactory.CacheReader;
import com.google.code.geobeagle.io.DatabaseFactory.CacheWriter;
import com.google.code.geobeagle.ui.ErrorDisplayer;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class LocationBookmarksSql implements LifecycleManager {
    private final DatabaseFactory mDatabaseFactory;
    private final DescriptionsAndLocations mDescriptionsAndLocations;
    private final ErrorDisplayer mErrorDisplayer;
    private final DestinationFactory mDestinationFactory;

    public LocationBookmarksSql(DescriptionsAndLocations descriptionsAndLocations,
            DatabaseFactory databaseFactory, DestinationFactory destinationFactory,
            ErrorDisplayer errorDisplayer) {
        mDescriptionsAndLocations = descriptionsAndLocations;
        mErrorDisplayer = errorDisplayer;
        mDatabaseFactory = databaseFactory;
        mDestinationFactory = destinationFactory;
    }

    public DescriptionsAndLocations getDescriptionsAndLocations() {
        return mDescriptionsAndLocations;
    }

    public ArrayList<CharSequence> getLocations() {
        return mDescriptionsAndLocations.getPreviousLocations();
    }

    public void onPause(Editor editor) {
        saveBookmarks();
    }

    public void onResume(SharedPreferences preferences) {
        readBookmarks();
    }

    private void readBookmarks() {
        SQLiteDatabase sqlite = mDatabaseFactory.openOrCreateCacheDatabase(mErrorDisplayer);
        if (sqlite != null) {
            CacheReader cacheReader = mDatabaseFactory.createCacheReader(sqlite);
            if (cacheReader.open()) {
                readBookmarks(cacheReader);
                cacheReader.close();
            }
            sqlite.close();
        }
    }

    public void readBookmarks(CacheReader cacheReader) {
        mDescriptionsAndLocations.clear();
        do {
            saveLocation(cacheReader.getCache());
        } while (cacheReader.moveToNext());
    }

    private void saveBookmarks() {
        SQLiteDatabase sqlite = mDatabaseFactory.openOrCreateCacheDatabase(mErrorDisplayer);
        if (sqlite != null) {
            CacheWriter cacheWriter = mDatabaseFactory.createCacheWriter(sqlite, mErrorDisplayer);
            cacheWriter.startWriting();
            for (final CharSequence location : mDescriptionsAndLocations.getPreviousLocations()) {
                Destination destination = mDestinationFactory.create(location);
                final CharSequence id = destination.getFullId();
                if (!cacheWriter.write(id, destination.getName(), destination.getLatitude(), destination.getLongitude()))
                    break;
            }
            cacheWriter.stopWriting();
            sqlite.close();
        }
    }

    public void saveLocation(final CharSequence location) {
        mDescriptionsAndLocations.add(Destination.extractDescription(location), location);
    }

}
