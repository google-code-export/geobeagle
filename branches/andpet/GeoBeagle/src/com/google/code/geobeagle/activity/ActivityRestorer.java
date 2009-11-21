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

import com.google.code.geobeagle.activity.ActivityDI.ActivityTypeFactory;
import com.google.code.geobeagle.activity.cachelist.CacheListActivity;
import com.google.code.geobeagle.activity.cachelist.GeocacheListController;
import com.google.code.geobeagle.activity.main.GeoBeagle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;

/** Invoked from SearchOnlineActivity to restore the last GeoBeagle activity */
public class ActivityRestorer {
    static class CacheListRestorer implements Restorer {
        private final Activity mActivity;

        public CacheListRestorer(Activity activity) {
            mActivity = activity;
        }

        @Override
        public void restore() {
            mActivity.startActivity(new Intent(mActivity, CacheListActivity.class));
            mActivity.finish();
        }

    }

    static class NullRestorer implements Restorer {
        @Override
        public void restore() {
        }
    }

    interface Restorer {
        void restore();
    }

    static class ViewCacheRestorer implements Restorer {
        private final Activity mActivity;
        private final SharedPreferences mSharedPreferences;

        public ViewCacheRestorer(SharedPreferences sharedPreferences, Activity activity) {
            mSharedPreferences = sharedPreferences;
            mActivity = activity;
        }

        @Override
        public void restore() {
            String id = mSharedPreferences.getString("geocacheId", "");
            final Intent intent = new Intent(mActivity, GeoBeagle.class);
            intent.putExtra("geocacheId", id).setAction(GeocacheListController.SELECT_CACHE);
            mActivity.startActivity(intent);
            mActivity.finish();
        }
    }

    private final ActivityTypeFactory mActivityTypeFactory;
    private final Restorer[] mRestorers;
    private final SharedPreferences mSharedPreferences;

    public ActivityRestorer(Activity activity,
            ActivityTypeFactory activityTypeFactory, SharedPreferences sharedPreferences) {
        mActivityTypeFactory = activityTypeFactory;
        mSharedPreferences = sharedPreferences;
        final NullRestorer nullRestorer = new NullRestorer();
        mRestorers = new Restorer[] {
                nullRestorer, new CacheListRestorer(activity), nullRestorer,
                new ViewCacheRestorer(sharedPreferences, activity)
        };
    }

    public void restore(int flags) {
        if ((flags & Intent.FLAG_ACTIVITY_NEW_TASK) == 0)
            return;
        final int iLastActivity = mSharedPreferences.getInt(ActivitySaver.LAST_ACTIVITY,
                ActivityType.NONE.toInt());
        final ActivityType activityType = mActivityTypeFactory.fromInt(iLastActivity);
        mRestorers[activityType.toInt()].restore();
    }
}
