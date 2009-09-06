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
 
package com.google.code.geobeagle.database;

import java.util.ArrayList;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.database.DatabaseDI;
import com.google.code.geobeagle.database.DatabaseDI.GeoBeagleSqliteOpenHelper;

/** Takes complete responsibility of opening and accessing a database to
 * load geocaches. */
public class GeocachesLoader {
	CacheReader mCacheReader;
	Context mContext;
	GeoBeagleSqliteOpenHelper open;
	GeocachesSql mGeocachesSql;
	boolean mIsDatabaseOpen;
	
	public GeocachesLoader(Context context) {
		mContext = context;
		mIsDatabaseOpen = false;
	}

	public void openDatabase() {
		if (mIsDatabaseOpen)
			return;
		Log.d("GeoBeagle", "GeocachesLoader.openDatabase()");
		mIsDatabaseOpen = true;
		
        open = new GeoBeagleSqliteOpenHelper(mContext);
        final SQLiteDatabase sqDb = open.getReadableDatabase();
        final ISQLiteDatabase database = new DatabaseDI.SQLiteWrapper(sqDb);
        DatabaseDI.createGeocachesSql(database);
		
        final Geocaches geocaches = new Geocaches();
        mCacheReader = DatabaseDI.createCacheReader(database);
        mGeocachesSql = new GeocachesSql(mCacheReader, geocaches);
	}
	
	public void closeDatabase() {
		if (!mIsDatabaseOpen)
			return;
		Log.d("GeoBeagle", "GeocachesLoader.closeDatabase()");
		mIsDatabaseOpen = false;
		
		open.close();
	}

    public ArrayList<Geocache> loadCaches(double latitude, double longitude, 
                                          WhereFactory whereFactory) {
    	openDatabase();
    	
    	CacheReaderCursor cursor = mCacheReader.open(latitude, longitude, whereFactory);
    	ArrayList<Geocache> geocaches = new ArrayList<Geocache>();
    	if (cursor != null) {
        	do {
        		geocaches.add(cursor.getCache());
        	} while (cursor.moveToNext());
    		cursor.close();
    	}
    	return geocaches;
    }
    
}
