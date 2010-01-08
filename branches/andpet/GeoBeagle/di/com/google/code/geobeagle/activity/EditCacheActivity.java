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

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.GeocacheFactory;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.main.view.EditCache;
import com.google.code.geobeagle.database.DbFrontend;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class EditCacheActivity extends Activity {
    private DbFrontend mDbFrontend;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final EditCache.CancelButtonOnClickListener cancelButtonOnClickListener = new EditCache.CancelButtonOnClickListener(
                this);
        final GeocacheFactory geocacheFactory = new GeocacheFactory();
        mDbFrontend = new DbFrontend(this, geocacheFactory);
        setContentView(R.layout.cache_edit);
        final Intent intent = getIntent();
        final EditCache editCache = new EditCache(geocacheFactory,
                (EditText)findViewById(R.id.edit_id),
                (EditText)findViewById(R.id.edit_name),
                (EditText)findViewById(R.id.edit_latitude),
                (EditText)findViewById(R.id.edit_longitude));
        
        EditCache.CacheSaverOnClickListener cacheSaver = new EditCache.CacheSaverOnClickListener(
                this, editCache, mDbFrontend);
        ((Button)findViewById(R.id.edit_set)).setOnClickListener(cacheSaver);
        ((Button)findViewById(R.id.edit_cancel))
                .setOnClickListener(cancelButtonOnClickListener);

        Geocache geocache = mDbFrontend.loadCacheFromId(intent.getStringExtra("geocacheId"));
        editCache.set(geocache);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDbFrontend.closeDatabase();
    }
}
