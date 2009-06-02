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

package com.google.code.geobeagle.mainactivity.view;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.GeocacheFactory;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.cachelistactivity.GeocacheListController;
import com.google.code.geobeagle.database.Database;
import com.google.code.geobeagle.database.LocationSaver;
import com.google.code.geobeagle.database.DatabaseDI.SQLiteWrapper;
import com.google.code.geobeagle.mainactivity.Util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class EditCacheActivityDelegate {
    public static class CancelButtonOnClickListener implements OnClickListener {
        private final Activity mActivity;

        public CancelButtonOnClickListener(Activity activity) {
            mActivity = activity;
        }

        public void onClick(View v) {
            // TODO: replace magic number.
            mActivity.setResult(-1, null);
            mActivity.finish();
        }
    }

    public static class EditCache {
        private final GeocacheFactory mGeocacheFactory;
        private final EditText mId;
        private final EditText mLatitude;
        private final EditText mLongitude;
        private final EditText mName;
        private Geocache mOriginalGeocache;

        public EditCache(GeocacheFactory geocacheFactory, EditText id, EditText name,
                EditText latitude, EditText longitude) {
            mGeocacheFactory = geocacheFactory;
            mId = id;
            mName = name;
            mLatitude = latitude;
            mLongitude = longitude;
        }

        Geocache get() {
            return mGeocacheFactory.create(mId.getText(), mName.getText(), Util
                    .parseCoordinate(mLatitude.getText()), Util.parseCoordinate(mLongitude
                    .getText()), mOriginalGeocache.getSourceType(), mOriginalGeocache
                    .getSourceName());
        }

        void set(Geocache geocache) {
            mOriginalGeocache = geocache;
            mId.setText(geocache.getId());
            mName.setText(geocache.getName());
            mLatitude.setText(Util.formatDegreesAsDecimalDegreesString(geocache.getLatitude()));
            mLongitude.setText(Util.formatDegreesAsDecimalDegreesString(geocache.getLongitude()));

            mLatitude.requestFocus();
        }
    }

    public static class SetButtonOnClickListener implements OnClickListener {
        private final Activity mActivity;
        private final EditCache mGeocacheView;
        private final LocationSaver mLocationSaver;

        public SetButtonOnClickListener(Activity activity, EditCache editCache,
                LocationSaver locationSaver) {
            mActivity = activity;
            mGeocacheView = editCache;
            mLocationSaver = locationSaver;
        }

        public void onClick(View v) {
            Geocache geocache = mGeocacheView.get();
            mLocationSaver.saveLocation(geocache);
            Intent i = new Intent();
            i.setAction(GeocacheListController.SELECT_CACHE);
            i.putExtra("geocache", geocache);
            mActivity.setResult(0, i);
            mActivity.finish();
        }
    }

    private final CancelButtonOnClickListener mCancelButtonOnClickListener;
    private final Database mDatabase;
    private final GeocacheFactory mGeocacheFactory;
    private final LocationSaver mLocationSaver;
    private final Activity mParent;
    private final SQLiteWrapper mSqliteWrapper;

    public EditCacheActivityDelegate(Activity parent,
            CancelButtonOnClickListener cancelButtonOnClickListener, SQLiteWrapper sqliteWrapper,
            Database database, LocationSaver locationSaver, GeocacheFactory geocacheFactory) {
        mParent = parent;
        mCancelButtonOnClickListener = cancelButtonOnClickListener;
        mGeocacheFactory = geocacheFactory;
        mLocationSaver = locationSaver;
        mSqliteWrapper = sqliteWrapper;
        mDatabase = database;
    }

    public void onCreate(Bundle savedInstanceState) {
        mParent.setContentView(R.layout.cache_edit);
    }

    public void onPause() {
        mSqliteWrapper.close();
    }

    public void onResume() {
        mSqliteWrapper.openWritableDatabase(mDatabase);
        Intent intent = mParent.getIntent();
        Geocache geocache = intent.<Geocache> getParcelableExtra("geocache");

        EditCache editCache = new EditCache(mGeocacheFactory, (EditText)mParent
                .findViewById(R.id.edit_id), (EditText)mParent.findViewById(R.id.edit_name),
                (EditText)mParent.findViewById(R.id.edit_latitude), (EditText)mParent
                        .findViewById(R.id.edit_longitude));
        editCache.set(geocache);

        SetButtonOnClickListener setButtonOnClickListener = new SetButtonOnClickListener(mParent,
                editCache, mLocationSaver);
        ((Button)mParent.findViewById(R.id.edit_set)).setOnClickListener(setButtonOnClickListener);

        Button cancel = (Button)mParent.findViewById(R.id.edit_cancel);
        cancel.setOnClickListener(mCancelButtonOnClickListener);
    }
}