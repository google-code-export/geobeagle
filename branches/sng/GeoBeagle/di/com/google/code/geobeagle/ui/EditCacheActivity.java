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

package com.google.code.geobeagle.ui;

import com.google.code.geobeagle.io.CacheWriter;
import com.google.code.geobeagle.io.Database;
import com.google.code.geobeagle.io.DatabaseDI;
import com.google.code.geobeagle.io.LocationSaver;
import com.google.code.geobeagle.io.DatabaseDI.SQLiteWrapper;
import com.google.code.geobeagle.mainactivity.GeocacheFactory;
import com.google.code.geobeagle.ui.EditCacheActivityDelegate.CancelButtonOnClickListener;

import android.app.Activity;
import android.os.Bundle;

public class EditCacheActivity extends Activity {
    private final EditCacheActivityDelegate mEditCacheActivityDelegate;

    public EditCacheActivity() {
        super();

        final SQLiteWrapper sqliteWrapper = new SQLiteWrapper(null);
        final CacheWriter cacheWriter = DatabaseDI.createCacheWriter(sqliteWrapper);
        final LocationSaver locationSaver = new LocationSaver(cacheWriter);
        final CancelButtonOnClickListener cancelButtonOnClickListener = new CancelButtonOnClickListener(
                this);
        final Database database = DatabaseDI.create(this);
        final GeocacheFactory geocacheFactory = new GeocacheFactory();
        mEditCacheActivityDelegate = new EditCacheActivityDelegate(this,
                cancelButtonOnClickListener, sqliteWrapper, database, locationSaver,
                geocacheFactory);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEditCacheActivityDelegate.onCreate(savedInstanceState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mEditCacheActivityDelegate.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mEditCacheActivityDelegate.onResume();
    }
}
