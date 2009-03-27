
package com.google.code.geobeagle.ui.di;

import com.google.code.geobeagle.io.CacheWriter;
import com.google.code.geobeagle.io.Database;
import com.google.code.geobeagle.io.LocationSaver;
import com.google.code.geobeagle.io.di.DatabaseDI;
import com.google.code.geobeagle.io.di.DatabaseDI.SQLiteWrapper;
import com.google.code.geobeagle.ui.EditCacheActivityDelegate;
import com.google.code.geobeagle.ui.EditCacheActivityDelegate.CancelButtonOnClickListener;

import android.app.Activity;
import android.os.Bundle;

public class EditCacheActivity extends Activity {
    private final EditCacheActivityDelegate mEditCacheActivityDelegate;

    public EditCacheActivity() {
        super();
        final Database database = DatabaseDI.create(this);

        final SQLiteWrapper sqliteWrapper = new SQLiteWrapper(null);
        final CacheWriter cacheWriter = DatabaseDI.createCacheWriter(sqliteWrapper);
        final LocationSaver locationSaver = new LocationSaver(database, sqliteWrapper,
                cacheWriter);
        final CancelButtonOnClickListener cancelButtonOnClickListener = new CancelButtonOnClickListener(
                this);
        mEditCacheActivityDelegate = new EditCacheActivityDelegate(this,
                cancelButtonOnClickListener, locationSaver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEditCacheActivityDelegate.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mEditCacheActivityDelegate.onResume();
    }
}
