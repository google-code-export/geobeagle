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
import com.google.code.geobeagle.activity.ActivityDI.ActivityTypeFactory;
import com.google.code.geobeagle.activity.cachelist.CacheList;
import com.google.code.geobeagle.activity.cachelist.GeocacheListController;
import com.google.code.geobeagle.activity.main.GeoBeagle;
import com.google.code.geobeagle.activity.main.GeocacheFromPreferencesFactory;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;

public class ActivityRestorer {
    private final Activity mActivity;
    private final GeocacheFromPreferencesFactory mGeocacheFromPreferencesFactory;
    private final ActivityTypeFactory mActivityTypeFactory;
    private final SharedPreferences mSharedPreferences;

    public ActivityRestorer(Activity activity,
            GeocacheFromPreferencesFactory geocacheFromPreferencesFactory,
            ActivityTypeFactory activityTypeFactory, SharedPreferences sharedPreferences) {
        mGeocacheFromPreferencesFactory = geocacheFromPreferencesFactory;
        mActivityTypeFactory = activityTypeFactory;
        mActivity = activity;
        mSharedPreferences = sharedPreferences;
    }

    public void restore(int flags) {
        if ((flags & Intent.FLAG_ACTIVITY_NEW_TASK) == 0)
            return;

        final int iLastActivity = mSharedPreferences.getInt(ActivitySaver.LAST_ACTIVITY,
                ActivityType.NONE.toInt());
        final ActivityType activityType = mActivityTypeFactory.fromInt(iLastActivity);
        Intent intent;
        switch (activityType) {
            case VIEW_CACHE:
                final Geocache geocache = mGeocacheFromPreferencesFactory
                        .create(mSharedPreferences);
                intent = new Intent(mActivity, GeoBeagle.class);
                intent.putExtra("geocache", geocache)
                        .setAction(GeocacheListController.SELECT_CACHE);
                break;
            case CACHE_LIST:
                intent = new Intent(mActivity, CacheList.class);
                break;
            case SEARCH_ONLINE:
                return;
            default:
                return;
        }
        mActivity.startActivity(intent);
        mActivity.finish();
    }
}
