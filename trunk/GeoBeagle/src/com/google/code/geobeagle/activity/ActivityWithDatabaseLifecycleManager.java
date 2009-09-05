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

package com.google.code.geobeagle.activity;

import com.google.code.geobeagle.database.Closable;
import com.google.code.geobeagle.database.NullClosable;
import com.google.code.geobeagle.database.DatabaseDI.GeoBeagleSqliteOpenHelper;
import com.google.code.geobeagle.database.DatabaseDI.SQLiteWrapper;

public class ActivityWithDatabaseLifecycleManager {
    private final PausableWithDatabase mPausable;
    private final NullClosable mNullClosable;
    private Closable mClosable;
    private final GeoBeagleSqliteOpenHelper mGeoBeagleSqliteOpenHelper;

    public ActivityWithDatabaseLifecycleManager(PausableWithDatabase pausableWithDatabase,
            NullClosable nullClosable, GeoBeagleSqliteOpenHelper geoBeagleSqliteOpenHelper) {
        mPausable = pausableWithDatabase;
        mNullClosable = nullClosable;
        mGeoBeagleSqliteOpenHelper = geoBeagleSqliteOpenHelper;
    }

    public void onPause() {
        mPausable.onPause();
        mClosable.close();
        mClosable = mNullClosable;
    }

    public void onResume() {
        SQLiteWrapper mWritableDatabase = mGeoBeagleSqliteOpenHelper.getWritableSqliteWrapper();
        mClosable = mWritableDatabase;
        mPausable.onResume(mWritableDatabase);
    }
}
