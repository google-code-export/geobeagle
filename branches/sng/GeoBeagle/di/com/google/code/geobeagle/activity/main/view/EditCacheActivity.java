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

package com.google.code.geobeagle.activity.main.view;

import com.google.code.geobeagle.GeocacheFactory;
import com.google.code.geobeagle.activity.ActivityWithDatabaseLifecycleManager;
import com.google.code.geobeagle.activity.main.view.EditCacheActivityDelegate.CancelButtonOnClickListener;
import com.google.code.geobeagle.database.CacheWriterFactory;
import com.google.code.geobeagle.database.LocationSaverFactory;
import com.google.code.geobeagle.database.NullClosable;
import com.google.code.geobeagle.database.DatabaseDI.GeoBeagleSqliteOpenHelper;

import android.app.Activity;
import android.os.Bundle;

public class EditCacheActivity extends Activity {
    private EditCacheActivityDelegate mEditCacheActivityDelegate;
    private ActivityWithDatabaseLifecycleManager mActivityWithDatabaseLifecycleManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final CancelButtonOnClickListener cancelButtonOnClickListener = new CancelButtonOnClickListener(
                this);
        final GeocacheFactory geocacheFactory = new GeocacheFactory();
        final CacheWriterFactory cacheWriterFactory = new CacheWriterFactory();
        final LocationSaverFactory locationSaverFactory = new LocationSaverFactory(
                cacheWriterFactory);
        final GeoBeagleSqliteOpenHelper geoBeagleSqliteOpenHelper = new GeoBeagleSqliteOpenHelper(
                this);
        final NullClosable nullClosable = new NullClosable();

        mEditCacheActivityDelegate = new EditCacheActivityDelegate(this,
                cancelButtonOnClickListener, geocacheFactory, locationSaverFactory);
        mActivityWithDatabaseLifecycleManager = new ActivityWithDatabaseLifecycleManager(
                mEditCacheActivityDelegate, nullClosable, geoBeagleSqliteOpenHelper);
        mEditCacheActivityDelegate.onCreate(savedInstanceState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mActivityWithDatabaseLifecycleManager.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mActivityWithDatabaseLifecycleManager.onResume();
    }
}
