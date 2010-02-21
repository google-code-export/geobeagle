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

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.GeocacheFactory;
import com.google.code.geobeagle.Tags;
import com.google.code.geobeagle.activity.cachelist.GeocacheListController;
import com.google.code.geobeagle.activity.main.Util;
import com.google.code.geobeagle.database.DbFrontend;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class EditCache {
    public static class CancelButtonOnClickListener implements OnClickListener {
        private final Activity mActivity;
    
        public CancelButtonOnClickListener(Activity activity) {
            mActivity = activity;
        }
    
        public void onClick(View v) {
            mActivity.setResult(Activity.RESULT_CANCELED, null);
            mActivity.finish();
        }
    }

    public static class CacheSaverOnClickListener implements OnClickListener {
        private final Activity mActivity;
        private final EditCache mGeocacheView;
        private final DbFrontend mDbFrontend;
    
        public CacheSaverOnClickListener(Activity activity, EditCache editCache,
                DbFrontend dbFrontend) {
            mActivity = activity;
            mGeocacheView = editCache;
            mDbFrontend = dbFrontend;
        }
    
        public void onClick(View v) {
            final Geocache geocache = mGeocacheView.get();
            if (geocache.saveToDbIfNeeded(mDbFrontend))
                mDbFrontend.setGeocacheTag(geocache.getId(), Tags.LOCKED_FROM_OVERWRITING, true);
                
            final Intent i = new Intent();
            i.setAction(GeocacheListController.SELECT_CACHE);
            i.putExtra("geocacheId", geocache.getId());
            mActivity.setResult(Activity.RESULT_OK, i);
            mActivity.finish();
        }
    }

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
                .getSourceName(), mOriginalGeocache.getCacheType(), mOriginalGeocache
                .getDifficulty(), mOriginalGeocache.getTerrain(), mOriginalGeocache
                .getContainer());
    }

    public void set(Geocache geocache) {
        mOriginalGeocache = geocache;
        mId.setText(geocache.getId());
        mName.setText(geocache.getName());
        mLatitude.setText(Util.formatDegreesAsDecimalDegreesString(geocache.getLatitude()));
        mLongitude.setText(Util.formatDegreesAsDecimalDegreesString(geocache.getLongitude()));
        //mLatitude.setText(Double.toString(geocache.getLatitude()));
        //mLongitude.setText(Double.toString(geocache.getLongitude()));

        mLatitude.requestFocus();
    }
}