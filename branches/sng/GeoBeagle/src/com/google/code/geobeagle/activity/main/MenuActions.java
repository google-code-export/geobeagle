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

package com.google.code.geobeagle.activity.main;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.cachelist.CacheList;
import com.google.code.geobeagle.activity.cachelist.actions.menu.MenuAction;
import com.google.code.geobeagle.activity.main.view.EditCacheActivity;
import com.google.code.geobeagle.activity.preferences.EditPreferences;
import com.google.code.geobeagle.activity.searchonline.SearchOnlineActivity;

import android.app.Activity;
import android.content.Intent;

public class MenuActions {

    public static class MenuActionCacheList implements MenuAction {
        private Activity mActivity;

        MenuActionCacheList(Activity activity) {
            mActivity = activity;
        }

        @Override
        public void act() {
            mActivity.startActivity(new Intent(mActivity, CacheList.class));
        }
    }

    public static class MenuActionEditGeocache implements MenuAction {
        private final GeoBeagle mParent;

        MenuActionEditGeocache(GeoBeagle parent) {
            mParent = parent;
        }

        @Override
        public void act() {
            Intent intent = new Intent(mParent, EditCacheActivity.class);
            intent.putExtra("geocache", mParent.getGeocache());
            mParent.startActivityForResult(intent, 0);
        }
    }

    public static class MenuActionLogDnf implements MenuAction {
        private final Activity mActivity;

        MenuActionLogDnf(Activity activity) {
            mActivity = activity;
        }

        @Override
        public void act() {
            mActivity.showDialog(R.id.menu_log_dnf);
        }

    }

    public static class MenuActionLogFind implements MenuAction {
        private final Activity mActivity;

        MenuActionLogFind(Activity activity) {
            mActivity = activity;
        }

        @Override
        public void act() {
            mActivity.showDialog(R.id.menu_log_find);
        }
    }

    public static class MenuActionSearchOnline implements MenuAction {
        private final Activity mActivity;

        MenuActionSearchOnline(Activity activity) {
            mActivity = activity;
        }

        @Override
        public void act() {
            mActivity.startActivity(new Intent(mActivity, SearchOnlineActivity.class));
        }
    }

    public static class MenuActionSettings implements MenuAction {
        private final Activity mActivity;

        MenuActionSettings(Activity activity) {
            mActivity = activity;
        }

        @Override
        public void act() {
            mActivity.startActivity(new Intent(mActivity, EditPreferences.class));
        }
    }

}
